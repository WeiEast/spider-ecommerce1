/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.login;

import com.datatrees.crawler.core.domain.config.login.LoginConfig;
import com.datatrees.crawler.core.domain.config.login.LoginType;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 16, 2014 4:14:49 PM
 */
public enum Login {
    INSTANCE;
    private static final Logger logger = LoggerFactory.getLogger(Login.class);

    public void doLogin(SearchProcessorContext context) throws Exception {
        if (context.needLogin()) {
            LoginConfig loginConfig = context.getLoginConfig();
            LoginType loginType = loginConfig.getType();

            boolean result = LoginHandler.get(loginType).login(context);

            if (result) {
                logger.info("Cookie checking is pass ");
                context.setLoginStatus(Status.SUCCEED);
            } else if (loginType == LoginType.SERVER) {
                result = LoginHandler.CLIENT_LOGIN.login(context);
                if (result) {
                    logger.info("Cookie checking is pass ");
                    context.setLoginStatus(Status.SUCCEED);
                    context.getLoginResource().putCookie(context.getLoginAccountKey(), ProcessorContextUtil.getCookieString(context));
                } else {
                    logger.warn("Cookie checking is not pass ");
                    context.setLoginStatus(Status.FAILED);
                }
            } else {
                logger.warn("Cookie checking is not pass ");
                context.setLoginStatus(Status.FAILED);
            }

        }
    }

    public enum Status {
        SUCCEED,
        FAILED
    }

}
