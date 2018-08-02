package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.service.dubbo.mail.MailServiceApiForSina;
import com.datatrees.spider.share.domain.CommonPluginParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhangyanjia on 2018/1/29.
 */
@RestController
@RequestMapping("/sina")
public class SinaMailController {

    private static final Logger                logger = LoggerFactory.getLogger(SinaMailController.class);

    @Resource
    private              MailServiceApiForSina mailServiceApiForSina;

    @RequestMapping("/login/init")
    public Object loginInit(CommonPluginParam param) {
        return mailServiceApiForSina.init(param);
    }

    @RequestMapping("/login/submit")
    public Object loginSubmit(CommonPluginParam param) {
        return mailServiceApiForSina.login(param);
    }

    @RequestMapping("/register/refeshPicCode")
    public Object registerRefeshPicCode(CommonPluginParam param) {
        return mailServiceApiForSina.refeshPicCode(param);
    }

}
