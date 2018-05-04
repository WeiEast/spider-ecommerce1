package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantSimpleResult;
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
    public void afterPropertiesSet() throws Exception {
        List<AppCrawlerConfig> list = this.selectAll();

        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Map<String, Boolean>> map = list.stream().collect(Collectors.groupingBy(AppCrawlerConfig::getAppId, Collectors.toMap(AppCrawlerConfig::getProject, AppCrawlerConfig::getCrawlerStatus, (v1, v2) -> v2)));

            map.forEach((appId, configMap) -> {
                redisService.putMap(CACHE_PREFIX + appId, configMap);
            });
        }
    }

    private List<AppCrawlerConfig> selectAll() {
        AppCrawlerConfigCriteria criteria = new AppCrawlerConfigCriteria();
        return appCrawlerConfigDao.selectByExample(criteria);
    }

    private List<AppCrawlerConfig> selectListByAppId(String appId) {
        AppCrawlerConfigCriteria criteria = new AppCrawlerConfigCriteria();
        criteria.createCriteria().andAppIdEqualTo(appId);
        criteria.setOrderByClause("website_type desc");
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
        return value == null ? Boolean.TRUE.toString() : value.toString();
    }

    @Override
    public List<AppCrawlerConfigParam> getAppCrawlerConfigList() {
        MerchantResult<List<MerchantSimpleResult>> merchantResult = merchantBaseInfoFacade.querySimpleMerchantSimple(new BaseRequest());
        List<MerchantSimpleResult> appList = merchantResult.getData();
        logger.info("Merchant list: {}, size: {}", appList, appList.size());

        if (CollectionUtils.isEmpty(appList)) {
            return Collections.emptyList();
        }

        return appList.stream().map(merchantSimpleResult -> {
            AppCrawlerConfigParam appCrawlerConfigParam = getAppCrawlerConfigParamByAppId(merchantSimpleResult.getAppId());
            appCrawlerConfigParam.setAppName(merchantSimpleResult.getAppName());
            return appCrawlerConfigParam;
        }).sorted(Comparator.comparing(AppCrawlerConfigParam::getAppId)).collect(Collectors.toList());
    }

    @Override
    public AppCrawlerConfigParam getAppCrawlerConfigParamByAppId(String appId) {
        AppCrawlerConfigParam appCrawlerConfigParam = new AppCrawlerConfigParam();
        //set appId
        appCrawlerConfigParam.setAppId(appId);

        List<AppCrawlerConfig> configs = selectListByAppId(appId);
        Iterator<AppCrawlerConfig> iterator = configs.iterator();

        List<CrawlerProjectParam> projectConfigInfos = new ArrayList<>();
        List<String> projectNames = new ArrayList<>();

        Map<WebsiteType, List<BusinessType>> group = BusinessType.getGroup();
        group.forEach((websiteType, businessTypes) -> {
            Map<String, AppCrawlerConfig> map = new HashMap<>();

            while (iterator.hasNext()) {
                AppCrawlerConfig config = iterator.next();
                if (websiteType.getValue().equals(config.getWebsiteType())) {
                    map.put(uniqueKey(config.getWebsiteType(), config.getProject()), config);
                    iterator.remove();
                }
            }

            for (BusinessType businessType : businessTypes) {
                if (!businessType.isEnable()) {
                    map.remove(uniqueKey(businessType));
                    continue;
                }

                map.computeIfAbsent(uniqueKey(businessType), s -> {
                    AppCrawlerConfig conf = new AppCrawlerConfig();
                    conf.setAppId(appId);
                    conf.setWebsiteType(websiteType.getValue());
                    conf.setProject(businessType.getCode());
                    conf.setCrawlerStatus(businessType.isOpen());

                    return conf;
                });
            }

            List<ProjectParam> projects = new ArrayList<>();
            Collection<AppCrawlerConfig> list = map.values();
            for (AppCrawlerConfig config : list) {
                String project = config.getProject();
                BusinessType businessType = BusinessType.getBusinessType(project);
                if (businessType == null) {
                    continue;
                }

                ProjectParam projectParam = new ProjectParam();
                projectParam.setCode(project);
                projectParam.setName(businessType.getName());
                projectParam.setCrawlerStatus(config.getCrawlerStatus() ? 1 : 0);

                if (config.getCrawlerStatus()) {
                    projectNames.add(businessType.getName());
                }

                projects.add(projectParam);
            }

            logger.info("appId: {}, websiteType: {}, projects: {}", appId, websiteType.getType(), projects);

            CrawlerProjectParam crawlerProjectParam = new CrawlerProjectParam();
            crawlerProjectParam.setWebsiteType(Integer.valueOf(websiteType.getValue()));
            crawlerProjectParam.setProjects(projects);
            projectConfigInfos.add(crawlerProjectParam);
        });

        appCrawlerConfigParam.setProjectNames(projectNames);
        appCrawlerConfigParam.setProjectConfigInfos(projectConfigInfos);

        return appCrawlerConfigParam;
    }

    private String uniqueKey(String websiteType, String project) {
        return websiteType + "_" + project;
    }

    private String uniqueKey(BusinessType businessType) {
        return uniqueKey(businessType.getWebsiteType().getValue(), businessType.getCode());
    }

    @Override
    public void updateAppConfig(String appId, List<CrawlerProjectParam> projectConfigInfos) {
        if (StringUtils.isBlank(appId) || CollectionUtils.isEmpty(projectConfigInfos)) {
            throw new IllegalArgumentException("Incorrect parameters!");
        }

        try {
            distributedLocks.doInLock("crawler_business_control_setting_update", 3, TimeUnit.SECONDS, () -> {
                for (CrawlerProjectParam crawlerProjectParam : projectConfigInfos) {
                    List<ProjectParam> projectList = crawlerProjectParam.getProjects();

                    if (CollectionUtils.isEmpty(projectList)) {
                        continue;
                    }

                    for (ProjectParam projectParam : projectList) {
                        AppCrawlerConfig config = new AppCrawlerConfig();
                        config.setCrawlerStatus(projectParam.getCrawlerStatus() != 0);

                        AppCrawlerConfigCriteria criteria = new AppCrawlerConfigCriteria();
                        criteria.createCriteria().andAppIdEqualTo(appId).andWebsiteTypeEqualTo(String.valueOf(crawlerProjectParam.getWebsiteType())).andProjectEqualTo(projectParam.getCode());
                        int i = appCrawlerConfigDao.updateByExampleSelective(config, criteria);
                        if (i == 0) {
                            config.setAppId(appId);
                            config.setWebsiteType(String.valueOf(crawlerProjectParam.getWebsiteType()));
                            config.setProject(projectParam.getCode());
                            appCrawlerConfigDao.insertSelective(config);
                        }

                        try {
                            redisService.putMap(CACHE_PREFIX + appId, projectParam.getCode(), config.getCrawlerStatus());
                        } catch (Exception e) {
                            logger.warn("Error putting app business config into redis. appId: {}, businessType: {}, isCrawling: {}", appId, projectParam.getCode(), config.getCrawlerStatus(), e);
                        }
                    }
                }
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("The thread is interrupted unexpectedly", e);
        } catch (LockingFailureException e) {
            throw new UnexpectedException("其他人正在更新配置中，请稍后再试！");
        }
    }

}
