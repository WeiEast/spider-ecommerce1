package com.datatrees.spider.share.web.controller;

import javax.annotation.Resource;

import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.service.WebsiteInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
@RestController
@RequestMapping("/website")
public class WebsiteController {

    private static final Logger             logger = LoggerFactory.getLogger(WebsiteController.class);

    @Resource
    private              WebsiteInfoService websiteInfoService;

    @RequestMapping(value = "/updateWebsiteConf", method = RequestMethod.POST)
    public HttpResult<Boolean> updateWebsiteConf(String websiteName, String searchConfig, String extractConfig) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            websiteInfoService.updateWebsiteConf(websiteName, searchConfig, extractConfig);
            logger.info("updateWebsiteConf success websiteName={}", websiteName);
            return result.success(true);
        } catch (Exception e) {
            logger.error("updateWebsiteConf error websiteName={}", websiteName, e);
            return result.failure();
        }
    }

}
