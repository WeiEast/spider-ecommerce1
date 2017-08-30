/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.core.message.AbstractRocketMessageListener;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
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
        String key = RedisKeyPrefixEnum.TASK_RUN_STAGE.getRedisKey(taskId);
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(key, "INIT");
        if (!ifAbsent) {
            logger.warn("重复消息,不处理,taskId={},websiteName={}", taskId, websiteName);
            return;
        }
        redisTemplate.expire(key, RedisKeyPrefixEnum.TASK_RUN_STAGE.getTimeout(), RedisKeyPrefixEnum.TASK_RUN_STAGE.getTimeUnit());

        RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);
        Website website = null;
        if (StringUtils.startsWith(websiteName, RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.getPrefix())) {
            redisService.cache(RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME, taskId, websiteName);
            websiteName = RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.parsePostfix(websiteName);
            message.setWebsiteName(websiteName);
            TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_NAME, websiteName);
            WebsiteOperator websiteOperator = BeanFactoryUtils.getBean(WebsiteOperatorService.class).getByWebsiteName(websiteName);
            website = BeanFactoryUtils.getBean(WebsiteConfigService.class).buildWebsite(websiteOperator);
            OperatorPluginService operatorPluginService = BeanFactoryUtils.getBean(ClassLoaderService.class).getOperatorPluginService(websiteName);
            OperatorParam param = new OperatorParam();
            param.setTaskId(taskId);
            param.setWebsiteName(websiteName);
            operatorPluginService.init(param);
        } else {
            website = BeanFactoryUtils.getBean(WebsiteConfigService.class).getWebsiteByWebsiteName(websiteName);
        }
        redisService.cache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, website);
        logger.info("receve login message taskId={}", message.getTaskId());
        collector.processMessage(message);
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
