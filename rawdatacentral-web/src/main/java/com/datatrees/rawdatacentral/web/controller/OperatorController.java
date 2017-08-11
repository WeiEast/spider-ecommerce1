package com.datatrees.rawdatacentral.web.controller;

import com.datatrees.crawler.plugin.util.PluginHttpUtils;
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
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

    private static final Logger    logger = LoggerFactory.getLogger(OperatorController.class);

    @Resource
    private CrawlerOperatorService crawlerOperatorService;

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
            String picCode = result.getData().get(OperatorPluginService.RETURN_FIELD_PIC_CODE).toString();
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
        if (StringUtils.equals("post", type)) {
            return PluginHttpUtils.postString(taskId, null, url);
        }
        return PluginHttpUtils.getString(taskId, null, url);
    }

}
