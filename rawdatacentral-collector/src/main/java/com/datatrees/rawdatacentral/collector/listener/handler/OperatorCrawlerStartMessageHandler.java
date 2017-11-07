package com.datatrees.rawdatacentral.collector.listener.handler;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.TaskStageEnum;
import com.datatrees.rawdatacentral.domain.enums.TopicTag;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.service.mq.handler.AbstractMessageHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OperatorCrawlerStartMessageHandler extends AbstractMessageHandler {

    private static final Logger  logger                = LoggerFactory.getLogger(OperatorCrawlerStartMessageHandler.class);
    private static final boolean setCookieFormatSwitch = PropertiesConfiguration.getInstance().getBoolean("set.cookie.format.switch", false);
    @Resource
    private Collector      collector;
    @Resource
    private MonitorService monitorService;

    @Override
    public String getTag() {
        return TopicTag.OPERATOR_CRAWLER_START.getTag();
    }

    @Override
    public String getBizType() {
        return "处理爬虫启动消息";
    }

    @Override
    public boolean consumeMessage(String msg) {
        LoginMessage loginInfo = JSON.parseObject(msg, LoginMessage.class);
        Long taskId = loginInfo.getTaskId();
        String taskStageKey = RedisKeyPrefixEnum.TASK_RUN_STAGE.getRedisKey(taskId);
        //如果是登录成功消息就启动爬虫
        String taskStage = RedisUtils.get(taskStageKey);
        if (StringUtils.equals(taskStage, TaskStageEnum.LOGIN_SUCCESS.getStatus()) ||
                StringUtils.equals(taskStage, TaskStageEnum.LOGIN_POST_SUCCESS.getStatus())) {
            monitorService.sendTaskLog(taskId, loginInfo.getWebsiteName(), "爬虫-->启动-->成功");
            RedisUtils.set(taskStageKey, TaskStageEnum.CRAWLER_START.getStatus(), RedisKeyPrefixEnum.TASK_RUN_STAGE.toSeconds());
            collector.processMessage(buildCollectorMessage(loginInfo));
        }
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
                collectorMessage.setAccountNo(loginInfo.getAccountNo());
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
