package com.datatrees.rawdatacentral.service;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.rawdatacentral.domain.common.WebsiteConfig;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;

/**
 * 站点配置
 * Created by zhouxinghai on 2017/6/30.
 */
public interface WebsiteConfigService {

    /**
     * 获取站点配置
     * @param websiteId
     * @return
     */
    WebsiteConfig getWebsiteConfigByWebsiteId(Integer websiteId);

    /**
     * 获取站点配置
     * @param websiteName
     * @return
     */
    WebsiteConfig getWebsiteConfigByWebsiteName(String websiteName);

    /**
     * 获取爬虫里的Website
     * @param websiteId
     * @return
     */
    Website getWebsiteByWebsiteId(Integer websiteId);

    /**
     * 获取爬虫里的Website
     * @param websiteName
     * @return
     */
    Website getWebsiteByWebsiteName(String websiteName);

    /**
     * 从WebsiteConfig转换
     * @param websiteConfig
     * @return
     */
    Website getFromWebsiteConfig(WebsiteConfig websiteConfig);

    /**
     * 获取WebsiteConf
     * @param websiteName
     * @return
     */
    WebsiteConf getWebsiteConf(String websiteName);

    /**
     * 根据websiteName更新searchConfigSource,extractConfigSource
     * @param websiteName
     * @param searchConfig
     * @param extractConfig
     */
    boolean updateWebsiteConf(String websiteName, String searchConfig, String extractConfig);

    SearchProcessorContext getSearchProcessorContext(String websiteName);

    ExtractorProcessorContext getExtractorProcessorContext(int websiteId);

    ExtractorProcessorContext getExtractorProcessorContextWithBankId(int bankId);

}
