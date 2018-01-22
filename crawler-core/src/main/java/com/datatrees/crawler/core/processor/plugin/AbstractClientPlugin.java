/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin;

import com.datatrees.common.protocol.Protocol;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.webrobot.driver.WebRobotClientDriver;
import com.datatrees.webrobot.webdriver.browser.BrowserType;
import com.treefinance.crawler.framework.extension.plugin.PluginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * abstract client plugin custom plugin should implements this as super class
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 10:28:20 AM
 */
public abstract class AbstractClientPlugin {

    private Logger logger = LoggerFactory.getLogger(AbstractClientPlugin.class);
    private Protocol     webClient;
    private ProxyManager proxyManager;

    public Protocol getWebClient() {
        return webClient;
    }

    public void setWebClient(Protocol webClient) {
        this.webClient = webClient;
    }

    public ProxyManager getProxyManager() {
        return proxyManager;
    }

    public void setProxyManager(ProxyManager proxyManager) {
        this.proxyManager = proxyManager;
    }

    public abstract String process(String... args) throws Exception;

    protected String getResponseByWebRequest(LinkNode linkNode) {
        try {
            return PluginHelper.requestAsString(linkNode, null);
        } catch (Exception e) {
            throw new RuntimeException("Error sending request >>> " + linkNode, e);
        }
    }

    @Deprecated
    protected String getPorxy(String cacertUrl) throws Exception {
        return this.getProxy(cacertUrl);
    }
    protected String getProxy(String url) throws Exception {
        return PluginHelper.getProxy(url);
    }

    public WebRobotClientDriver getWebRobotDriver(String url) throws Exception {
        return getWebRobotDriver(url, BrowserType.FIREFOX, null);
    }

    public WebRobotClientDriver getWebRobotDriver(String url, BrowserType browserType, String clientName) throws Exception {
        return PluginHelper.getWebRobotDriver(url, browserType, clientName);
    }

    public void releaseDriver(WebRobotClientDriver driver) {
        PluginHelper.releaseDriver(driver);
    }

}
