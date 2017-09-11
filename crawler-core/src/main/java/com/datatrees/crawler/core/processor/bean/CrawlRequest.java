/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.bean;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.page.handler.URLHandler;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 1:48:24 PM
 */
public class CrawlRequest extends Request {

    private CrawlRequest() {
        super();
    }

    public static CrawlRequest build() {
        return new CrawlRequest();
    }

    public String getSearchTemplateId() {
        return RequestUtil.getCurrentTemplateId(this);
    }

    public CrawlRequest setSearchTemplateId(String searchTemplateId) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(searchTemplateId));
        RequestUtil.setCurrentTemplateId(this, searchTemplateId);
        return this;
    }

    public AbstractProcessorContext getProcessorContext() {
        return RequestUtil.getProcessorContext(this);
    }

    public CrawlRequest setProcessorContext(AbstractProcessorContext context) {
        RequestUtil.setProcessorContext(this, context);
        return this;
    }

    public LinkNode getUrl() {
        return RequestUtil.getCurrentUrl(this);
    }

    public CrawlRequest setUrl(LinkNode url) {
        RequestUtil.setCurrentUrl(this, url);
        return this;
    }

    private Map<? extends String, ? extends Object> linkNodeToMap(LinkNode url) {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put(Constants.PAGE_REQUEST_CONTEXT_CURRENT_URL, url.getUrl());
        map.put(Constants.PAGE_REQUEST_CONTEXT_CURRENT_DEPTH, url.getDepth() + "");
        if (StringUtils.isBlank(url.getReferer())) {
            map.put(Constants.PAGE_REQUEST_CONTEXT_REFERER_URL, url.getUrl());
        } else {
            map.put(Constants.PAGE_REQUEST_CONTEXT_REFERER_URL, url.getReferer());
        }
        map.put(Constants.PAGE_REQUEST_CONTEXT_REDIRECT_URL, url.getRedirectUrl());
        map.put(Constants.PAGE_REQUEST_CONTEXT_PAGE_TITLE, url.getPageTitle());
        map.putAll(url.getHeaders());
        map.putAll(url.getPropertys());
        return map;
    }

    public CrawlRequest contextInit() { //
        this.getContext().putAll(linkNodeToMap(this.getUrl()));
        this.getContext().putAll(this.getProcessorContext().getContext());
        return this;
    }

    public Map<String, Object> getContext() {
        Map<String, Object> context = RequestUtil.getContext(this);
        if (context == null) {
            context = new HashMap<String, Object>();
            RequestUtil.setContext(this, context);
        }
        return context;
    }

    public CrawlRequest setContext(Map<String, Object> context) {
        RequestUtil.setContext(this, context);
        return this;
    }

    public String getSearchTemplate() {
        return RequestUtil.getSearchTemplate(this);
    }

    public CrawlRequest setSearchTemplate(String searchTemplate) {
        RequestUtil.setSearchTemplate(this, searchTemplate);
        return this;
    }

    public Configuration getConf() {
        return RequestUtil.getConf(this);
    }

    public CrawlRequest setConf(Configuration conf) {
        RequestUtil.setConf(this, conf);
        return this;
    }

    public URLHandler getUrlHandler() {
        return RequestUtil.getURLHandler(this);
    }

    public CrawlRequest setUrlHandler(URLHandler handler) {
        RequestUtil.setURLHandler(this, handler);
        return this;
    }

    @Override
    public String toString() {
        return "Request [url=" + getUrl().toString() + ", template=" + getSearchTemplateId() + "]";
    }

}
