package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.domain.model.OperatorGroup;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.OperatorGroupService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
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
    @Resource
    private OperatorGroupService   operatorGroupService;

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

    @RequestMapping("/configGroup")
    public HttpResult<Object> configGroup(@RequestBody Map<String, Object> map) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            String groupCode = (String) map.get("groupCode");
            Map<String, Integer> config = (LinkedHashMap<String, Integer>) map.get("config");
            List<OperatorGroup> list = operatorGroupService.configGroup(groupCode, config);
            logger.info("configGroup success config={}", JSON.toJSONString(map));
            return result.success(list);
        } catch (Exception e) {
            logger.error("configGroup error config={}", JSON.toJSONString(map), e);
            return result.failure();
        }
    }

    @RequestMapping("/deleteGroupConfig")
    public HttpResult<Object> deleteGroupConfig(String groupCode) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            operatorGroupService.deleteByGroupCode(groupCode);
            logger.info("deleteGroupConfig success groupCode={}", groupCode);
            return result.success(true);
        } catch (Exception e) {
            logger.error("deleteGroupConfig error groupCode={}", groupCode, e);
            return result.failure();
        }
    }

}
