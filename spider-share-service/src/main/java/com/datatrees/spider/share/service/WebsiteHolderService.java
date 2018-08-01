package com.datatrees.spider.share.service;

import com.datatrees.crawler.core.domain.Website;

public interface WebsiteHolderService {

    /**
     * 获取Website
     * @param websiteName
     * @return
     */
    Website getWebsite(String websiteName);

}
