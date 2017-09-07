package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.domain.model.WebsiteGroup;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.WebsiteGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权重
 * Created by zhouxinghai on 2017/8/31
 */
@RestController
@RequestMapping("/website/group")
public class WebsiteGroupController {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteGroupController.class);
    @Resource
    private WebsiteGroupService websiteGroupService;

    /**
     * 配置权重
     * 格式:{
     * "groupCode":"CHINA_10000",
     * "config":{"china_10000_app":200}
     * }
     * @param map
     * @return
     */
    @RequestMapping("/configGroup")
    public HttpResult<Object> configGroup(@RequestBody Map<String, Object> map) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            String groupCode = (String) map.get("groupCode");
            Map<String, Integer> config = (LinkedHashMap<String, Integer>) map.get("config");
            List<WebsiteGroup> list = websiteGroupService.configGroup(groupCode, config);
            logger.info("configGroup success config={}", JSON.toJSONString(map));
            return result.success(list);
        } catch (Exception e) {
            logger.error("configGroup error config={}", JSON.toJSONString(map), e);
            return result.failure();
        }
    }

    /**
     * 删除权重配置
     * @param groupCode
     * @return
     */
    @RequestMapping("/deleteGroup")
    public HttpResult<Object> deleteGroupConfig(String groupCode) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            websiteGroupService.deleteByGroupCode(groupCode);
            logger.info("deleteGroupConfig success groupCode={}", groupCode);
            return result.success(true);
        } catch (Exception e) {
            logger.error("deleteGroupConfig error groupCode={}", groupCode, e);
            return result.failure();
        }
    }

}
