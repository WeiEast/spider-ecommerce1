package com.datatrees.rawdatacentral.collector.listener.handler;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.StepEnum;
import com.datatrees.rawdatacentral.domain.enums.TopicTag;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.service.mq.handler.AbstractMessageHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoginInfoMessageHandler extends AbstractMessageHandler {

    private static final Logger  logger                = LoggerFactory.getLogger(LoginInfoMessageHandler.class);
    private static final boolean setCookieFormatSwitch = PropertiesConfiguration.getInstance().getBoolean("set.cookie.format.switch", false);
    @Resource
    private Collector            collector;
    @Resource
    private RedisService         redisService;
    @Resource
    private WebsiteConfigService websiteConfigService;
    @Resource
    private MonitorService       monitorService;

    @Override
    public String getTag() {
        return TopicTag.LOGIN_INFO.getTag();
    }

    @Override
    public String getBizType() {
        return "准备";
    }

    @Override
    public boolean consumeMessage(String msg) {
        LoginMessage loginInfo = JSON.parseObject(msg, LoginMessage.class);
        Long taskId = loginInfo.getTaskId();
        TaskUtils.addStep(taskId, StepEnum.REC_INIT_MSG);
        String websiteName = loginInfo.getWebsiteName();
        Boolean initStatus = RedisUtils
                .setnx(RedisKeyPrefixEnum.TASK_RUN_COUNT.getRedisKey(taskId), "0", RedisKeyPrefixEnum.TASK_RUN_COUNT.toSeconds());
        //第一次收到启动消息
        if (initStatus) {
            TaskUtils.addStep(taskId, StepEnum.INIT);
            //这里电商,邮箱,老运营商
            Website website = websiteConfigService.getWebsiteByWebsiteName(websiteName);
            redisService.cache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, website);
            //缓存task基本信息
            TaskUtils.initTaskShare(taskId, loginInfo.getWebsiteName());
            TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, loginInfo.getAccountNo());
            TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_CODE, website.getGroupCode());
            TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_NAME, website.getGroupName());
            TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TITLE, website.getWebsiteTitle());
            TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TYPE, website.getWebsiteType());

            //初始化监控信息
            monitorService.initTask(taskId, websiteName, loginInfo.getAccountNo());
            TaskUtils.addStep(taskId, StepEnum.INIT_SUCCESS);
            monitorService.sendTaskLog(taskId, websiteName, "爬虫-->启动-->成功");
            //启动爬虫
            collector.processMessage(buildCollectorMessage(loginInfo));
            return true;
        }
        logger.warn("重复消息,不处理,taskId={},websiteName={}", taskId, loginInfo.getWebsiteName());
        return true;
    }

    public CollectorMessage buildCollectorMessage(LoginMessage loginInfo) {
        CollectorMessage collectorMessage = new CollectorMessage();
        try {
            if (loginInfo != null) {
                collectorMessage.setTaskId(loginInfo.getTaskId());
                collectorMessage.setWebsiteName(loginInfo.getWebsiteName());
                collectorMessage.setEndURL(loginInfo.getEndUrl());
                collectorMessage.setCookie(loginInfo.getCookie());
                //collectorMessage.setAccountNo(loginInfo.getAccountNo());
                collectorMessage.setGroupCode(loginInfo.getGroupCode());
                collectorMessage.setGroupName(loginInfo.getGroupName());
                if (setCookieFormatSwitch && StringUtils.isNotBlank(loginInfo.getSetCookie())) {
                    if (StringUtils.isBlank(loginInfo.getCookie())) {
                        collectorMessage.setCookie(loginInfo.getSetCookie());
                    } else {
                        String cookie = collectorMessage.getCookie().endsWith(";") ? collectorMessage.getCookie() + loginInfo.getSetCookie() :
                                collectorMessage.getCookie() + ";" + loginInfo.getSetCookie();
                        collectorMessage.setCookie(cookie);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Message convert error.." + e.getMessage(), e);
        }
        return collectorMessage;
    }

}
