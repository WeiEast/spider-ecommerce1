/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model.subtask;

import java.util.HashMap;

import com.datatrees.crawler.core.processor.proxy.Proxy;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月19日 下午4:25:02
 */
public class SubSeed extends HashMap {

    public final static String WEBSITE_NAME       = "websiteName";
    public final static String UNIQUE_SUFFIX      = "uniqueSuffix";
    public final static String SYNC               = "sync";
    public final static String MUTEX              = "mutex";
    public final static String TEMPLATEID         = "templateId";// 包含多个“,”分割
    public final static String SEEDURL            = "seedurl";
    public final static String WAITING            = "waiting";// waiting waitingMillis or 'parentTask'
    public final static String LOGIN_CHECK_IGNORE = "loginCheckIgnore";
    public final static String NO_STATUS          = "noStatus";
    public final static String PROXY_SHARED       = "proxyShared";// 代理共享，父子任务持续使用代理
    public final static String PROXY              = "proxy";// 代理共享，父子任务持续使用代理

    public Proxy getProxy() {
        return (Proxy) this.get(PROXY);
    }

    public void setProxy(Proxy proxy) {
        this.put(PROXY, proxy);
    }

    public Boolean getProxyShared() {
        return (Boolean) this.get(PROXY_SHARED);
    }

    public void setProxyShared(Boolean proxyShared) {
        this.put(PROXY_SHARED, proxyShared);
    }

    /**
     * @return the loginCheckIgnore
     */
    public Boolean getLoginCheckIgnore() {
        return (Boolean) this.get(LOGIN_CHECK_IGNORE);
    }

    /**
     * @param loginCheckIgnore the loginCheckIgnore to set
     */
    public void setLoginCheckIgnore(Boolean loginCheckIgnore) {
        this.put(LOGIN_CHECK_IGNORE, loginCheckIgnore);
    }

    public String getWaiting() {
        return (String) this.get(WAITING);
    }

    public void setWaiting(String waiting) {
        this.put(WAITING, waiting);
    }

    /**
     * @return the seedurl
     */
    public String getSeedUrl() {
        return (String) this.get(SEEDURL);
    }

    /**
     * @param url the url to set
     */
    public void setSeedUrl(String seedurl) {
        this.put(SEEDURL, seedurl);
    }

    /**
     * @return the url
     */
    public String getTemplateId() {
        return (String) this.get(TEMPLATEID);
    }

    /**
     * @param url the url to set
     */
    public void setTemplateId(String templateId) {
        this.put(TEMPLATEID, templateId);
    }

    /**
     * @return the mutex
     */
    public Boolean isMutex() {
        return (Boolean) this.get(MUTEX);
    }

    /**
     * @param mutex the mutex to set
     */
    public void setMutex(boolean mutex) {
        this.put(MUTEX, mutex);
    }

    /**
     * @return the sync
     */
    public Boolean isSync() {
        return (Boolean) this.get(SYNC);
    }

    /**
     * @param sync the sync to set
     */
    public void setSync(boolean sync) {
        this.put(SYNC, sync);
    }

    /**
     * @return the uniqueSuffix
     */
    public String getUniqueSuffix() {
        return (String) this.get(UNIQUE_SUFFIX);
    }

    /**
     * @param uniqueSuffix the uniqueSuffix to set
     */
    public void setUniqueSuffix(String uniqueSuffix) {
        this.put(UNIQUE_SUFFIX, uniqueSuffix);
    }

    /**
     * @return the websiteName
     */
    public String getWebsiteName() {
        return (String) this.get(WEBSITE_NAME);
    }

    /**
     * @param websiteName the websiteName to set
     */
    public void setWebsiteName(String websiteName) {
        this.put(WEBSITE_NAME, websiteName);
    }

    public Boolean noStatus() {
        return (Boolean) this.get(NO_STATUS);
    }

}
