package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;

import com.datatrees.rawdatacentral.api.CommonPluginService;
import com.datatrees.rawdatacentral.api.mail.qq.MailServiceApiForQQ;
import com.datatrees.spider.share.common.utils.ProcessResultUtils;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ProcessResult;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail/qq")
public class QQMailControler {

    private static final Logger              logger = LoggerFactory.getLogger(QQMailControler.class);

    @Resource
    private              MailServiceApiForQQ mailServiceApiForQQ;

    @Resource
    private              CommonPluginService commonPluginService;

    @RequestMapping("/login")
    public Object login(CommonPluginParam param) {
        return mailServiceApiForQQ.login(param);
    }

    @RequestMapping("/queryLoginStatus")
    public Object queryLoginStatus(Long processId) {
        return commonPluginService.queryProcessResult(processId);
    }

    @RequestMapping("/refeshQRCode")
    public Object refeshQRCode(CommonPluginParam param) {
        return mailServiceApiForQQ.refeshQRCode(param);
    }

    @RequestMapping("/queryQRStatus")
    public Object queryQRStatus(CommonPluginParam param) {
        return mailServiceApiForQQ.queryQRStatus(param);
    }

    @RequestMapping("/refeshQRCode2")
    public ResponseEntity<InputStreamResource> refeshQRCode2(Long processId) {
        ProcessResult<Object> processResult = ProcessResultUtils.queryProcessResult(processId);
        if (processResult.isSuccess()) {
            String picCode = processResult.getData().toString();
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
}
