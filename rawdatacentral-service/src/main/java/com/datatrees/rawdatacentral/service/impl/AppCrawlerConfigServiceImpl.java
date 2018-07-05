package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.datatrees.crawler.core.domain.config.search.BusinessType;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.dao.AppCrawlerConfigDao;
import com.datatrees.rawdatacentral.domain.appconfig.AppCrawlerConfigParam;
import com.datatrees.rawdatacentral.domain.appconfig.CrawlerProjectParam;
import com.datatrees.rawdatacentral.domain.appconfig.ProjectParam;
import com.datatrees.rawdatacentral.domain.enums.WebsiteType;
import com.datatrees.rawdatacentral.domain.model.AppCrawlerConfig;
import com.datatrees.rawdatacentral.domain.model.AppCrawlerConfigCriteria;
import com.datatrees.rawdatacentral.service.AppCrawlerConfigService;
import com.datatrees.rawdatacentral.service.lock.DistributedLocks;
import com.datatrees.rawdatacentral.service.lock.LockingFailureException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.treefinance.crawler.exception.UnexpectedException;
import com.treefinance.saas.merchant.center.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.center.facade.result.console.AppBizLicenseSimpleResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantAppLicenseResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.service.MerchantBaseInfoFacade;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: yand
 * Date: 2018/4/10
 */
@Service
public class AppCrawlerConfigServiceImpl implements AppCrawlerConfigService, InitializingBean {

