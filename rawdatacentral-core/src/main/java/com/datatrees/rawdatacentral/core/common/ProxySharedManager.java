/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.crawler.core.domain.config.operation.impl.proxyset.Option;
import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.proxy.ProxyStatus;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 15, 2014 10:52:51 AM
 */
public class ProxySharedManager extends ProxyManager {
    private static final Logger log = LoggerFactory.getLogger(ProxySharedManager.class);
    private Proxy sharedProxy;
    private ProxyManager proxyManager;


    /**
     * @param sharedProxy
     * @param proxyManager
     */
    public ProxySharedManager(Proxy sharedProxy, ProxyManager proxyManager) {
        super();
        this.sharedProxy = sharedProxy;
        this.proxyManager = proxyManager;
    }

    public void setCallBackTemplate(String callBackTemplate) {
        if (proxyManager != null) proxyManager.setCallBackTemplate(callBackTemplate);
        this.callBackTemplate = callBackTemplate;
    }

    /**
     * @return the sharedProxy
     */
    public Proxy getSharedProxy() {
        return sharedProxy;
    }

    /**
     * @param sharedProxy the sharedProxy to set
     */
    public void setSharedProxy(Proxy sharedProxy) {
        this.sharedProxy = sharedProxy;
    }

    public ProxyManager getProxyManager() {
        return proxyManager;
    }

    public void setProxyManager(ProxyManager proxyManager) {
        this.proxyManager = proxyManager;
    }


    @Override
    public Proxy getProxy(String url) throws Exception {
        return sharedProxy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.common.resource.ProxyManager#callBackProxy(com.datatrees
     * .crawler.core.processor.proxy.Proxy, com.datatrees.crawler.core.processor.proxy.ProxyStatus)
     */
    @Override
    public void callBackProxy(Proxy proxy, ProxyStatus status) throws Exception {
        proxyManager.callBackProxy(sharedProxy, status);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.common.resource.ProxyManager#callBackProxy(com.datatrees
     * .crawler.core.processor.proxy.Proxy, com.datatrees.crawler.core.processor.proxy.ProxyStatus,
     * boolean)
     */
    @Override
    public void callBackProxy(Proxy proxy, ProxyStatus status, boolean aync) throws Exception {
        proxyManager.callBackProxy(sharedProxy, status, aync);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.common.resource.ProxyManager#release(com.datatrees.crawler
     * .core.processor.proxy.Proxy, boolean)
     */
    @Override
    public void release(Proxy proxy, boolean aync) throws Exception {
        proxyManager.release(sharedProxy, aync);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.common.resource.ProxyManager#setModeOption(com.datatrees
     * .crawler.core.domain.config.operation.impl.proxyset.Option)
     */
    @Override
    public void setModeOption(Option option) {
        proxyManager.setModeOption(option);
    }
}
