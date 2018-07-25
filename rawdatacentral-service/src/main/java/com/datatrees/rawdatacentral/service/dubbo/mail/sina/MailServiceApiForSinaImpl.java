package com.datatrees.rawdatacentral.service.dubbo.mail.sina;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.mail.sina.MailServiceApiForSina;
import com.datatrees.spider.share.domain.GroupEnum;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhangyanjia on 2018/1/26.
 */
@Service
public class MailServiceApiForSinaImpl implements MailServiceApiForSina {

    private static final Logger          logger = LoggerFactory.getLogger(MailServiceApiForSinaImpl.class);

    @Resource
    private              CommonPluginApi commonPluginApi;

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        if (param.getTaskId() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.MAIL_SINA_H5.getWebsiteName());
        HttpResult<Object> result = commonPluginApi.init(param);
        return result;

    }

    @Override
    public HttpResult<Object> login(CommonPluginParam param) {
        if (param.getTaskId() == null || param.getUsername() == null || param.getPassword() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.MAIL_SINA_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        HttpResult<Object> result = commonPluginApi.submit(param);

        return result;
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        if (param.getTaskId() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.MAIL_SINA_H5.getWebsiteName());
        return commonPluginApi.refeshPicCode(param);
    }
}
