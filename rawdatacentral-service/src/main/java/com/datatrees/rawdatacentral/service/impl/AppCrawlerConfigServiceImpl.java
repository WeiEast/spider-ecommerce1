package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
        logger.info("redisKey : {}", redisKey);
        String result = redisService.getString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey);
        logger.info("result from redis : {}", result);
        if (StringUtils.isBlank(result)) {
            AppCrawlerConfig appCrawlerConfig = getOneAppCrawlerConfig(appId, project);
            if (appCrawlerConfig != null) {
                result = String.valueOf(appCrawlerConfig.getCrawlerStatus());
                redisService.saveString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey, result);
            }
        }
        return result;
    }

    @Override
    public List<AppCrawlerConfigParam> getAppCrawlerConfigList() {
        List<AppCrawlerConfigParam> resultList = new ArrayList<>();
        BaseRequest var1 = new BaseRequest();
        MerchantResult<List<MerchantSimpleResult>> merchantResult = merchantBaseInfoFacade.querySimpleMerchantSimple(var1);
        List<MerchantSimpleResult> appList = merchantResult.getData();
        logger.info("appList is {}", appList);

        if (CollectionUtils.isEmpty(appList)) {
            throw new RuntimeException("商户列表为null");
        }
        for (MerchantSimpleResult merchantSimpleResult : appList) {
            AppCrawlerConfigParam appCrawlerConfigParam = getOneAppCrawlerConfigParam(merchantSimpleResult.getAppId());
            appCrawlerConfigParam.setAppName(merchantSimpleResult.getAppName());
            resultList.add(appCrawlerConfigParam);
        }

        return resultList;
    }

    @Override
    public AppCrawlerConfigParam getOneAppCrawlerConfigParam(String appId) {
        AppCrawlerConfigParam result = new AppCrawlerConfigParam();
        List<String> projectNames = new ArrayList<>();
        List<CrawlerProjectParam> projectConfigInfos = new ArrayList<>();
        CrawlerProjectParam crawlerProjectParam = new CrawlerProjectParam();
        //set appId
        result.setAppId(appId);
        //遍历websiteId
        for (WebsiteType websiteType : WebsiteType.values()) {
            AppCrawlerConfigCriteria example = new AppCrawlerConfigCriteria();
            example.createCriteria().andAppIdEqualTo(appId).andWebsiteTypeEqualTo(websiteType.getValue());
            List<AppCrawlerConfig> appCrawlerConfigList = appCrawlerConfigDao.selectByExample(example);
            logger.info("appCrawlerConfigList size is {},WebsiteType is {},appId is {}", appCrawlerConfigList.size(), websiteType.getValue(), appId);
            if (CollectionUtils.isEmpty(appCrawlerConfigList)) {
                continue;
            }

            List<ProjectParam> projects = new ArrayList<>();
            crawlerProjectParam.setWebsiteType(Integer.valueOf(websiteType.getValue()));

            for (AppCrawlerConfig appCrawlerConfig : appCrawlerConfigList) {
                String project = appCrawlerConfig.getProject();
                String projectName = BusinessType.getBusinessType(project).getName();
                logger.info("appId is {},websiteType is {},project is {},projectname is {},crawlerStatus is {}", appCrawlerConfig.getAppId(), appCrawlerConfig.getWebsiteType(), project, projectName, appCrawlerConfig.getCrawlerStatus());
                ProjectParam projectParam = new ProjectParam();
                projectParam.setCode(project);
                projectParam.setName(projectName);
                projectParam.setCrawlerStatus(appCrawlerConfig.getCrawlerStatus());
                if (appCrawlerConfig.getCrawlerStatus()) {
                    projectNames.add(projectName);
                }
                projects.add(projectParam);
            }
            crawlerProjectParam.setProjects(projects);

        }
        projectConfigInfos.add(crawlerProjectParam);
        result.setProjectNames(projectNames);
        result.setProjectConfigInfos(projectConfigInfos);

        return result;
    }

    @Override
    public void addAppCrawlerConfig(AppCrawlerConfig param) {
        AppCrawlerConfig appCrawlerConfig = getOneAppCrawlerConfig(param.getAppId(), param.getProject());
        if (appCrawlerConfig == null) {
            //新增
            appCrawlerConfigDao.insertSelective(param);
            //存入redis
            String redisKey = appCrawlerConfig.getAppId() + separator + appCrawlerConfig.getProject();
            redisService.saveString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey, String.valueOf(appCrawlerConfig.getCrawlerStatus()));
        }
    }

    @Override
    public void updateAppCrawlerConfig(AppCrawlerConfig param) {
        appCrawlerConfigDao.updateByPrimaryKeySelective(param);
        String redisKey = param.getAppId() + separator + param.getProject();
        redisService.saveString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey, String.valueOf(param.getCrawlerStatus()));

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
    public int updateAppConfig(List<CrawlerProjectParam> params, String appId) {
        if (CollectionUtils.isEmpty(params)) {
            throw new RuntimeException("CrawlerProjectParamList is null");
        }
        AppCrawlerConfigCriteria example = new AppCrawlerConfigCriteria();
        for (CrawlerProjectParam crawlerProjectParam : params) {
            List<ProjectParam> projectList = crawlerProjectParam.getProjects();
            example.createCriteria().andAppIdEqualTo(appId).andWebsiteTypeEqualTo(String.valueOf(crawlerProjectParam.getWebsiteType()));
            List<AppCrawlerConfig> appCrawlerConfigList = appCrawlerConfigDao.selectByExample(example);
            //若数据库里没有记录
            if (CollectionUtils.isEmpty(appCrawlerConfigList)) {
                for (ProjectParam project : projectList) {
                    AppCrawlerConfig appCrawlerConfig = new AppCrawlerConfig();
                    appCrawlerConfig.setAppId(appId);
                    appCrawlerConfig.setCrawlerStatus(project.getCrawlerStatus());
                    appCrawlerConfig.setWebsiteType(String.valueOf(crawlerProjectParam.getWebsiteType()));
                    appCrawlerConfig.setProject(project.getCode());
                    addAppCrawlerConfig(appCrawlerConfig);
                }
                break;
            }
            //appCrawlerConfigList 不为null
            for (ProjectParam project : projectList) {
                for (AppCrawlerConfig elem : appCrawlerConfigList) {
                    if (project.getCode().equals(elem.getProject())) {
                        if (project.getCrawlerStatus() != elem.getCrawlerStatus()) {
                            elem.setCrawlerStatus(project.getCrawlerStatus());
                            updateAppCrawlerConfig(elem);
                        }
                    }

                }

            }

        }
        return 0;

    }
}
