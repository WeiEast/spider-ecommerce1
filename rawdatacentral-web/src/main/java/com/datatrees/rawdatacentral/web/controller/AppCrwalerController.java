package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.domain.appconfig.AppCrawlerConfigParam;
import com.datatrees.rawdatacentral.domain.appconfig.CrawlerProjectParam;
import com.datatrees.rawdatacentral.service.AppCrawlerConfigService;
import com.treefinance.saas.knife.common.CommonStateCode;
import com.treefinance.saas.knife.request.PageRequest;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import org.apache.poi.ss.formula.functions.Count;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: yand
 * Date: 2018/4/16
 */
@RestController
@RequestMapping("/app/crawler")
public class AppCrwalerController {

    private static final Logger logger = LoggerFactory.getLogger(AppCrwalerController.class);
    @Resource
    private AppCrawlerConfigService appCrawlerConfigService;

    @RequestMapping("/getList")
    public SaasResult<List<AppCrawlerConfigParam>> getAppCrawlerConfigList() {
        return Results.newSuccessResult(appCrawlerConfigService.getAppCrawlerConfigList());

    }

    //@RequestMapping("/getList")
    //public Object getAppCrawlerConfigList() {
    //    return appCrawlerConfigService.getAppCrawlerConfigList();
    //
    //}

    @RequestMapping("/update")
    public SaasResult<String> updateAppCrawlerConfig(List<CrawlerProjectParam> crawlerProjectParam, String appId) {
        appCrawlerConfigService.updateAppConfig(crawlerProjectParam, appId);
        return Results.newResult(CommonStateCode.SUCCESS);
    }

}
