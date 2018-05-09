package com.datatrees.rawdatacentral.collector.listener.handler;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.service.mq.handler.AbstractMessageHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @since 17:32 2018/5/4
 */
abstract class LoginStartMessageHandler extends AbstractMessageHandler {

    private static final boolean setCookieFormatSwitch = PropertiesConfiguration.getInstance().getBoolean("set.cookie.format.switch", false);

    protected CollectorMessage buildCollectorMessage(LoginMessage loginInfo) {
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
                        String cookie = collectorMessage.getCookie().endsWith(";") ? collectorMessage.getCookie() + loginInfo.getSetCookie() : collectorMessage.getCookie() + ";" + loginInfo.getSetCookie();
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
