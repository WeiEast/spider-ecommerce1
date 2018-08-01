package com.datatrees.spider.share.service;

import com.datatrees.crawler.core.domain.Website;

public interface WebsiteHolderService {

    /**
     * 获取Website
     * @param taskId
     * @param websiteName
     * @return
     */
    Website getWebsite(long taskId, String websiteName);

    /**
     * 运营商,电商,website表不一样
     * @author zhouxinghai
     * @date 2018/7/23
     */
    interface WebsiteHolder {

        /**
         * 是否支持
         * @param taskId
         * @param websiteName
         * @return
         */
        boolean support(long taskId, String websiteName);

        /**
         * 获取Website
         * @param taskId
         * @param websiteName
         * @return
         */
        Website getWebsite(long taskId, String websiteName);

    }
}
