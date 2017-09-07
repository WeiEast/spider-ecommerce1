/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.listener;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.core.message.AbstractRocketMessageListener;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.TaskStageEnum;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月13日 下午2:27:24
 */
public class LoginInfoMessageListener extends AbstractRocketMessageListener<CollectorMessage> {

    private static final Logger  logger                = LoggerFactory.getLogger(LoginInfoMessageListener.class);
    private static final boolean setCookieFormatSwitch = PropertiesConfiguration.getInstance().getBoolean("set.cookie.format.switch", false);
    private Collector           collector;
    private StringRedisTemplate redisTemplate;

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void process(CollectorMessage message) {
        Long taskId = message.getTaskId();
        String websiteName = message.getWebsiteName();
        RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);
        WebsiteConfigService websiteConfigService = BeanFactoryUtils.getBean(WebsiteConfigService.class);
        //是否是独立运营商
        Boolean isNewOperator = StringUtils.startsWith(websiteName, RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.getPrefix());

        String key = RedisKeyPrefixEnum.TASK_RUN_STAGE.getRedisKey(taskId);
        Boolean initStatus = redisTemplate.opsForValue().setIfAbsent(key, TaskStageEnum.INIT.getStatus());
        //第一次收到启动消息
        if (initStatus) {
            BeanFactoryUtils.getBean(MonitorService.class).initTask(taskId, websiteName);
            //30不再接受重复消息
            redisTemplate.expire(key, RedisKeyPrefixEnum.TASK_RUN_STAGE.getTimeout(), RedisKeyPrefixEnum.TASK_RUN_STAGE.getTimeUnit());
            //记录运营商别名,有别名就是独立模块
            redisService.saveString(RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME, taskId, websiteName);
            Website website = null;
            if (isNewOperator) {
                //数据库真实websiteName
                websiteName = RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.parsePostfix(websiteName);
                //之后运行还是数据库真实websiteName
                message.setWebsiteName(websiteName);
                TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_NAME, websiteName);
                //从新的运营商表读取配置
                WebsiteOperator websiteOperator = BeanFactoryUtils.getBean(WebsiteOperatorService.class).getByWebsiteName(websiteName);
                //设置代理
                ProxyUtils.setProxyEnable(taskId, websiteOperator.getProxyEnable());
                website = websiteConfigService.buildWebsite(websiteOperator);
                OperatorPluginService operatorPluginService = BeanFactoryUtils.getBean(ClassLoaderService.class)
                        .getOperatorPluginService(websiteName);
                OperatorParam param = new OperatorParam();
                param.setTaskId(taskId);
                param.setWebsiteName(websiteName);
                //执行运营商插件初始化操作
                operatorPluginService.init(param);
                //保存taskId对应的website,因为运营过程中用的是
                redisService.cache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, website);
                //运营商独立部分第一次初始化后不启动爬虫
            } else {
                //非运营商或者老运营商
                website = BeanFactoryUtils.getBean(WebsiteConfigService.class).getWebsiteByWebsiteName(websiteName);
                redisService.cache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, website);
                collector.processMessage(message);
            }
            logger.info("receve login message taskId={}", message.getTaskId());
        } else {
            //独立运营商登录成功消息是真实的websiteName,用从redis里取第一次的websiteName
            websiteName = redisService.getString(RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.getRedisKey(taskId), 15, TimeUnit.SECONDS);
            //是否是独立运营商
            isNewOperator = StringUtils.startsWith(websiteName, RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.getPrefix());
            //非运营商或者老运营商,重复消息不处理
            if (!isNewOperator) {
                logger.warn("重复消息,不处理,taskId={},websiteName={}", taskId, websiteName);
                return;
            }
            //如果是登录成功消息就启动爬虫
            String taskStage = redisService.getString(RedisKeyPrefixEnum.TASK_RUN_STAGE.getRedisKey(taskId));
            if (StringUtils.equals(taskStage, TaskStageEnum.CRAWLER_START.getStatus())) {
                websiteName = RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.parsePostfix(websiteName);
                //之后运行还是数据库真实websiteName
                message.setWebsiteName(websiteName);
                collector.processMessage(message);
                logger.info("receve login message taskId={}", message.getTaskId());
            }
        }

    }

    @Override
    public CollectorMessage messageConvert(MessageExt message) {
        CollectorMessage collectorMessage = new CollectorMessage();
        String body = new String(message.getBody());
        try {
            LoginMessage loginInfo = JSON.parseObject(body, LoginMessage.class);
            if (loginInfo != null) {
                logger.info("Init logininfo:" + loginInfo);
                collectorMessage.setTaskId(loginInfo.getTaskId());
                collectorMessage.setWebsiteName(loginInfo.getWebsiteName());
                collectorMessage.setEndURL(loginInfo.getEndUrl());
                collectorMessage.setCookie(loginInfo.getCookie());
                collectorMessage.setAccountNo(loginInfo.getAccountNo());
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
