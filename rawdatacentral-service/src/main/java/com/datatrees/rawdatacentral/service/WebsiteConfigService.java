package com.datatrees.rawdatacentral.service;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.domain.model.WebsiteInfoWithBLOBs;
import com.datatrees.spider.share.domain.website.WebsiteConfig;

/**
 * 站点配置
 * Created by zhouxinghai on 2017/6/30.
 */
public interface WebsiteConfigService {

    /**
     * 获取站点配置
     * @param websiteName
     * @return
     */
    WebsiteConfig getWebsiteConfigByWebsiteName(String websiteName);

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

    /**
     * 获取SearchProcessorContext,taskInit使用
     * @param websiteName
     * @return
     */
    SearchProcessorContext getSearchProcessorContext(Long taskId, String websiteName);

    ExtractorProcessorContext getExtractorProcessorContext(Long taskId, String websiteName);

    ExtractorProcessorContext getExtractorProcessorContextWithBankId(int bankId, Long taskId);

    /**
     * 将WebsiteConfig转化成Website
     * @param websiteConfig
     * @return
     */
    Website buildWebsite(WebsiteConfig websiteConfig);

    /**
     * 将WebsiteInfoWithBLOBs转化成Website
     * @param websiteInfo
     * @return
     */
    Website buildWebsiteFromWebsiteInfo(WebsiteInfoWithBLOBs websiteInfo);

}
