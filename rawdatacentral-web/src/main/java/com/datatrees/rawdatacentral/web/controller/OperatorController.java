package com.datatrees.rawdatacentral.web.controller;

import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TaskHttpClient;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
@RestController
@RequestMapping("/operator")
public class OperatorController {

    private static final Logger    logger = LoggerFactory.getLogger(OperatorController.class);

    @Resource
    private CrawlerOperatorService crawlerOperatorService;

    @Resource
    private RedisService           redisService;

    @RequestMapping("/queryAllOperatorConfig")
    public Object queryAllOperatorConfig() {
        return crawlerOperatorService.queryAllConfig();
    }

    @RequestMapping("/init")
    public Object init(OperatorParam param) {
        return crawlerOperatorService.init(param);
    }

    @RequestMapping("/refeshPicCode")
    public Object refeshPicCode(OperatorParam param) {
        return crawlerOperatorService.refeshPicCode(param);
    }

    @RequestMapping("/refeshPicCodeAndDisplay")
    public ResponseEntity<InputStreamResource> refeshPicCodeAndDisplay(OperatorParam param) {
        HttpResult<Map<String, Object>> result = crawlerOperatorService.refeshPicCode(param);
        if (result.getStatus()) {
            String picCode = result.getData().get(AttributeKey.PIC_CODE).toString();
            byte[] bytes = Base64.decodeBase64(picCode);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", "inline");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            return ResponseEntity.ok().headers(headers).contentLength(bytes.length).contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(new ByteArrayInputStream(bytes)));

        }
        return null;
    }

    @RequestMapping("/refeshSmsCode")
    public Object refeshSmsCode(OperatorParam param) {
        return crawlerOperatorService.refeshSmsCode(param);
    }

    @RequestMapping("/submit")
    public Object login(OperatorParam param) {
        return crawlerOperatorService.submit(param);
    }

    @RequestMapping("/validatePicCode")
    public Object validatePicCode(OperatorParam param) {
        return crawlerOperatorService.validatePicCode(param);
    }

    @RequestMapping("/openPage")
    public Object openPage(Long taskId, String url, String type) throws IOException {
        return TaskHttpClient.create(taskId, "openpage", RequestType.valueOf(type.trim()), "remark01").setFullUrl(url)
            .invoke().getPageContent();
    }

    @RequestMapping("/deleteRedisResult")
    public Object deleteRedisResult(Long taskId) throws IOException {
        redisService.deleteKey(RedisKeyPrefixEnum.TASK_REQUEST.getRedisKey(taskId));
        redisService.deleteKey(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId));
        redisService.deleteKey(RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId));
        return new HttpResult<>().success();
    }

    @RequestMapping("/mappingPluginFile")
    public Object mappingPluginFile(String websiteName,String fileName) throws IOException {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        CheckUtils.checkNotBlank(fileName, "fileName is empty");
        redisService.saveString(RedisKeyPrefixEnum.PLUGIN_FILE_WEBSITE.getRedisKey(websiteName),fileName,RedisKeyPrefixEnum.PLUGIN_FILE_WEBSITE.getTimeout(),RedisKeyPrefixEnum.PLUGIN_FILE_WEBSITE.getTimeUnit());
        return new HttpResult<>().success();
    }

}
