package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.datatrees.crawler.core.domain.config.search.BusinessType;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.dao.AppCrawlerConfigDao;
import com.datatrees.rawdatacentral.domain.appconfig.AppCrawlerConfigParam;
import com.datatrees.rawdatacentral.domain.appconfig.CrawlerProjectParam;
import com.datatrees.rawdatacentral.domain.appconfig.ProjectParam;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.WebsiteType;
import com.datatrees.rawdatacentral.domain.model.AppCrawlerConfig;
import com.datatrees.rawdatacentral.domain.model.AppCrawlerConfigCriteria;
import com.datatrees.rawdatacentral.service.AppCrawlerConfigService;
import com.treefinance.saas.merchant.center.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantSimpleResult;
import com.treefinance.saas.merchant.center.facade.service.MerchantBaseInfoFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * User: yand
 * Date: 2018/4/10
 */
@Service
public class AppCrawlerConfigServiceImpl implements AppCrawlerConfigService, InitializingBean {

    private static final Logger logger    = LoggerFactory.getLogger(AppCrawlerConfigServiceImpl.class);
    private static final String separator = "_";
    @Resource
    private AppCrawlerConfigDao    appCrawlerConfigDao;
    @Resource
    private RedisService           redisService;
    @Resource
    private MerchantBaseInfoFacade merchantBaseInfoFacade;

