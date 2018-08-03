package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;
import java.util.List;

import com.datatrees.spider.share.domain.param.AppCrawlerConfigParam;
import com.datatrees.spider.share.service.AppCrawlerConfigService;
import com.treefinance.saas.knife.common.CommonStateCode;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: yand
 * Date: 2018/4/16
 */
@RestController
@RequestMapping("/app/crawler")
public class AppCrwalerController {

    private static final Logger                  logger = LoggerFactory.getLogger(AppCrwalerController.class);

    @Resource
    private              AppCrawlerConfigService appCrawlerConfigService;

    @RequestMapping("/getList")
    public SaasResult<List<AppCrawlerConfigParam>> getAppCrawlerConfigList() {
        return Results.newSuccessResult(appCrawlerConfigService.getAppCrawlerConfigList());

    }

    @RequestMapping("/update")
    public SaasResult<String> updateAppCrawlerConfig(@RequestBody AppCrawlerConfigParam params) {
        logger.info("AppCrawlerConfigParam is {}", params);
        appCrawlerConfigService.updateAppConfig(params.getAppId(), params.getProjectConfigInfos());
        return Results.newResult(CommonStateCode.SUCCESS);
    }

}
