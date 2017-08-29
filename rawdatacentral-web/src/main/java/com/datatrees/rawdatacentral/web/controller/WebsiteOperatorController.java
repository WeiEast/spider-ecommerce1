package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.domain.model.OperatorGroup;
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
    public HttpResult<Object> importWebsite(WebsiteOperator config) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            websiteOperatorService.importWebsite(config);
            logger.info("importWebsite success websiteName={}", config.getWebsiteName());
            return result.success(true);
        } catch (Exception e) {
            logger.error("importWebsite error websiteName={}", config.getWebsiteName(), e);
            return result.failure();
        }
    }

    @RequestMapping("/configOperatorGroup")
    public HttpResult<Object> configOperatorGroup(String groupCode, String config) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            Map<String, Integer> map = JSON.parseObject(config, new TypeReference<Map<String, Integer>>() {});
            List<OperatorGroup> list = websiteOperatorService.configOperatorGroup(groupCode, map);
            logger.info("importWebsite success groupCode={},config={}", groupCode, config);
            return result.success(list);
        } catch (Exception e) {
            logger.error("importWebsite error groupCode={},config={}", groupCode, config, e);
            return result.failure();
        }
    }

}
