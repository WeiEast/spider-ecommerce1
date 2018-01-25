package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.Map;

import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.mail.qq.MailServiceApiFor163;
import com.datatrees.rawdatacentral.common.utils.ProcessResultUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;
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
@RequestMapping("/mail/163")
public class _163MailControler {

    private static final Logger logger = LoggerFactory.getLogger(_163MailControler.class);
    @Resource
    private MailServiceApiFor163 mailServiceApiFor163;
    @Resource
    private CommonPluginApi      commonPluginApi;

    @RequestMapping("/login")
    public Object login(CommonPluginParam param) {
        return mailServiceApiFor163.login(param);
    }

    @RequestMapping("/queryLoginStatus")
    public Object queryLoginStatus(Long processId) {
        return commonPluginApi.queryProcessResult(processId);
    }

    @RequestMapping("/refeshQRCode")
    public Object refeshQRCode(CommonPluginParam param) {
        return mailServiceApiFor163.refeshQRCode(param);
    }

    @RequestMapping("/queryQRStatus")
    public Object queryQRStatus(CommonPluginParam param) {
        return mailServiceApiFor163.queryQRStatus(param);
    }

    @RequestMapping("/refeshQRCode2")
    public ResponseEntity<InputStreamResource> refeshQRCode2(Long processId) {
        ProcessResult<Map<String, String>> processResult = ProcessResultUtils.queryProcessResult(processId);
        if (processResult.isSuccess()) {
            String picCode = processResult.getData().get(
                    AttributeKey.QR_BASE64);
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
