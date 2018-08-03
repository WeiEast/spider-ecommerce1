package com.datatrees.rawdatacentral.collector.listener.handler;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.spider.share.domain.CollectorMessage;
import com.datatrees.spider.share.domain.LoginMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectorMessageUtils {

    private static final Logger logger = LoggerFactory.getLogger(CollectorMessageUtils.class);

    public static CollectorMessage buildCollectorMessage(LoginMessage loginInfo) {
        boolean setCookieFormatSwitch = PropertiesConfiguration.getInstance().getBoolean("set.cookie.format.switch", false);
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
