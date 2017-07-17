package com.datatrees.rawdatacentral.web.controller;

import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorLoginPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
@RestController
@RequestMapping("/operator")
public class OperatorController {

    private static final Logger logger = LoggerFactory.getLogger(OperatorController.class);

    @Resource
    private CrawlerService      crawlerService;

    @Resource
    private ClassLoaderService  classLoaderService;

    @RequestMapping("/queryAllOperatorConfig")
    public Object queryAllOperatorConfig() {
        return crawlerService.queryAllOperatorConfig();
    }

    @RequestMapping("/refeshPicCode")
    public Object refeshPicCode(Long taskId, String websiteName, OperatorParam param) {
        OperatorLoginPluginService longService = classLoaderService.getOperatorLongService(websiteName);
        return longService.refeshPicCode(taskId, websiteName, param);
    }

    @RequestMapping("/refeshSmsCode")
    public Object refeshSmsCode(Long taskId, String websiteName, OperatorParam param) {
        OperatorLoginPluginService longService = classLoaderService.getOperatorLongService(websiteName);
        return longService.refeshSmsCode(taskId, websiteName, param);
    }

    @RequestMapping("/login")
    public Object login(Long taskId, String websiteName, OperatorParam param) {
        OperatorLoginPluginService longService = classLoaderService.getOperatorLongService(websiteName);
        return longService.login(taskId, websiteName, param);
    }

}
