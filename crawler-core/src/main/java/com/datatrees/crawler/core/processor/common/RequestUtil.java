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
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.page.handler.URLHandler;
import com.treefinance.crawler.framework.format.datetime.DateTimeFormats;
import com.treefinance.crawler.framework.format.number.NumberUnit;
import com.treefinance.crawler.framework.format.number.NumberUnitMapping;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 8:14:36 PM
 */
public class RequestUtil {

    public static LinkNode getCurrentUrl(Request req) {
        return (LinkNode) req.getAttribute(Constants.CURRENT_LINK_NODE);
    }

    public static void setCurrentUrl(Request req, LinkNode node) {
        req.setAttribute(Constants.CURRENT_LINK_NODE, node);
    }

    public static Integer getRetryCount(Request req) {
        return (Integer) req.getAttribute(Constants.CRAWLER_REQUEST_RETRY_COUNT);
    }

    public static void setRetryCount(Request req, Integer count) {
        req.setAttribute(Constants.CRAWLER_REQUEST_RETRY_COUNT, count);
    }

    public static URLHandler getURLHandler(Request req) {
        return (URLHandler) req.getAttribute(Constants.CRAWLER_RREQUEST_URL_HANDLER);
    }

    public static void setURLHandler(Request req, URLHandler handler) {
        req.setAttribute(Constants.CRAWLER_RREQUEST_URL_HANDLER, handler);
    }

    public static void setContext(Request req, Map<String, Object> context) {
        req.setAttribute(Constants.CRAWLER_RREQUEST_CONTEXT, context);
    }

    public static void setConf(Request req, Configuration conf) {
        req.setAttribute(Constants.CRAWLER_RREQUEST_CONF, conf);
    }

    public static Configuration getConf(Request req) {
        Configuration configuration = (Configuration) req.getAttribute(Constants.CRAWLER_RREQUEST_CONF);
        if (configuration == null) {
            configuration = PropertiesConfiguration.getInstance();
        }
        return configuration;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getContext(Request req) {
        Map<String, Object> context = (Map<String, Object>) req.getAttribute(Constants.CRAWLER_RREQUEST_CONTEXT);
        if (context == null) {
            context = new HashMap<String, Object>();
            req.setAttribute(Constants.CRAWLER_RREQUEST_CONTEXT, context);
        }
        return context;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getRequestVisibleFields(Request req) {
        Map<String, Object> fields = (Map<String, Object>) req.getAttribute(Constants.RREQUEST_VISIBLE_FIELS);
        if (fields == null) {
            fields = new HashMap<String, Object>();
            req.setAttribute(Constants.RREQUEST_VISIBLE_FIELS, fields);
        }
        return fields;
    }

    @SuppressWarnings("unchecked")
    public static void setRequestVisibleFields(Request req, Map<String, Object> fields) {
        req.setAttribute(Constants.RREQUEST_VISIBLE_FIELS, fields);
    }

    public static String getCurrentTemplateId(Request req) {
        return (String) req.getAttribute(Constants.CURRENT_SEARCH_TEMPLATE);
    }

    public static void setCurrentTemplateId(Request req, String content) {
        req.setAttribute(Constants.CURRENT_SEARCH_TEMPLATE, content);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getPluginRuntimeConf(Request req) {
        return (Map<String, String>) req.getAttribute(Constants.CRAWLER_RREQUEST_PLUGIN_CONF);
    }

    public static void setPluginRuntimeConf(Request req, Map<String, String> confList) {
        req.setAttribute(Constants.CRAWLER_RREQUEST_PLUGIN_CONF, confList);
    }

    public static String getContent(Request req) {
        return (String) req.getInput();
    }

    public static void setContent(Request req, String content) {
        req.setInput(content);
    }

    public static String getKeyWord(Request req) {
        return (String) req.getAttribute(Constants.CRAWLER_REQUEST_KEYWORD);
    }

    public static void setKeyWord(Request req, String keyword) {
        req.setAttribute(Constants.CRAWLER_REQUEST_KEYWORD, keyword);
    }

    public static String getContentCharset(Request req) {
        return (String) req.getAttribute(Constants.CRAWLER_PAGECONTENT_CHARSET);
    }

    public static void setContentCharset(Request req, String charset) {
        req.setAttribute(Constants.CRAWLER_PAGECONTENT_CHARSET, charset);
    }

    public static AbstractProcessorContext getProcessorContext(Request req) {
        return (AbstractProcessorContext) req.getAttribute(Constants.PROCESSER_CONTEXT);
    }

    public static void setProcessorContext(Request req, AbstractProcessorContext context) {
        req.setAttribute(Constants.PROCESSER_CONTEXT, context);
    }

    public static Website getWebsite(Request req) {
        return (Website) req.getAttribute(Constants.PARSER_WEBSITE_CONFIG);
    }

    public static void setWebsite(Request req, Website website) {
        req.setAttribute(Constants.PARSER_WEBSITE_CONFIG, website);
    }

    @SuppressWarnings("unchecked")
    public static DateTimeFormats getDateFormat(Request req) {
        return (DateTimeFormats) req.computeAttributeIfAbsent(Constants.CRAWLER_DATE_FROMAT, k -> new DateTimeFormats());
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static Map<String, NumberUnit> getNumberFormat(Request req, Configuration configuration) {
        return (Map<String, NumberUnit>) req.computeAttributeIfAbsent(Constants.CRAWLER_REQUEST_NUMBER_MAP, key -> NumberUnitMapping.getNumberUnitMap(configuration));
    }

    public static String getSearchTemplate(Request req) {
        return (String) req.getAttribute(Constants.CRAWLER_REQUEST_TEMPLATE);
    }

    public static void setSearchTemplate(Request req, String template) {
        req.setAttribute(Constants.CRAWLER_REQUEST_TEMPLATE, template);
    }

    public static String getAttribute(Request req, String key) {
        return (String) req.getAttribute(Constants.REQUEST_PREFIX + key);
    }

    public static void setAttribute(Request req, String key, Object obj) {
        req.setAttribute(Constants.REQUEST_PREFIX + key, obj);
    }

    public static Map<String, Object> getSourceMap(Request request) {
        Map<String, Object> sourceMap = new HashMap<String, Object>();
        if (RequestUtil.getProcessorContext(request) != null) {
            sourceMap.putAll(RequestUtil.getProcessorContext(request).getContext());
        }
        sourceMap.putAll(RequestUtil.getContext(request));
        sourceMap.putAll(RequestUtil.getRequestVisibleFields(request));
        return sourceMap;
    }

    public static Page getCurrenPage(Request req) {
        return (Page) req.getAttribute(Constants.CURRENT_PAGE);
    }

    public static void setCurrentPage(Request req, Page page) {
        req.setAttribute(Constants.CURRENT_PAGE, page);
    }

}
