package com.datatrees.rawdatacentral.service;

/**
 * User: yand
 * Date: 2018/4/10
 */
public interface AppCrawlerConfigService {

    /**
     * 从redis中获取
     * @return
     */
    String
    getFromRedis(String appId, String project);

}