    @Override
    public void afterPropertiesSet() throws Exception {
        AppCrawlerConfigCriteria appCrawlerConfigCriteria = new AppCrawlerConfigCriteria();
        List<AppCrawlerConfig> list = appCrawlerConfigDao.selectByExample(appCrawlerConfigCriteria);
        if (list != null && list.size() > 0) {
            for (AppCrawlerConfig appCrawlerConfig : list) {
                String redisKey = appCrawlerConfig.getAppId() + separator + appCrawlerConfig.getProject();
                redisService.saveString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey, String.valueOf(appCrawlerConfig.getCrawlerStatus()));
            }
        }
    }

    @Override
    public String getFromRedis(String appId, String project) {
        String redisKey = appId + separator + project;
        String result = redisService.getString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey);
        logger.info("result from redis : {},redisKey : {}", result, redisKey);
        if (StringUtils.isBlank(result)) {
            AppCrawlerConfig appCrawlerConfig = getOneAppCrawlerConfig(appId, project);
            if (appCrawlerConfig != null) {
                logger.info("result from db appCrawlerConfig is : {}", appCrawlerConfig);
                result = String.valueOf(appCrawlerConfig.getCrawlerStatus());
                redisService.saveString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey, result);
            }
        }
        return result;
    }

    @Override
    public List<AppCrawlerConfigParam> getAppCrawlerConfigList() {
        BaseRequest var1 = new BaseRequest();
        MerchantResult<List<MerchantSimpleResult>> merchantResult = merchantBaseInfoFacade.querySimpleMerchantSimple(var1);
        List<MerchantSimpleResult> appList = merchantResult.getData();
        logger.info("appList is {} ,applist size is {}", appList, appList.size());

        if (CollectionUtils.isEmpty(appList)) {
            throw new RuntimeException("商户列表为null");
        }

        return appList.stream().map(merchantSimpleResult -> {
            AppCrawlerConfigParam appCrawlerConfigParam = getOneAppCrawlerConfigParam(merchantSimpleResult.getAppId());
            appCrawlerConfigParam.setAppName(merchantSimpleResult.getAppName());
            return appCrawlerConfigParam;
        }).sorted(Comparator.comparing(AppCrawlerConfigParam::getAppId)).collect(Collectors.toList());
    }

    @Override
    public AppCrawlerConfigParam getOneAppCrawlerConfigParam(String appId) {
        AppCrawlerConfigParam appCrawlerConfigParam = new AppCrawlerConfigParam();
        List<String> projectNames = new ArrayList<>();
        List<CrawlerProjectParam> projectConfigInfos = new ArrayList<>();

        //set appId
        appCrawlerConfigParam.setAppId(appId);
        //遍历websiteId
        for (WebsiteType websiteType : WebsiteType.values()) {
            CrawlerProjectParam crawlerProjectParam = new CrawlerProjectParam();
            List<ProjectParam> projects = new ArrayList<>();
            AppCrawlerConfigCriteria example = new AppCrawlerConfigCriteria();
            example.createCriteria().andAppIdEqualTo(appId).andWebsiteTypeEqualTo(websiteType.getValue());
            List<AppCrawlerConfig> appCrawlerConfigList = appCrawlerConfigDao.selectByExample(example);

            if (CollectionUtils.isEmpty(appCrawlerConfigList)) {
                //如果参数为数据库里没值，说明是新增商户，目前只对website=2或者website=3做新增操作
                if ("2".equals(websiteType.getValue()) || "3".equals(websiteType.getValue())) {
                    for (BusinessType type : BusinessType.values()) {
                        if (type.getWebsiteType().equals(websiteType.getValue())) {
                            AppCrawlerConfig appCrawlerConfig = new AppCrawlerConfig();
                            appCrawlerConfig.setAppId(appId);
                            appCrawlerConfig.setWebsiteType(websiteType.getValue());
                            appCrawlerConfig.setProject(type.getCode());
                            //全部默认为爬取
                            appCrawlerConfig.setCrawlerStatus(true);
                            int result = appCrawlerConfigDao.insertSelective(appCrawlerConfig);
                            logger.info("appCrawlerConfig new add result is {}，appCrawlerConfig is {}", result,appCrawlerConfig);
                            //存入redis
                            String redisKey = appCrawlerConfig.getAppId() + separator + appCrawlerConfig.getProject();
                            logger.info("appCrawlerConfig new add redisKey is {}", redisKey);
                            redisService.saveString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey, String.valueOf(appCrawlerConfig.getCrawlerStatus()));
                            appCrawlerConfigList = appCrawlerConfigDao.selectByExample(example);
                            //再次判断数据是否新增成功
                            if (CollectionUtils.isEmpty(appCrawlerConfigList)) {
                                throw new RuntimeException("商户爬取信息新增失败");
                            }
                        }
                    }

                } else {
                    continue;
                }
            }

            crawlerProjectParam.setWebsiteType(Integer.valueOf(websiteType.getValue()));

            for (AppCrawlerConfig appCrawlerConfig : appCrawlerConfigList) {
                String project = appCrawlerConfig.getProject();
                String projectName = BusinessType.getBusinessType(project).getName();
                logger.info("appId is {},websiteType is {},project is {},projectname is {},crawlerStatus is {}", appCrawlerConfig.getAppId(), appCrawlerConfig.getWebsiteType(), project, projectName, appCrawlerConfig.getCrawlerStatus());
                ProjectParam projectParam = new ProjectParam();
                projectParam.setCode(project);
                projectParam.setName(projectName);
                if (appCrawlerConfig.getCrawlerStatus()) {
                    projectParam.setCrawlerStatus(1);
                } else {
                    projectParam.setCrawlerStatus(0);
                }
                if (appCrawlerConfig.getCrawlerStatus()) {
                    projectNames.add(projectName);
                }
                projects.add(projectParam);
            }
            crawlerProjectParam.setProjects(projects);
            projectConfigInfos.add(crawlerProjectParam);
        }

        appCrawlerConfigParam.setProjectNames(projectNames);
        appCrawlerConfigParam.setProjectConfigInfos(projectConfigInfos);

        return appCrawlerConfigParam;
    }

    @Override
    public AppCrawlerConfig getOneAppCrawlerConfig(String appId, String project) {
        AppCrawlerConfig result = new AppCrawlerConfig();
        AppCrawlerConfigCriteria example = new AppCrawlerConfigCriteria();
        example.createCriteria().andAppIdEqualTo(appId).andProjectEqualTo(project);
        List<AppCrawlerConfig> appCrawlerConfigList = appCrawlerConfigDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(appCrawlerConfigList)) {
            result = appCrawlerConfigList.get(0);
        }
        return result;
    }

    @Override
    public void updateAppConfig(List<CrawlerProjectParam> projectConfigInfos, String appId) {
        if (CollectionUtils.isEmpty(projectConfigInfos)) {
            throw new RuntimeException("CrawlerProjectParamList is null");
        }

        for (CrawlerProjectParam crawlerProjectParam : projectConfigInfos) {
            List<ProjectParam> projectList = crawlerProjectParam.getProjects();
            AppCrawlerConfigCriteria example = new AppCrawlerConfigCriteria();
            example.createCriteria().andAppIdEqualTo(appId).andWebsiteTypeEqualTo(String.valueOf(crawlerProjectParam.getWebsiteType()));
            List<AppCrawlerConfig> appCrawlerConfigList = appCrawlerConfigDao.selectByExample(example);
            //若数据库里没有记录 做新增操作
            if (CollectionUtils.isEmpty(appCrawlerConfigList)) {
                throw new RuntimeException("商户数据异常");
            }
            for (ProjectParam project : projectList) {
                for (AppCrawlerConfig elem : appCrawlerConfigList) {
                    if (project.getCode().equals(elem.getProject())) {
                        boolean crawlerStatus;
                        if (project.getCrawlerStatus() == 0) {
                            crawlerStatus = false;
                        } else {
                            crawlerStatus = true;
                        }
                        if (crawlerStatus != elem.getCrawlerStatus()) {
                            elem.setCrawlerStatus(crawlerStatus);
                            elem.setUpdatedAt(null);
                            appCrawlerConfigDao.updateByPrimaryKeySelective(elem);
                            String redisKey = elem.getAppId() + separator + elem.getProject();
                            redisService.saveString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey, String.valueOf(elem.getCrawlerStatus()));
                        }
                    }

                }

            }

        }

    }

}
