package com.datatrees.rawdatacentral.service;

import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.domain.appconfig.AppCrawlerConfigParam;
import com.datatrees.rawdatacentral.domain.appconfig.CrawlerProjectParam;
import com.datatrees.rawdatacentral.domain.model.AppCrawlerConfig;

/**
 * User: yand
 * Date: 2018/4/10
 */
public interface AppCrawlerConfigService {

    /**
     * 从redis中获取
     * @return
     */
    String getFromRedis(String appId, String project);

    /**
     * 获取所有商户信息
     * @return
     */
    List<AppCrawlerConfigParam> getAppCrawlerConfigList();

    /**
     * 获取一个商户的爬取信息
     * @param appId
     * @return
     */
    AppCrawlerConfigParam getOneAppCrawlerConfigParam(String appId);

    /**
     * 修改商户配置信息
     * @param params
     * @param appId
     */
    int updateAppConfig(List<CrawlerProjectParam> params, String appId);

    void addAppCrawlerConfig(AppCrawlerConfig param);

    AppCrawlerConfig getOneAppCrawlerConfig(String appId, String project);


    void updateAppCrawlerConfig(AppCrawlerConfig param);

}
