package com.datatrees.spider.share.service;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.spider.share.domain.model.WebsiteConf;
import com.datatrees.spider.share.domain.website.WebsiteConfig;

public interface WebsiteHolderService {

    /**
     * 获取Website
     * @param websiteName
     * @return
     */
    Website getWebsite(String websiteName);

    WebsiteConfig getWebsiteConfig(String websiteName);

    /**
     * 获取WebsiteConf
     * @param websiteName
     * @return
     */
    WebsiteConf getWebsiteConf(String websiteName);

}
