package com.datatrees.spider.share.service;

import java.util.List;
import java.util.Map;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.spider.share.domain.model.WebsiteConf;
import com.datatrees.spider.share.domain.website.WebsiteConfig;

/**
 * 站点配置
 * Created by zhouxinghai on 2017/6/30.
 */
public interface WebsiteConfigService {

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
     * key:bankId
     * value:websiteName
     * @param map
     */
    void initBankCache(Map<Integer, String> map);

    WebsiteConf getWebsiteConf(String websiteName);

    List<WebsiteConf> getWebsiteConf(List<String> websiteNameList);

}
