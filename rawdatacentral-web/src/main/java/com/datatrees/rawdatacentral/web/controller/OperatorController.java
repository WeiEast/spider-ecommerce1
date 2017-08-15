package com.datatrees.rawdatacentral.web.controller;

import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
@RestController
@RequestMapping("/operator")
public class OperatorController {

    private static final Logger  logger = LoggerFactory.getLogger(OperatorController.class);


    @Resource
    private CrawlerService crawlerService;

    @RequestMapping("/queryAllOperatorConfig")
    public HttpResult<List<OperatorCatalogue>> queryAllOperatorConfig() {
        return crawlerService.queryAllOperatorConfig();
    }



}
