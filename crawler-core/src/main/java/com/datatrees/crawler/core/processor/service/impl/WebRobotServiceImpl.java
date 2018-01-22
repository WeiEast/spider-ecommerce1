/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.ProtocolStatusCodes;
import com.datatrees.common.protocol.util.CookieParser;
import com.datatrees.common.util.URLUtil;
import com.datatrees.crawler.core.domain.config.properties.cookie.BaseCookie;
import com.datatrees.crawler.core.domain.config.properties.cookie.CookieScope;
import com.datatrees.crawler.core.domain.config.service.impl.WebRobotService;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.BeanResourceFactory;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.datatrees.webrobot.action.AbstractAction;
import com.datatrees.webrobot.action.impl.GetCookieAction;
import com.datatrees.webrobot.action.impl.SetCookieAction;
import com.datatrees.webrobot.action.impl.SetCookieArraysAction;
import com.datatrees.webrobot.driver.ClientDriverManager;
import com.datatrees.webrobot.driver.WebRobotClientDriver;
import com.datatrees.webrobot.webdriver.browser.BrowserType;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 7, 2014 7:32:15 PM
 */
public class WebRobotServiceImpl extends ServiceBase {

    private static final Logger log        = LoggerFactory.getLogger(WebRobotServiceImpl.class);
    private              int    retryCount = PropertiesConfiguration.getInstance().getInt("webRobot.retry.count", 3);

    @Override
    public void process(Request request, Response response) throws Exception {
        WebRobotService service = (WebRobotService) getService();
        LinkNode current = RequestUtil.getCurrentUrl(request);
        String url = current.getUrl();
        BrowserType browserType = BrowserType.getBrowserType(service.getBrowserType());
        if (browserType == null) {
            browserType = BrowserType.FIREFOX;
        }
        int retrySize = retryCount;
        WebRobotClientDriver driver = null;
        for (int i = 0; i < retrySize; i++) {
            try {
                String proxyString = null;
                ClientDriverManager clientDriverManager = BeanResourceFactory.getInstance().getBean(ClientDriverManager.class);
                SearchProcessorContext context = (SearchProcessorContext) RequestUtil.getProcessorContext(request);

                Proxy proxy = context.getProxy(url);
                if(proxy != null) {
                    proxyString = proxy.format();
                }

                driver = clientDriverManager.getWebDriver(browserType, proxyString);
                List<AbstractAction> actions = new ArrayList<AbstractAction>();
                HttpState state = ProcessorContextUtil.getHttpState(context);
                if (context.getCookieConf() != null && ((BaseCookie) context.getCookieConf()).getCoexist()) {
                    state = new HttpState();
                    ProcessorContextUtil.setHttpState(context, state);
                }
                if (state != null) {
                    log.debug("set cookie state {}", CookieParser.formatCookies(state.getCookies()));
                    actions.add(new SetCookieArraysAction(Arrays.asList(state.getCookies())));
                } else {
                    log.debug("set cookie string {}", ProcessorContextUtil.getCookieString(context));
                    actions.add(new SetCookieAction(ProcessorContextUtil.getCookieString(context), URLUtil.getDomainName(url)));
                }
                String content = StringUtils.defaultString(driver.open(actions, url, service.getPageLoadPattern(), service.getPageLoadTimeOut()));
                log.debug("perform output content : {} , url : {}", content, url);
                ResponseUtil.setResponseContent(response, content);
                RequestUtil.setContent(request, content);
                if (context.getCookieConf() != null && context.getCookieConf().getScope() != null && context.getCookieConf().getScope() == CookieScope.USER_SESSION) {
                    actions = new ArrayList<AbstractAction>();
                    if (((BaseCookie) context.getCookieConf()).getCoexist()) {
                        actions.add(new GetCookieAction(true));
                        List<Cookie> coookies = driver.performActions(actions, new TypeToken<List<Cookie>>() {}.getType(), false);
                        log.info("alipay get driver cookString: " + coookies);
                        state.addCookies(coookies.toArray(new Cookie[0]));
                    } else {
                        String cookie = StringUtils.defaultString(driver.performActions(actions, String.class, false));
                        ProcessorContextUtil.setCookieString(context, cookie);
                    }
                }
                ProcessorContextUtil.addThreadLocalLinkNode(context, current);
                ProcessorContextUtil.addThreadLocalResponse(context, response);
                break;
            } catch (Exception e) {
                log.error("do performActions error , do recordIndexUrl revisit ... " + e.getMessage(), e);
                if (i == retrySize - 1) {
                    ResponseUtil.setResponseStatus(response, ProtocolStatusCodes.EXCEPTION);
                    throw e;
                }
                current.increaseRetryCount();
            } finally {
                if (driver != null) {
                    driver.release();
                }
            }
        }
    }

}
