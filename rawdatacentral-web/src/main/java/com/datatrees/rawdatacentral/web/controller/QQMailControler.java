package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.mail.qq.MailServiceApiForQQ;
import com.datatrees.rawdatacentral.domain.mail.MailParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail/qq")
public class QQMailControler {

    private static final Logger logger = LoggerFactory.getLogger(QQMailControler.class);
    @Resource
    private MailServiceApiForQQ mailServiceApiForQQ;

    @RequestMapping("/login")
    public Object login(MailParam param) {
        return mailServiceApiForQQ.login(param);
    }

    @RequestMapping("/queryLoginStatus")
    public Object queryLoginStatus(MailParam param) {
        return mailServiceApiForQQ.queryLoginStatus(param);
    }
}
