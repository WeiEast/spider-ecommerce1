/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.login;

import java.util.LinkedHashMap;
import java.util.Map;

import com.datatrees.crawler.core.domain.Cookie;
import com.datatrees.crawler.core.domain.WebsiteAccount;
import com.datatrees.crawler.core.domain.config.login.LoginConfig;
import com.datatrees.crawler.core.domain.config.login.LoginType;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.exception.LoginException;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.plugin.PluginCaller;
import com.datatrees.crawler.core.processor.plugin.PluginConfSupplier;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
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
            switch (loginType) {
                case SERVER:
                    logger.info("init cookie get from server...");
                    this.doServerLogin(context);
                    break;
                case CLIENT:
                    logger.info("init cookie with client login...");
                    this.doClientLogin(context);
                    break;
                case APP:
                    logger.info("init cookie with app login...");
                    this.doAPPLogin(context);
                    break;
                case PLUGIN:
                    logger.info("init cookie with plugin login...");
                    this.doPluginLogin(context);
                    break;
            }
        }
    }

    public void doPluginLogin(SearchProcessorContext context) throws Exception {
        LoginConfig logconfig = context.getLoginConfig();
        String cookie = null;
        Cookie objCookie = new Cookie();

        AbstractPlugin pluginDesc = logconfig.getPlugin();
        if (pluginDesc != null) {// plugin
            logger.info("init cookie with plugin " + pluginDesc);

            cookie = (String) PluginCaller.call(context, pluginDesc, (PluginConfSupplier) pluginWrapper -> new LinkedHashMap<>());

            logger.info("plugin fetch cookie result : " + cookie);
            Map<String, Object> resultMap = PluginUtil.checkPluginResult(cookie);
            cookie = resultMap.get(PluginConstants.COOKIE).toString();
            // set other params from login to context
            resultMap.remove(PluginConstants.COOKIE);
            if (resultMap.size() > 0) context.getContext().putAll(resultMap);
        } else {
            logger.warn("doPluginLogin with no plugin ...");
        }
        objCookie.setCookie(cookie);
        // set cookie to context
        ProcessorContextUtil.setCookieObject(context, objCookie);

        logger.info("fetch cookie = " + objCookie);
        // check login success
        this.doCookieCheck(logconfig, context);
    }

    public void doAPPLogin(SearchProcessorContext context) throws Exception {
        // EMPTY,to do ,send url to app & app sent back cookie
        String cookie = ProcessorContextUtil.getCookieString(context);

        if (cookie == null) {
            logger.warn("no active cookie while do app login");
        } else {
            logger.info("fetch cookie = " + cookie);
            LoginConfig loginConfig = context.getLoginConfig();
            // check login success
            this.doCookieCheck(loginConfig, context);
        }
    }

    /**
     * server login
     *
     * @param context
     * @throws Exception
     */
    public void doServerLogin(SearchProcessorContext context) throws Exception {
        Cookie cookie;
        if (context.getLoginResource() == null || (cookie = context.getLoginResource().getCookie(ProcessorContextUtil.getAccountKey(context))) == null) {
            logger.warn("no active cookie while do server login,use empty cookie");
            cookie = new Cookie();
        }
        ProcessorContextUtil.setCookieObject(context, cookie);
        logger.info("fetch cookie = " + cookie);
        // get proxy
        // String proxyUrlString = this.getProxy(context);
        LoginConfig loginConfig = context.getLoginConfig();
        this.doCookieCheck(loginConfig, context);
        if (context.getLoginStatus().equals(Status.FAILED)) {
            this.doClientLogin(context);// do client login
            if (context.getLoginStatus().equals(Status.SUCCEED)) {
                context.getLoginResource().putCookie(ProcessorContextUtil.getAccountKey(context), ProcessorContextUtil.getCookieString(context));
            }
        }
    }

    /**
     * client login use Random account
     *
     * @param context
     * @throws Exception
     */
    private void doClientLogin(SearchProcessorContext context) throws Exception {
        LoginConfig loginConfig = context.getLoginConfig();
        WebsiteAccount account = context.getLoginResource().getAccount(ProcessorContextUtil.getAccountKey(context));
        ProcessorContextUtil.setAccount(context, account);

        if (account == null) {
            logger.error("no active accountlist while do client login for " + ProcessorContextUtil.getAccountKey(context));
            throw new LoginException("no active accountlist while do client login for " + ProcessorContextUtil.getAccountKey(context));
        }

        Cookie objCookie = new Cookie();
        objCookie.setUserName(account.getUserName());
        String cookie = null;
        // get proxy
        // String proxyUrlString = this.getProxy(context);

        AbstractPlugin pluginDesc = loginConfig.getPlugin();
        if (pluginDesc != null) {// plugin
            logger.info("init cookie with plugin " + pluginDesc);

            cookie = (String) PluginCaller.call(context, pluginDesc, (PluginConfSupplier) pluginWrapper -> {
                Map<String, String> params = new LinkedHashMap<>();
                params.put(PluginConstants.USERNAME, account.getUserName());
                params.put(PluginConstants.PASSWORD, account.getPassword());

                return params;
            });

            logger.info("plugin fetch cookie result : " + cookie);
            Map<String, Object> resultMap = PluginUtil.checkPluginResult(cookie);
            cookie = resultMap.get(PluginConstants.COOKIE) != null ? resultMap.get(PluginConstants.COOKIE).toString() : null;
            // set other params from login to context
            resultMap.remove(PluginConstants.COOKIE);
            if (resultMap.size() > 0) context.getContext().putAll(resultMap);
        } else {// httpclient
            cookie = LoginUtil.getInstance().doLogin(loginConfig, context);
        }
        objCookie.setCookie(cookie);
        // set cookie to context
        ProcessorContextUtil.setCookieObject(context, objCookie);

        logger.info("fetch cookie = " + objCookie);
        // check login success
        this.doCookieCheck(loginConfig, context);
    }

    private boolean doCookieCheck(LoginConfig config, SearchProcessorContext context) throws ResultEmptyException {
        boolean loginSuccess = LoginUtil.getInstance().doLoginByCookies(config, context);
        if (loginSuccess) {
            logger.info("Cookie checking is pass ");
            context.setLoginStatus(Status.SUCCEED);
            return true;
        } else {
            logger.warn("Cookie checking is not pass ");
            context.setLoginStatus(Status.FAILED);
            return false;
        }
    }

    public enum Status {
        SUCCEED,
        FAILED
    }

}
