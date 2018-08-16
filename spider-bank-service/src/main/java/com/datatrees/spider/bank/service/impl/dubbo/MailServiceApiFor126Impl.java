package com.datatrees.spider.bank.service.impl.dubbo;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.*;
import com.datatrees.spider.share.service.CommonPluginService;
import com.datatrees.spider.bank.api.MailServiceApiFor126;
import com.datatrees.spider.share.common.utils.RedisUtils;
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

    private static final Logger              logger = LoggerFactory.getLogger(MailServiceApiFor126Impl.class);

    @Resource
    private              CommonPluginService commonPluginService;

    @Override
    public HttpResult<Object> login(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.MAIL_126_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        param.setAutoSendLoginSuccessMsg(false);
        TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.WEBSITE_NAME, param.getWebsiteName());


        String initKey = RedisKeyPrefixEnum.LOGIN_INIT.getRedisKey(param.getTaskId());
        Boolean initStatus = RedisUtils.setnx(initKey, "true", RedisKeyPrefixEnum.LOGIN_INIT.toSeconds());
        logger.info("rec login request initStatus:{},param:{}", initStatus, JSON.toJSONString(param));
        if (initStatus) {
            HttpResult<Object> initResult = commonPluginService.init(param);
            if (!initResult.getStatus()) {
                RedisUtils.del(initKey);
                return new HttpResult<>().failure(ErrorCode.TASK_INIT_ERROR);
            }
        }
        return commonPluginService.submit(param);
    }

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.MAIL_126_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        param.setAutoSendLoginSuccessMsg(false);
        TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.WEBSITE_NAME, param.getWebsiteName());
        return commonPluginService.refeshQRCode(param);
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.MAIL_126_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        param.setAutoSendLoginSuccessMsg(false);
        return commonPluginService.queryQRStatus(param);
    }
}
