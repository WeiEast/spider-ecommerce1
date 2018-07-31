package com.datatrees.rawdatacentral.service.dubbo.mail._126;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.mail._126.MailServiceApiFor126;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.domain.GroupEnum;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * User: yand
 * Date: 2018/2/27
 */
@Service
public class MailServiceApiFor126Impl implements MailServiceApiFor126 {

    private static final Logger          logger = LoggerFactory.getLogger(MailServiceApiFor126Impl.class);

    @Resource
    private              CommonPluginApi commonPluginApi;

    @Override
    public HttpResult<Object> login(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.MAIL_126_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        param.setAutoSendLoginSuccessMsg(false);

        String initKey = RedisKeyPrefixEnum.LOGIN_INIT.getRedisKey(param.getTaskId());
        Boolean initStatus = RedisUtils.setnx(initKey, "true", RedisKeyPrefixEnum.LOGIN_INIT.toSeconds());
        logger.info("rec login request initStatus:{},param:{}", initStatus, JSON.toJSONString(param));
        if (initStatus) {
            HttpResult<Object> initResult = commonPluginApi.init(param);
            if (!initResult.getStatus()) {
                RedisUtils.del(initKey);
                return new HttpResult<>().failure(ErrorCode.TASK_INIT_ERROR);
            }
        }
        return commonPluginApi.submit(param);
    }

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.MAIL_126_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        param.setAutoSendLoginSuccessMsg(false);
        return commonPluginApi.refeshQRCode(param);
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.MAIL_126_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        param.setAutoSendLoginSuccessMsg(false);
        return commonPluginApi.queryQRStatus(param);
    }
}
