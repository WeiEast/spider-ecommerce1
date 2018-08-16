package com.datatrees.spider.bank.service.impl.dubbo;

import javax.annotation.Resource;

import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.*;
import com.datatrees.spider.share.service.CommonPluginService;
import com.datatrees.spider.bank.api.MailServiceApiForSina;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhangyanjia on 2018/1/26.
 */
@Service
public class MailServiceApiForSinaImpl implements MailServiceApiForSina {

    private static final Logger              logger = LoggerFactory.getLogger(MailServiceApiForSinaImpl.class);

    @Resource
    private              CommonPluginService commonPluginService;

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        if (param.getTaskId() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.MAIL_SINA_H5.getWebsiteName());
        TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.WEBSITE_NAME, param.getWebsiteName());

        HttpResult<Object> result = commonPluginService.init(param);
        return result;

    }

    @Override
    public HttpResult<Object> login(CommonPluginParam param) {
        if (param.getTaskId() == null || param.getUsername() == null || param.getPassword() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.MAIL_SINA_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.WEBSITE_NAME, param.getWebsiteName());

        HttpResult<Object> result = commonPluginService.submit(param);

        return result;
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        if (param.getTaskId() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.MAIL_SINA_H5.getWebsiteName());
        TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.WEBSITE_NAME, param.getWebsiteName());

        return commonPluginService.refeshPicCode(param);
    }
}
