package com.datatrees.rawdatacentral.service.dubbo.mail.qq;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.mail.qq.MailServiceApiForQQ;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
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
        param.setProxyEnable(true);
        param.setAutoSendLoginSuccessMsg(false);

        String initKey = RedisKeyPrefixEnum.LOGIN_INIT.getRedisKey(param.getTaskId());
        Boolean initStatus = RedisUtils.setnx(initKey, "true", RedisKeyPrefixEnum.LOGIN_INIT.toSeconds());
        log.info("rec login request initStatus:{},param:{}", initStatus, JSON.toJSONString(param));
        if (initStatus) {
            HttpResult<Object> initResult = commonPluginApi.init(param);
            if (!initResult.getStatus()) {
                RedisUtils.del(initKey);
                return new HttpResult<>().failure(ErrorCode.TASK_INIT_ERROR);
            }
        }
        return commonPluginApi.submit(param);
    }

}

