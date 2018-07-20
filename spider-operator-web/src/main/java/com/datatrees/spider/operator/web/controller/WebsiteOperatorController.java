package com.datatrees.spider.operator.web.controller;

import javax.annotation.Resource;
import java.util.HashMap;

import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;
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

    private static final Logger                 logger = LoggerFactory.getLogger(WebsiteOperatorController.class);

    @Resource
    private              WebsiteOperatorService websiteOperatorService;

    @Resource
    private              RedisService           redisService;

    /**
     * 从老运营商导入配置
     * @param config
     * @return
     */
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

    /**
     * 根据websiteName更新searchConfig和extractorConfig
     * @param operator
     * @return
     */
    @RequestMapping("/updateConfig")
    public HttpResult<Object> updateConfig(@RequestBody WebsiteOperator operator) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            websiteOperatorService.updateWebsite(operator);
            logger.info("updateConfig success websiteName={}", operator.getWebsiteName());
            return result.success(true);
        } catch (Exception e) {
            logger.error("updateConfig error websiteName={}", operator.getWebsiteName(), e);
            return result.failure();
        }
    }

    /**
     * 从其他环境导入配置
     * @param websiteName
     * @param from
     * @return
     */
    @RequestMapping("/importConfig")
    public HttpResult<Object> importConfig(String websiteName, String from) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            websiteOperatorService.importConfig(websiteName, from);
            logger.info("importConfig success websiteName={},from={}", websiteName, from);
            return result.success(true);
        } catch (Exception e) {
            logger.error("importConfig error websiteName={},from={}", websiteName, from, e);
            return result.failure();
        }
    }

    /**
     * 导出配置到其他环境
     * @param websiteName
     * @param to
     * @return
     */
    @RequestMapping("/exportConfig")
    public HttpResult<Object> exportConfig(String websiteName, String to) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            websiteOperatorService.exportConfig(websiteName, to);
            logger.info("importConfig success websiteName={},to={}", websiteName, to);
            return result.success(true);
        } catch (Exception e) {
            logger.error("importConfig error websiteName={},to={}", websiteName, to, e);
            return result.failure();
        }
    }

    /**
     * 导出运营商时所需保存的
     * @param websiteOperator
     * @return
     */
    @RequestMapping("/saveConfigForExport")
    public HttpResult<Object> saveConfigForExport(@RequestBody WebsiteOperator websiteOperator) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            websiteOperatorService.saveConfigForExport(websiteOperator);
            logger.info("importConfig success websiteName={}", websiteOperator.getWebsiteName());
            return result.success(true);
        } catch (Exception e) {
            logger.error("importConfig error", e);
            return result.failure();
        }
    }

    /**
     * 查询配置
     */
    @RequestMapping("/getByWebsiteNameAndEnv")
    public Object getByWebsiteNameAndEnv(String websiteName, String env) {
        try {
            return websiteOperatorService.getByWebsiteNameAndEnv(websiteName, env);
        } catch (Exception e) {
            logger.error("getByWebsiteName error websiteName={}", websiteName, e);
            return new HashMap<>();
        }
    }

    /**
     * 启用/禁用
     */
    @RequestMapping("/updateEnable")
    public Object updateEnable(String websiteName, Boolean enable) {
        return websiteOperatorService.updateWebsiteStatus(websiteName, enable, false);
    }

    @RequestMapping("/mappingPluginFile")
    public Object mappingPluginFile(String websiteName, String fileName) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        CheckUtils.checkNotBlank(fileName, "fileName is empty");
        redisService.saveString(RedisKeyPrefixEnum.WEBSITE_PLUGIN_FILE_NAME, websiteName, fileName);
        return new HttpResult<>().success();
    }

}
