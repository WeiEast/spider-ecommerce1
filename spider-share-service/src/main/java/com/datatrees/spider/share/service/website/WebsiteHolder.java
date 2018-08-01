package com.datatrees.spider.share.service.website;

import com.datatrees.crawler.core.domain.Website;

/**
 * 运营商,电商,website表不一样
 * @author zhouxinghai
 * @date 2018/7/23
 */
public interface WebsiteHolder {

    /**
     * 是否支持
     * @param websiteName
     * @return
     */
    boolean support(String websiteName);

    /**
     * 获取Website
     * @param websiteName
     * @return
     */
    Website getWebsite(String websiteName);

}
