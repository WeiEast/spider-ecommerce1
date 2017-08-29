package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运营商配置
 * Created by zhouxinghai on 2017/8/29
 */
@RestController
@RequestMapping("/website/operator")
public class WebsiteOperatorController {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteOperatorController.class);
    @Resource
    private WebsiteOperatorService websiteOperatorService;

    @RequestMapping("/importWebsite")
    public HttpResult<Boolean> importWebsite(WebsiteOperator config) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            websiteOperatorService.importWebsite(config);
            logger.info("importWebsite success websiteName={}", config.getWebsiteName());
            return result.success(true);
        } catch (Exception e) {
            logger.error("importWebsite error websiteName={}", config.getWebsiteName(), e);
            return result.failure();
        }
    }

}
