package com.datatrees.spider.share.service.website.impl;

import javax.annotation.Resource;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.spider.share.domain.model.WebsiteInfo;
import com.datatrees.spider.share.domain.website.WebsiteConfig;
import com.datatrees.spider.share.service.WebsiteConfigService;
import com.datatrees.spider.share.service.WebsiteInfoService;
import com.datatrees.spider.share.service.utils.WebsiteUtils;
import com.datatrees.spider.share.service.website.WebsiteHolder;
import org.springframework.stereotype.Component;

@Component
public class CommonWebsiteHolder implements WebsiteHolder {

    @Resource
    private WebsiteConfigService websiteConfigService;

    @Resource
    private WebsiteInfoService   websiteInfoService;

    @Override
    public boolean support(String websiteName) {
        return !WebsiteUtils.isOperator(websiteName);
    }

    @Override
    public Website getWebsite(String websiteName) {
        WebsiteConfig websiteConfig = getWebsiteConfig(websiteName);
        return websiteConfigService.buildWebsite(websiteConfig);
    }

    @Override
    public WebsiteConfig getWebsiteConfig(String websiteName) {
        WebsiteInfo websiteInfo = websiteInfoService.getByWebsiteName(websiteName);
        return websiteInfoService.buildWebsiteConfig(websiteInfo);
    }
}
