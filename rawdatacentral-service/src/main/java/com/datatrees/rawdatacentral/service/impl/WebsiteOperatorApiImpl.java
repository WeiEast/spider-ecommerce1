package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.datatrees.spider.operator.api.WebsiteOperatorApi;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebsiteOperatorApiImpl implements WebsiteOperatorApi {

    private static final Logger                 logger = LoggerFactory.getLogger(WebsiteOperatorServiceImpl.class);

    @Resource
    private              WebsiteOperatorService websiteOperatorService;

    @Override
    public void updateEnable(String websiteName, Boolean enable) {
        websiteOperatorService.updateEnable(websiteName, enable);
        logger.info("updateEnable success wesiteName={},enable={}", websiteName, enable);
    }

    @Override
    public List<WebsiteOperator> queryDisable() {
        return websiteOperatorService.queryDisable();
    }

    @Override
    public WebsiteOperator getByWebsiteName(String websiteName) {
        return websiteOperatorService.getByWebsiteName(websiteName);
    }

    @Override
    public List<WebsiteOperator> queryAll() {
        return websiteOperatorService.queryAll();
    }

    @Override
    public Map<String, WebsiteOperator> updateWebsiteStatus(String websiteName, Boolean enable, Boolean auto) {
        return websiteOperatorService.updateWebsiteStatus(websiteName, enable, auto);
    }

}
