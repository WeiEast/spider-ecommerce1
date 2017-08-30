package com.datatrees.rawdatacentral.service;

import java.util.List;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.vo.WebsiteConfig;

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

    ///**
    // * 获取WebsiteConf
    // * @param websiteName
    // * @return
    // */
    //WebsiteConf getWebsiteConfFromCache(String websiteName);

    /**
     * 根据websiteName更新searchConfigSource,extractConfigSource
     * @param websiteName
     * @param searchConfig
     * @param extractConfig
     */
    boolean updateWebsiteConf(String websiteName, String searchConfig, String extractConfig);

    /**
     * 删除缓存
     * @param websiteName
     */
    void deleteCacheByWebsiteName(String websiteName);

    /**
     * 获取运营商配置
     * @return
     */
    List<OperatorCatalogue> queryAllOperatorConfig();

    /**
     * 获取SearchProcessorContext,taskInit使用
     * @param taskId
     * @return
     */
    SearchProcessorContext getSearchProcessorContext(Long taskId);

    ExtractorProcessorContext getExtractorProcessorContext(Long taskId);

    ExtractorProcessorContext getExtractorProcessorContextWithBankId(int bankId);

    /**
     * 将WebsiteConfig转化成Website
     * @param websiteConfig
     * @return
     */
    Website buildWebsite(WebsiteConfig websiteConfig);

    /**
     * 将WebsiteOperator转化成Website
     * @param websiteOperator
     * @return
     */
    Website buildWebsite(WebsiteOperator websiteOperator);

    /**
     * 根据taskId从缓存取Website
     * @param taskId
     * @return
     */
    Website getWebsiteFromCache(Long taskId);

}
