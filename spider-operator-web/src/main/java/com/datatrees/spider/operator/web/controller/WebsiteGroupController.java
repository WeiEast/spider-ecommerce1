package com.datatrees.spider.operator.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.operator.domain.model.WebsiteGroup;
import com.datatrees.spider.operator.service.WebsiteGroupService;
import com.datatrees.spider.operator.service.WebsiteOperatorService;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.treefinance.saas.knife.common.CommonStateCode;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
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

    private static final Logger                 logger = LoggerFactory.getLogger(WebsiteGroupController.class);

    @Resource
    private              WebsiteGroupService    websiteGroupService;

    @Resource
    private              WebsiteOperatorService websiteOperatorService;

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
    public SaasResult configGroup(@RequestBody Map<String, Object> map) {
        String groupCode = (String) map.get("groupCode");
        Map<String, Integer> config = (LinkedHashMap<String, Integer>) map.get("config");
        List<WebsiteGroup> list = websiteGroupService.configGroup(groupCode, config);
        logger.info("configGroup success config={}", JSON.toJSONString(map));
        return Results.newSuccessResult(list);
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

    @RequestMapping("/updateEnable")
    public SaasResult updateEnable(HttpServletResponse response, @RequestBody WebsiteGroup websiteGroup) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            if (null == websiteGroup) return Results.newFailedResult(CommonStateCode.PARAMETER_LACK, "请求参数websiteGroup不能为空！");
            logger.info("updateEnable success websiteName={} Enable={}", websiteGroup.getWebsiteName(), websiteGroup.getEnable());
            websiteGroupService.updateEnable(websiteGroup.getWebsiteName(), websiteGroup.getEnable());
            websiteOperatorService.updateEnable(websiteGroup.getWebsiteName(), websiteGroup.getEnable());
            return Results.newSuccessResult(true);
        } catch (Exception e) {
            logger.error("updateEnable error", e);
            return Results.newFailedResult(CommonStateCode.FAILURE);
        }
    }

    @RequestMapping("/getwebsitenamelist")
    public Object getwebsitenamelist(HttpServletResponse response, String enable, String groupCode, String operatorType) {
        logger.info("getwebsitenamelist() enable={},groupCode={},operatorType={}", enable, groupCode, operatorType);
        response.setHeader("Access-Control-Allow-Origin", "*");
        return websiteGroupService.getWebsiteNameList(enable, groupCode, operatorType);
    }

    /**
     * 查询所有
     * @return
     */
    @RequestMapping("/queryAll")
    public List<WebsiteGroup> queryAll() {
        return websiteGroupService.queryAll();
    }

    /**
     * 查询所有分组
     * @return
     */
    @RequestMapping("/queryAllGroupCode")
    public Map<String, String> queryAllGroupCode() {
        return websiteGroupService.queryAllGroupCode();
    }

    /**
     * 查询所有分组
     * @return
     */
    @RequestMapping("/queryByGroupCode")
    public SaasResult queryByGroupCode(String groupCode) {
        return Results.newSuccessResult(websiteGroupService.queryByGroupCode(groupCode));
    }

    /**
     * 清除缓存
     * @return
     */
    @RequestMapping("/clearOperatorQueueByGroupCode")
    public Object clearOperatorQueueByGroupCode(String groupCode) {
        websiteGroupService.clearOperatorQueueByGroupCode(groupCode);
        return new HttpResult<>().success();
    }

}
