package com.datatrees.rawdatacentral.web.controller;

import com.datatrees.crawler.plugin.util.PluginHttpUtils;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorLoginPluginService;
import com.datatrees.rawdatacentral.share.RedisService;
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

    private static final Logger logger = LoggerFactory.getLogger(OperatorController.class);

    @Resource
    private CrawlerService      crawlerService;

    @Resource
    private ClassLoaderService  classLoaderService;

    @Resource
    private RedisService        redisService;

    @RequestMapping("/queryAllOperatorConfig")
    public Object queryAllOperatorConfig() {
        return crawlerService.queryAllOperatorConfig();
    }

    @RequestMapping("/init")
    public Object init(Long taskId, String websiteName, OperatorParam param) {
        //重复任务清除cookie
        redisService.deleteKey(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId.toString()));
        OperatorLoginPluginService longService = classLoaderService.getOperatorLongService(websiteName);
        return longService.init(taskId, websiteName, param);
    }

    @RequestMapping("/refeshPicCode")
    public Object refeshPicCode(Long taskId, String websiteName, OperatorParam param) {
        OperatorLoginPluginService longService = classLoaderService.getOperatorLongService(websiteName);
        return longService.refeshPicCode(taskId, websiteName, param);
    }

    @RequestMapping("/refeshPicCodeAndDisplay")
    public ResponseEntity<InputStreamResource> refeshPicCodeAndDisplay(Long taskId, String websiteName,
                                                                       OperatorParam param) {
        OperatorLoginPluginService longService = classLoaderService.getOperatorLongService(websiteName);
        HttpResult<Map<String, Object>> result = longService.refeshPicCode(taskId, websiteName, param);
        if (result.getStatus()) {
            String picCode = result.getData().get(OperatorLoginPluginService.RETURN_FIELD_PIC_CODE).toString();
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
    public Object refeshSmsCode(Long taskId, String websiteName, OperatorParam param) {
        OperatorLoginPluginService longService = classLoaderService.getOperatorLongService(websiteName);
        return longService.refeshSmsCode(taskId, websiteName, param);
    }

    @RequestMapping("/login")
    public Object login(Long taskId, String websiteName, OperatorParam param) {
        OperatorLoginPluginService longService = classLoaderService.getOperatorLongService(websiteName);
        return longService.login(taskId, websiteName, param);
    }

    @RequestMapping("/validatePicCode")
    public Object validatePicCode(Long taskId, String websiteName, OperatorParam param) {
        OperatorLoginPluginService longService = classLoaderService.getOperatorLongService(websiteName);
        return longService.validatePicCode(taskId, websiteName, param);
    }

    @RequestMapping("/openPage")
    public Object openPage(Long taskId, String url, String type) throws IOException {
        if (StringUtils.equals("post", type)) {
            return PluginHttpUtils.postString(url, taskId);
        }
        return PluginHttpUtils.getString(url, taskId);
    }

}
