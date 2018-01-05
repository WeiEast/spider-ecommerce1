package com.datatrees.rawdatacentral.service.dubbo.mail.qq;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api

        .CommonPluginApi;
import com.datatrees.rawdatacentral.api.mail.qq.MailServiceApiForQQ;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailServiceApiForQQImpl implements MailServiceApiForQQ {

    @Resource
    private CommonPluginApi commonPluginApi;

    @Override
    public HttpResult<Object> login(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.MAIL_QQ_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);

        String initKey = RedisKeyPrefixEnum.LOGIN_INIT.getRedisKey(param.getTaskId());
        Boolean initStatus = RedisUtils.setnx(initKey, "true", RedisKeyPrefixEnum.LOGIN_INIT.toSeconds());
        if (initStatus) {
            commonPluginApi.init(param);
        }
        return commonPluginApi.submit(param);
    }

}

