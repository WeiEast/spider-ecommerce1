package com.datatrees.rawdatacentral.service;

import com.datatrees.crawler.core.domain.Website;

public interface WebsiteHolderService {

    /**
     * 获取Website
     * @param taskId
     * @param websiteName
     * @return
     */
    Website getWebsite(long taskId, String websiteName);

}
