package com.datatrees.spider.operator.service.impl;

import javax.annotation.Resource;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.spider.share.service.website.WebsiteHolder;
import com.datatrees.spider.share.service.utils.WebsiteUtils;
import com.datatrees.spider.share.service.WebsiteConfigService;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import com.datatrees.spider.operator.service.WebsiteOperatorService;
import org.springframework.stereotype.Component;

@Component
public class OperatorWebsiteHolder implements WebsiteHolder {

    @Resource
    private WebsiteConfigService   websiteConfigService;

    @Resource
    private WebsiteOperatorService websiteOperatorService;

    @Override
    public boolean support(long taskId, String websiteName) {
        return WebsiteUtils.isOperator(websiteName);
    }

    @Override
    public Website getWebsite(long taskId, String websiteName) {
        WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(websiteName);
        return websiteOperatorService.buildWebsite(websiteOperator);
    }
}
