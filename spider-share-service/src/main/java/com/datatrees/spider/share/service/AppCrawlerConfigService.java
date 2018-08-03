package com.datatrees.spider.share.service;

import java.util.List;

import com.datatrees.spider.share.domain.param.AppCrawlerConfigParam;
import com.datatrees.spider.share.domain.param.CrawlerProjectParam;

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
     * 修改商户配置信息
     * @param appId
     * @param projectConfigInfos
     */
    void updateAppConfig(String appId, List<CrawlerProjectParam> projectConfigInfos);

}
