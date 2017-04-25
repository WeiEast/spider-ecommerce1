/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.listener;

import com.datatrees.rawdatacentral.collector.actor.Collector;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.rawdatacentral.core.message.AbstractRocketMessageListener;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.core.model.message.impl.LoginInfo;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月13日 下午2:27:24
 */
public class LoginInfoMessageListener extends AbstractRocketMessageListener<CollectorMessage> {
    private static final Logger log = LoggerFactory.getLogger(LoginInfoMessageListener.class);
    private static final boolean setCookieFormatSwitch = PropertiesConfiguration.getInstance().getBoolean("set.cookie.format.switch", false);
    private Collector collector;

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * AbstractRocketMessageListener#process(java.lang.Object)
     */
    @Override
    public void process(CollectorMessage message) {
        collector.processMessage(message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * AbstractRocketMessageListener#messageConvert(com.alibaba
     * .rocketmq.common.message.Message)
     */
    @Override
    public CollectorMessage messageConvert(MessageExt message) {
        CollectorMessage collectorMessage = new CollectorMessage();
        String body = new String(message.getBody());
        try {
            LoginInfo loginInfo = (LoginInfo) GsonUtils.fromJson(body, LoginInfo.class);
            if (loginInfo != null) {
                log.info("Init logininfo:" + loginInfo);
                collectorMessage.setUserId(loginInfo.getUserId());
                collectorMessage.setWebsiteName(loginInfo.getWebsiteName());
                collectorMessage.setEndURL(loginInfo.getUrl());
                collectorMessage.setNeedDuplicate(loginInfo.isSupplyResult());
                collectorMessage.setLevel1Status(loginInfo.isLevel1Status());
                if (loginInfo.getHeader() != null) {
                    collectorMessage.setCookie(loginInfo.getHeader().getCookie() == null ? "" : loginInfo.getHeader().getCookie());
                    if (setCookieFormatSwitch && StringUtils.isNotBlank(loginInfo.getHeader().getSetCookie())) {
                        String cookie =
                                collectorMessage.getCookie().endsWith(";")
                                        ? collectorMessage.getCookie() + loginInfo.getHeader().getSetCookie()
                                        : collectorMessage.getCookie() + ";" + loginInfo.getHeader().getSetCookie();
                        collectorMessage.setCookie(cookie);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Message convert error.." + e.getMessage(), e);
        }
        return collectorMessage;
    }
}
