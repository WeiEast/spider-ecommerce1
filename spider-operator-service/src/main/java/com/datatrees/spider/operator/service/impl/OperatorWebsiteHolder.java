package com.datatrees.spider.operator.service.impl;

import javax.annotation.Resource;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import com.datatrees.spider.operator.service.WebsiteOperatorService;
import com.datatrees.spider.share.domain.website.WebsiteConfig;
import com.datatrees.spider.share.service.WebsiteConfigService;
import com.datatrees.spider.share.service.utils.WebsiteUtils;
import com.datatrees.spider.share.service.website.WebsiteHolder;
import org.springframework.stereotype.Component;

@Component
public class OperatorWebsiteHolder implements WebsiteHolder {

    @Resource
    private WebsiteOperatorService websiteOperatorService;

    @Resource
    private WebsiteConfigService   websiteConfigService;

    @Override
    public boolean support(String websiteName) {
        return WebsiteUtils.isOperator(websiteName);
    }

    @Override
    public Website getWebsite(String websiteName) {
        WebsiteConfig websiteConfig = getWebsiteConfig(websiteName);
        return websiteConfigService.buildWebsite(websiteConfig);
    }

    @Override
    public WebsiteConfig getWebsiteConfig(String websiteName) {
        WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(websiteName);
        return websiteOperatorService.buildWebsiteConfig(websiteOperator);
    }
}
