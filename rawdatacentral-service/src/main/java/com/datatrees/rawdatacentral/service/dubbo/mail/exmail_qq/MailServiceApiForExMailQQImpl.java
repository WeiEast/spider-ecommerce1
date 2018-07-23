package com.datatrees.rawdatacentral.service.dubbo.mail.exmail_qq;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.mail.exmail_qq.MailServiceApiForExMailQQ;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhangyanjia on 2018/2/26.
 */
@Service
public class MailServiceApiForExMailQQImpl implements MailServiceApiForExMailQQ {

    private static final Logger          logger = LoggerFactory.getLogger(MailServiceApiForExMailQQImpl.class);

    @Resource
    private              CommonPluginApi commonPluginApi;

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        if (param.getTaskId() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.EXMAIL_QQ_H5.getWebsiteName());
        HttpResult<Object> result = commonPluginApi.init(param);
        return result;

    }

    @Override
    public HttpResult<Object> login(CommonPluginParam param) {
        if (param.getTaskId() == null || param.getUsername() == null || param.getPassword() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.EXMAIL_QQ_H5.getWebsiteName());

        param.setFormType(FormType.LOGIN);
        HttpResult<Object> result = commonPluginApi.submit(param);
        return result;
    }
}
