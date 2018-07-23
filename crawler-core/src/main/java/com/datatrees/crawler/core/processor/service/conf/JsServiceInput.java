/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.service.conf;

import com.google.gson.annotations.SerializedName;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 24, 2014 7:11:20 PM
 */
public class JsServiceInput {

    public String  instruction;

    @SerializedName("to_trigger_onclick")
    public boolean toTriggerOnclick;

    @SerializedName("page_id")
    public long    pageId;

    @SerializedName("page_url")
    public String  pageUrl;

    public String  referer;

    @SerializedName("last_modified_time")
    public String  lastModifiedTime;

    @SerializedName("http_proxy")
    public String  httpProxy;

    public String  cookie;

    public String  cmd;

    public static JsServiceInput create() {
        return new JsServiceInput();
    }

    public String getInstruction() {
        return instruction;
    }

    public JsServiceInput setInstruction(String instruction) {
        this.instruction = instruction;
        return this;

    }

    public boolean isToTriggerOnclick() {
        return toTriggerOnclick;
    }

    public JsServiceInput setToTriggerOnclick(boolean toTriggerOnclick) {
        this.toTriggerOnclick = toTriggerOnclick;
        return this;
    }

    public long getPageId() {
        return pageId;
    }

    public JsServiceInput setPageId(long pageId) {
        this.pageId = pageId;
        return this;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public JsServiceInput setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public String getReferer() {
        return referer;
    }

    public JsServiceInput setReferer(String referer) {
        this.referer = referer;
        return this;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public JsServiceInput setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
        return this;
    }

    public String getHttpProxy() {
        return httpProxy;
    }

    public JsServiceInput setHttpProxy(String httpProxy) {
        this.httpProxy = httpProxy;
        return this;
    }

    public String getCookie() {
        return cookie;
    }

    public JsServiceInput setCookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public String getCmd() {
        return cmd;
    }

    public JsServiceInput setCmd(String cmd) {
        this.cmd = cmd;
        return this;
    }
}
