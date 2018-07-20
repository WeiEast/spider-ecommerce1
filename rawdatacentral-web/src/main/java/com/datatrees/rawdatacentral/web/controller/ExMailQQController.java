package com.datatrees.rawdatacentral.web.controller;

import com.datatrees.rawdatacentral.api.mail.exmail_qq.MailServiceApiForExMailQQ;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by zhangyanjia on 2018/2/27.
 */
@RestController
@RequestMapping("/exmailqq")
public class ExMailQQController {
    private static final Logger logger = LoggerFactory.getLogger(ExMailQQController.class);

    @Resource
    private MailServiceApiForExMailQQ mailServiceApiForExMailQQ;

    @RequestMapping("/login/init")
    public Object loginInit(CommonPluginParam param) {
        return mailServiceApiForExMailQQ.init(param);
    }

    @RequestMapping("/login/submit")
    public Object loginSubmit(CommonPluginParam param) {
        return mailServiceApiForExMailQQ.login(param);
    }
}