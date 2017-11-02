/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.listener;

import java.nio.charset.Charset;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.WebsiteUtils;
import com.datatrees.rawdatacentral.core.message.AbstractRocketMessageListener;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.TaskStageEnum;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月13日 下午2:27:24
 */
public class LoginInfoMessageListener extends AbstractRocketMessageListener<CollectorMessage> {

    private static final Logger  logger                = LoggerFactory.getLogger(LoginInfoMessageListener.class);
    private static final boolean setCookieFormatSwitch = PropertiesConfiguration.getInstance().getBoolean("set.cookie.format.switch", false);
    private Collector collector;
    private RedisService           redisService           = BeanFactoryUtils.getBean(RedisService.class);
    private WebsiteConfigService   websiteConfigService   = BeanFactoryUtils.getBean(WebsiteConfigService.class);
    private MonitorService         monitorService         = BeanFactoryUtils.getBean(MonitorService.class);
    private CrawlerOperatorService crawlerOperatorService = BeanFactoryUtils.getBean(CrawlerOperatorService.class);

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    @Override
    public void process(CollectorMessage message) {
        Long taskId = message.getTaskId();
        String taskStageKey = RedisKeyPrefixEnum.TASK_RUN_STAGE.getRedisKey(taskId);
        Boolean initStatus = RedisUtils.setnx(taskStageKey, TaskStageEnum.RECEIVE.getStatus(), RedisKeyPrefixEnum.TASK_RUN_STAGE.toSeconds());
        //第一次收到启动消息
        if (initStatus) {
            //是否是独立运营商
            Boolean isOperator = WebsiteUtils.isOperator(message.getWebsiteName());
            //获取真实websiteName
            String realebsiteName = TaskUtils.getRealWebsiteName(message.getWebsiteName());
            if (isOperator) {
                OperatorParam param = new OperatorParam();
                param.setTaskId(taskId);
                param.setFormType(FormType.LOGIN);
                param.setMobile(Long.valueOf(message.getAccountNo()));
                param.setWebsiteName(realebsiteName);
                param.getExtral().put(AttributeKey.USERNAME, message.getAccountNo());
                param.getExtral().put(AttributeKey.GROUP_CODE, message.getGroupCode());
                param.getExtral().put(AttributeKey.GROUP_NAME, message.getGroupName());
                crawlerOperatorService.init(param);
            } else {
                //初始化监控信息
                monitorService.initTask(taskId, realebsiteName, message.getAccountNo());
                //缓存task基本信息
                TaskUtils.initTaskShare(taskId, message.getWebsiteName());
                TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, message.getAccountNo());
                TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_CODE, message.getGroupCode());
                TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_NAME, message.getGroupName());
                //这里电商,邮箱,老运营商
                Website website = websiteConfigService.getWebsiteByWebsiteName(realebsiteName);
                //保存taskId对应的website
                redisService.cache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, website);
                monitorService.sendTaskLog(taskId, realebsiteName, "爬虫-->启动-->成功");
                //启动爬虫
                collector.processMessage(message);
            }
        } else {
            //这里电商,邮箱,老运营商不会有第二次消息,这里只处理运营商登录成功消息
            Boolean isOperator = WebsiteUtils.isOperator(message.getWebsiteName());
            //非运营商或者老运营商,重复消息不处理
            if (!isOperator) {
                logger.warn("重复消息,不处理,taskId={},websiteName={}", taskId, message.getWebsiteName());
                return;
            }
            //如果是登录成功消息就启动爬虫
            String taskStage = RedisUtils.get(taskStageKey);
            if (StringUtils.equals(taskStage, TaskStageEnum.LOGIN_SUCCESS.getStatus())) {
                monitorService.sendTaskLog(taskId, message.getWebsiteName(), "爬虫-->启动-->成功");
                RedisUtils.set(taskStageKey, TaskStageEnum.CRAWLER_START.getStatus(), RedisKeyPrefixEnum.TASK_RUN_STAGE.toSeconds());
                String websiteName = TaskUtils.getTaskShare(taskId, AttributeKey.WEBSITE_NAME);
                //之后运行还是数据库真实websiteName
                message.setWebsiteName(websiteName);
                collector.processMessage(message);
            }
        }

    }

    @Override
    public CollectorMessage messageConvert(MessageExt message) {
        CollectorMessage collectorMessage = new CollectorMessage();
        String msg = new String(message.getBody(), Charset.forName("UTF-8"));
        try {
            LoginMessage loginInfo = JSON.parseObject(msg, LoginMessage.class);
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
