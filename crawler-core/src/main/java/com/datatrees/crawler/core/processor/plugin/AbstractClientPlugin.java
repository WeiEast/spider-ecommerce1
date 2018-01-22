/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.Protocol;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.BeanResourceFactory;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.datatrees.webrobot.driver.ClientDriverManager;
import com.datatrees.webrobot.driver.WebRobotClientDriver;
import com.datatrees.webrobot.webdriver.browser.BrowserType;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
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
        Request newRequest = new Request();
        AbstractProcessorContext processorContext = PluginContext.getProcessorContext();
        RequestUtil.setProcessorContext(newRequest, processorContext);
        RequestUtil.setConf(newRequest, PropertiesConfiguration.getInstance());
        RequestUtil.setContext(newRequest, processorContext.getContext());
        Response newResponse = new Response();
        try {
            RequestUtil.setCurrentUrl(newRequest, linkNode);
            AbstractService service = processorContext.getDefaultService();
            ServiceBase serviceProcessor = ProcessorFactory.getService(service);
            serviceProcessor.invoke(newRequest, newResponse);
        } catch (Exception e) {
            logger.error("execute request error! " + e.getMessage(), e);
        }
        return StringUtils.defaultString(RequestUtil.getContent(newRequest));
    }

    protected String getPorxy(String cacertUrl) throws Exception {
        String proxyURL = null;
        AbstractProcessorContext context = PluginContext.getProcessorContext();
        if (context instanceof SearchProcessorContext ) {
            Proxy proxy = ((SearchProcessorContext) context).getProxy(cacertUrl);
            if (proxy != null) {
                proxyURL = proxy.format();
            }
        }
        return proxyURL;
    }

    public WebRobotClientDriver getWebRobotDriver(String url) throws Exception {
        return getWebRobotDriver(url, BrowserType.FIREFOX, null);
    }

    public WebRobotClientDriver getWebRobotDriver(String url, BrowserType browserType, String clientName) throws Exception {
        AbstractProcessorContext context = PluginContext.getProcessorContext();

        ClientDriverManager clientDriverManager = BeanResourceFactory.getInstance().getBean(ClientDriverManager.class);
        WebRobotClientDriver driver = clientDriverManager
                .getWebDriver(browserType, getPorxy(url), clientName, ProcessorContextUtil.getAccountKey(context));

        if (context instanceof SearchProcessorContext) {
            ((SearchProcessorContext) context).setWebRobotClientDriver(driver);
        }

        return driver;
    }

    public void releaseDriver(WebRobotClientDriver driver) {
        if (driver != null && BooleanUtils.isNotTrue(driver.getReleased())) driver.release();
    }

}
