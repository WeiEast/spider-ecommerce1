/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.conf.Configuration;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.page.handler.URLHandler;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.format.datetime.DateTimeFormats;
import com.treefinance.crawler.framework.format.number.NumberUnit;
import com.treefinance.crawler.framework.format.number.NumberUnitMapping;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 8:14:36 PM
 */
public class RequestUtil {

    public static LinkNode getCurrentUrl(SpiderRequest req) {
        return (LinkNode) req.getAttribute(Constants.CURRENT_LINK_NODE);
    }

    public static void setCurrentUrl(SpiderRequest req, LinkNode node) {
        req.setAttribute(Constants.CURRENT_LINK_NODE, node);
    }

    public static Integer getRetryCount(SpiderRequest req) {
        return (Integer) req.getAttribute(Constants.CRAWLER_REQUEST_RETRY_COUNT);
    }

    public static void setRetryCount(SpiderRequest req, Integer count) {
        req.setAttribute(Constants.CRAWLER_REQUEST_RETRY_COUNT, count);
    }

    public static URLHandler getURLHandler(SpiderRequest req) {
        return (URLHandler) req.getAttribute(Constants.CRAWLER_RREQUEST_URL_HANDLER);
    }

    public static void setURLHandler(SpiderRequest req, URLHandler handler) {
        req.setAttribute(Constants.CRAWLER_RREQUEST_URL_HANDLER, handler);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getRequestVisibleFields(SpiderRequest req) {
        return (Map<String, Object>) req.computeAttributeIfAbsent(Constants.RREQUEST_VISIBLE_FIELS, k -> new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public static void setRequestVisibleFields(SpiderRequest req, Map<String, Object> fields) {
        req.setAttribute(Constants.RREQUEST_VISIBLE_FIELS, fields);
    }

    public static String getCurrentTemplateId(SpiderRequest req) {
        return (String) req.getAttribute(Constants.CURRENT_SEARCH_TEMPLATE);
    }

    public static void setCurrentTemplateId(SpiderRequest req, String content) {
        req.setAttribute(Constants.CURRENT_SEARCH_TEMPLATE, content);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getPluginRuntimeConf(SpiderRequest req) {
        return (Map<String, String>) req.getAttribute(Constants.CRAWLER_RREQUEST_PLUGIN_CONF);
    }

    public static void setPluginRuntimeConf(SpiderRequest req, Map<String, String> confList) {
        req.setAttribute(Constants.CRAWLER_RREQUEST_PLUGIN_CONF, confList);
    }

    public static String getContent(SpiderRequest req) {
        return (String) req.getInput();
    }

    public static void setContent(SpiderRequest req, String content) {
        req.setInput(content);
    }

    public static String getKeyWord(SpiderRequest req) {
        return (String) req.getAttribute(Constants.CRAWLER_REQUEST_KEYWORD);
    }

    public static void setKeyWord(SpiderRequest req, String keyword) {
        req.setAttribute(Constants.CRAWLER_REQUEST_KEYWORD, keyword);
    }

    public static String getContentCharset(SpiderRequest req) {
        return (String) req.getAttribute(Constants.CRAWLER_PAGECONTENT_CHARSET);
    }

    public static void setContentCharset(SpiderRequest req, String charset) {
        req.setAttribute(Constants.CRAWLER_PAGECONTENT_CHARSET, charset);
    }

    @SuppressWarnings("unchecked")
    public static DateTimeFormats getDateFormat(SpiderRequest req) {
        return (DateTimeFormats) req.computeAttributeIfAbsent(Constants.CRAWLER_DATE_FROMAT, k -> new DateTimeFormats());
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static Map<String, NumberUnit> getNumberFormat(SpiderRequest req, Configuration configuration) {
        return (Map<String, NumberUnit>) req.computeAttributeIfAbsent(Constants.CRAWLER_REQUEST_NUMBER_MAP, key -> NumberUnitMapping.getNumberUnitMap(configuration));
    }

    public static String getSearchTemplate(SpiderRequest req) {
        return (String) req.getAttribute(Constants.CRAWLER_REQUEST_TEMPLATE);
    }

    public static void setSearchTemplate(SpiderRequest req, String template) {
        req.setAttribute(Constants.CRAWLER_REQUEST_TEMPLATE, template);
    }

    public static String getAttribute(SpiderRequest req, String key) {
        return (String) req.getAttribute(Constants.REQUEST_PREFIX + key);
    }

    public static void setAttribute(SpiderRequest req, String key, Object obj) {
        req.setAttribute(Constants.REQUEST_PREFIX + key, obj);
    }

    public static Map<String, Object> getSourceMap(SpiderRequest request) {
        Map<String, Object> sourceMap = new HashMap<String, Object>();
        AbstractProcessorContext processorContext = request.getProcessorContext();
        if (processorContext != null) {
            sourceMap.putAll(processorContext.getContext());
        }
        sourceMap.putAll(request.getRequestContext());
        sourceMap.putAll(RequestUtil.getRequestVisibleFields(request));
        return sourceMap;
    }

    /**
     * @see #getCurrentPage(SpiderRequest)
     */
    @Deprecated
    public static Page getCurrenPage(SpiderRequest req) {
        return getCurrentPage(req);
    }

    public static Page getCurrentPage(SpiderRequest req) {
        return (Page) req.getAttribute(Constants.CURRENT_PAGE);
    }

    public static void setCurrentPage(SpiderRequest req, Page page) {
        req.setAttribute(Constants.CURRENT_PAGE, page);
    }

}