    private static final Logger                              logger       = LoggerFactory.getLogger(AppCrawlerConfigServiceImpl.class);
    private static final String                              CACHE_PREFIX = "com.treefinance.crawler.business.control.";
    private final        Cache<String, Map<String, Boolean>> localCache   = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES).softValues().build();
    @Resource
    private AppCrawlerConfigDao    appCrawlerConfigDao;
    @Resource
    private RedisService           redisService;
    @Resource
    private MerchantBaseInfoFacade merchantBaseInfoFacade;
    @Autowired
    private DistributedLocks       distributedLocks;

    @Override
    public void afterPropertiesSet() {
        // TODO: 2018/5/4 初期启动可以先异步缓存，数据量增大后需要优化改进
        new Thread(() -> {
            List<AppCrawlerConfig> list = selectAll();

            if (CollectionUtils.isNotEmpty(list)) {
                Map<String, Map<String, Boolean>> map = list.stream().filter(config -> StringUtils.isNotEmpty(config.getAppId())).collect(Collectors.groupingBy(AppCrawlerConfig::getAppId, Collectors.toMap(AppCrawlerConfig::getProject, AppCrawlerConfig::getCrawlerStatus, (v1, v2) -> v2)));

                map.forEach((appId, configMap) -> {
                    redisService.putMap(CACHE_PREFIX + appId, configMap);
                });
            }
            logger.info("app crawling business config was finished to load into cache.");
        }).start();
    }

    private List<AppCrawlerConfig> selectAll() {
        AppCrawlerConfigCriteria criteria = new AppCrawlerConfigCriteria();
        return appCrawlerConfigDao.selectByExample(criteria);
    }

    private List<AppCrawlerConfig> selectListByAppId(String appId) {
        AppCrawlerConfigCriteria criteria = new AppCrawlerConfigCriteria();
        criteria.createCriteria().andAppIdEqualTo(appId);
        criteria.setOrderByClause("website_type asc");
        return appCrawlerConfigDao.selectByExample(criteria);
    }

    @Override
    public String getFromRedis(String appId, String project) {
        Map<String, Boolean> result;
        try {
            result = localCache.get(appId, () -> {
                String redisKey = CACHE_PREFIX + appId;
                Map<String, Boolean> map = null;
                try {
                    map = redisService.getMap(redisKey);
                } catch (Exception e) {
                    logger.warn("Error getting hash from redis with key: {}", redisKey, e);
                }
                if (CollectionUtils.isEmpty(map)) {
                    List<AppCrawlerConfig> list = this.selectListByAppId(appId);
                    if (CollectionUtils.isNotEmpty(list)) {
                        map = list.stream().collect(Collectors.toMap(AppCrawlerConfig::getProject, AppCrawlerConfig::getCrawlerStatus, (o1, o2) -> o2));

                        try {
                            redisService.putMap(redisKey, map);
                        } catch (Exception e) {
                            logger.warn("Error putting hash into redis with key: {}", redisKey, e);
                        }
                    }
                }

                return map == null ? Collections.emptyMap() : map;
            });
        } catch (ExecutionException e) {
            throw new UncheckedExecutionException(e);
        }

        Boolean value = result.get(project);

        logger.debug("Actual crawling-business result: {}", value);

        return value == null ? Boolean.TRUE.toString() : value.toString();
    }

    @Override
    public List<AppCrawlerConfigParam> getAppCrawlerConfigList(List<MerchantAppLicenseResult> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return Collections.emptyList();
        }
        return appIds.parallelStream().map(this::getAppCrawlerConfigParamByAppId).collect(Collectors.toList());
    }

    private AppCrawlerConfigParam getAppCrawlerConfigParamByAppId(MerchantAppLicenseResult merchant) {
        if (logger.isDebugEnabled()) {
            logger.debug("查询业务标签，merchant: {}", JSON.toJSONString(merchant));
        }
        AppCrawlerConfigParam param = new AppCrawlerConfigParam(merchant.getAppId(), merchant.getAppName());
        List<AppBizLicenseSimpleResult> results = merchant.getAppBizLicenseResults();

        List<CrawlerProjectParam> projectConfigInfos = new ArrayList<>();
        List<String> projectNames = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(results)) {
            List<AppCrawlerConfig> configs = selectListByAppId(merchant.getAppId());
            logger.debug("已知的业务标签配置 size: {}", configs.size());
            for (AppBizLicenseSimpleResult result : results) {
                if (result.getIsValid() == 1) {
                    logger.debug("业务标签类型，bizType: {}, bizName: {}", result.getBizType(), result.getBizName());

                    WebsiteType websiteType = getWebsiteType(result.getBizType());
                    if (websiteType == null) continue;

                    List<BusinessType> businessTypes = BusinessType.getBusinessTypeList(websiteType);
                    if (CollectionUtils.isEmpty(businessTypes)) continue;

                    Map<String, ProjectParam> map = new HashMap<>();
                    Iterator<AppCrawlerConfig> iterator = configs.iterator();
                    while (iterator.hasNext()) {
                        AppCrawlerConfig config = iterator.next();
                        logger.debug("业务标签设置 websiteType: {}, project: {}, status: {}", config.getWebsiteType(), config.getProject(), config.getCrawlerStatus());
                        if (websiteType.val() == config.getWebsiteType()) {
                            BusinessType businessType = BusinessType.getBusinessType(config.getProject());
                            if (businessType == null || !businessType.isEnable()) {
                                iterator.remove();
                                continue;
                            }

                            ProjectParam projectParam = convertProjectParam(businessType, config.getCrawlerStatus());

                            map.put(uniqueKey(config.getWebsiteType(), projectParam.getCode()), projectParam);

                            iterator.remove();
                        }
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("已知的业务标签配置：{}", JSON.toJSONString(map));
                    }

                    for (BusinessType businessType : businessTypes) {
                        if (!businessType.isEnable()) {
                            continue;
                        }

                        map.computeIfAbsent(uniqueKey(businessType), s -> convertProjectParam(businessType, null));
                    }

                    List<ProjectParam> projects = map.values().stream().sorted(Comparator.comparing(ProjectParam::getOrder)).collect(Collectors.toList());
                    List<String> names = projects.stream().filter(projectParam -> projectParam.getCrawlerStatus() == 1).map(ProjectParam::getName).collect(Collectors.toList());
                    projectNames.addAll(names);

                    logger.debug("appId: {}, websiteType: {}, projects: {}", param.getAppId(), websiteType.getType(), projects);

                    projectConfigInfos.add(new CrawlerProjectParam(websiteType.val(), projects));
                }
            }
        }
        param.setProjectNames(projectNames);
        if (projectConfigInfos.isEmpty()) {
            param.setProjectConfigInfos(projectConfigInfos);
        } else {
            param.setProjectConfigInfos(projectConfigInfos.stream().sorted(Comparator.comparing(CrawlerProjectParam::getWebsiteType)).collect(Collectors.toList()));
        }

        return param;
    }

    private WebsiteType getWebsiteType(Byte bizType) {
        if (bizType == 2) {
            return WebsiteType.ECOMMERCE;
        } else if (bizType == 3) {
            return WebsiteType.OPERATOR;
        }
        return null;
    }

    private ProjectParam convertProjectParam(BusinessType businessType, Boolean open) {
        ProjectParam projectParam = new ProjectParam();
        projectParam.setCode(businessType.getCode());
        projectParam.setName(businessType.getName());
        boolean isOpen = open != null ? open : businessType.isOpen();
        projectParam.setCrawlerStatus((byte) (isOpen ? 1 : 0));
        projectParam.setOrder(businessType.getOrder());
        return projectParam;
    }

    private String uniqueKey(byte websiteType, String project) {
        return websiteType + "_" + project;
    }

    private String uniqueKey(BusinessType businessType) {
        return uniqueKey(businessType.getWebsiteType().val(), businessType.getCode());
    }

    @Override
    public void updateAppConfig(String appId, List<CrawlerProjectParam> projectConfigInfos) {
        if (StringUtils.isBlank(appId) || CollectionUtils.isEmpty(projectConfigInfos)) {
            throw new IllegalArgumentException("Incorrect parameters!");
        }
        logger.info("update crawling-business config >> appId: {}", appId);

        try {
            distributedLocks.doInLock("crawler_business_control_setting_update", 3, TimeUnit.SECONDS, () -> {
                Map<String, Object> map = new HashMap<>();
                for (CrawlerProjectParam crawlerProjectParam : projectConfigInfos) {
                    List<ProjectParam> projectList = crawlerProjectParam.getProjects();

                    if (CollectionUtils.isEmpty(projectList)) {
                        continue;
                    }

                    for (ProjectParam projectParam : projectList) {
                        AppCrawlerConfig config = new AppCrawlerConfig();
                        config.setCrawlerStatus(projectParam.getCrawlerStatus() != 0);

                        AppCrawlerConfigCriteria criteria = new AppCrawlerConfigCriteria();
                        criteria.createCriteria().andAppIdEqualTo(appId).andWebsiteTypeEqualTo(crawlerProjectParam.getWebsiteType()).andProjectEqualTo(projectParam.getCode());
                        int i = appCrawlerConfigDao.updateByExampleSelective(config, criteria);
                        if (i == 0) {
                            config.setAppId(appId);
                            config.setWebsiteType(crawlerProjectParam.getWebsiteType());
                            config.setProject(projectParam.getCode());
                            appCrawlerConfigDao.insertSelective(config);
                        }
                        map.put(projectParam.getCode(), config.getCrawlerStatus());
                    }
                }
                if (!map.isEmpty()) {
                    redisService.deleteKey(CACHE_PREFIX + appId);
                    logger.info("更新业务标签. appId: {}, 标签：{}", appId, JSON.toJSONString(map));
                    redisService.putMap(CACHE_PREFIX + appId, map);
                    localCache.invalidate(appId);
                }
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("The thread is interrupted unexpectedly", e);
        } catch (LockingFailureException e) {
            throw new UnexpectedException("其他人正在更新配置中，请稍后再试！");
        }
    }

}
