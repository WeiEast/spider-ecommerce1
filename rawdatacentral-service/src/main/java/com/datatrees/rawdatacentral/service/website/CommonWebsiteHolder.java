package com.datatrees.rawdatacentral.service.website;

import javax.annotation.Resource;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.spider.share.service.WebsiteHolderService;
import com.datatrees.spider.share.service.utils.WebsiteUtils;
import com.datatrees.rawdatacentral.domain.model.WebsiteInfo;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.service.WebsiteInfoService;
import org.springframework.stereotype.Component;

@Component
public class CommonWebsiteHolder implements WebsiteHolderService.WebsiteHolder {

    @Resource
    private WebsiteConfigService websiteConfigService;

    @Resource
    private WebsiteInfoService   websiteInfoService;

    @Override
    public boolean support(long taskId, String websiteName) {
        return !WebsiteUtils.isOperator(websiteName);
    }

    @Override
    public Website getWebsite(long taskId, String websiteName) {
        WebsiteInfo websiteInfo = websiteInfoService.getByWebsiteName(websiteName);
        return websiteConfigService.buildWebsite(websiteInfo);
    }
}
