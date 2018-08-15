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

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.page.handler.URLHandler;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 1:48:24 PM
 */
public class CrawlRequest extends Request {

    private CrawlRequest() {
        super();
    }

    public String getSearchTemplateId() {
        return RequestUtil.getCurrentTemplateId(this);
    }

    public void setSearchTemplateId(String searchTemplateId) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(searchTemplateId));
        RequestUtil.setCurrentTemplateId(this, searchTemplateId);
    }

    public LinkNode getUrl() {
        return RequestUtil.getCurrentUrl(this);
    }

    public void setUrl(LinkNode url) {
        RequestUtil.setCurrentUrl(this, url);
    }

    public String getSearchTemplate() {
        return RequestUtil.getSearchTemplate(this);
    }

    public void setSearchTemplate(String searchTemplate) {
        RequestUtil.setSearchTemplate(this, searchTemplate);
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

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private LinkNode               url;
        private SearchProcessorContext searchContext;
        private String                 templateId;
        private String                 seedUrl;
        private URLHandler             urlHandler;

        private Builder() {
        }

        public Builder setUrl(LinkNode url) {
            this.url = url;
            return this;
        }

        public Builder setSearchContext(SearchProcessorContext context) {
            this.searchContext = context;
            return this;
        }

        public Builder setTemplateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public Builder setSeedUrl(String seedUrl) {
            this.seedUrl = seedUrl;
            return this;
        }

        public Builder setUrlHandler(URLHandler urlHandler) {
            this.urlHandler = urlHandler;
            return this;
        }

        public CrawlRequest build() {
            com.treefinance.toolkit.util.Preconditions.notNull("searchUrl", url);
            com.treefinance.toolkit.util.Preconditions.notNull("searchContext", searchContext);
            com.treefinance.toolkit.util.Preconditions.notEmpty("templateId", templateId);

            CrawlRequest crawlRequest = new CrawlRequest();
            crawlRequest.setUrl(url);
            crawlRequest.setProcessorContext(searchContext);
            crawlRequest.setSearchTemplateId(templateId);
            crawlRequest.setSearchTemplate(seedUrl);
            crawlRequest.setUrlHandler(urlHandler);

            crawlRequest.addRequestContext(linkNodeToMap(url));
            crawlRequest.addRequestContext(searchContext.getContext());

            return crawlRequest;
        }

        private Map<String, Object> linkNodeToMap(LinkNode url) {
            Map<String, Object> map = new HashMap<>();

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
    }
}
