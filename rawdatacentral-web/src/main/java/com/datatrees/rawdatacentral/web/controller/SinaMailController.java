package com.datatrees.rawdatacentral.web.controller;

import com.datatrees.rawdatacentral.api.RpcEducationService;
import com.datatrees.rawdatacentral.api.mail.sina.MailServiceApiForSina;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by zhangyanjia on 2018/1/29.
 */
@RestController
@RequestMapping("/sina")
public class SinaMailController {
    private static final Logger logger= LoggerFactory.getLogger(SinaMailController.class);

    @Resource
    private MailServiceApiForSina mailServiceApiForSina;

    @RequestMapping("/login/init")
    public Object loginInit(CommonPluginParam param){
        return mailServiceApiForSina.loginInit(param);
    }

    @RequestMapping("/login/submit")
    public Object loginSubmit(CommonPluginParam param){
        return mailServiceApiForSina.login(param);
    }

    @RequestMapping("/register/refeshPicCode")
    public Object registerRefeshPicCode(CommonPluginParam param){
        return mailServiceApiForSina.refeshPicCode(param);
    }

}
