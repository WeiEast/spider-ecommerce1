/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpState;
import org.apache.commons.lang.StringUtils;

import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.util.CookieFormater;
import com.datatrees.crawler.core.domain.Cookie;
import com.datatrees.crawler.core.domain.WebsiteAccount;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 8:14:36 PM
 */
public class ProcessorContextUtil {

    public static void setCookieString(AbstractProcessorContext context, String cookieString) {
        context.getContext().put(Constants.COOKIE_STRING, cookieString);
        boolean retainQuote = context instanceof SearchProcessorContext && ((SearchProcessorContext) context).getCookieConf() != null
                ? ((SearchProcessorContext) context).getCookieConf().getRetainQuote()
                : false;
        Map<String, String> cookieMap = CookieFormater.INSTANCE.parserCookieToMap(cookieString, retainQuote);
        context.getContext().put(Constants.COOKIE, cookieMap);
    }

    public static Map<String, String> getCookieMap(AbstractProcessorContext context) {
        Map<String, String> cookieMap = (Map<String, String>) context.getContext().get(Constants.COOKIE);
        return cookieMap == null ? new HashMap<String, String>() : cookieMap;
    }

    public static String getCookieString(AbstractProcessorContext context) {
        return (String) context.getContext().get(Constants.COOKIE_STRING);
    }

    public static void setCookieObject(AbstractProcessorContext context, Cookie cookie) {
        context.getContext().put(Constants.USERNAME, cookie.getUserName());
        ProcessorContextUtil.setCookieString(context, cookie.getCookie());
    }

    public static void setAccountKey(AbstractProcessorContext context, String accountKey) {
        context.getContext().put(Constants.ACCOUNT_KEY, accountKey);
    }

    public static String getAccountKey(AbstractProcessorContext context) {
        return (String) context.getContext().get(Constants.ACCOUNT_KEY);
    }

    public static void setAccount(AbstractProcessorContext context, WebsiteAccount account) {
        context.getContext().put(Constants.USERNAME, account.getUserName());
        context.getContext().put(Constants.PASSWORD, account.getPassword());
    }

    public static void setValue(AbstractProcessorContext context, String key, Object value) {
        context.getContext().put(key, value);
    }

    public static void addValues(AbstractProcessorContext context, Map values) {
        context.getContext().putAll(values);
    }

    public static Object getValue(AbstractProcessorContext context, String key) {
        return context.getContext().get(key);
    }

    public static void setKeyword(AbstractProcessorContext context, String keyword) {
        context.getContext().put(Constants.PAGE_REQUEST_CONTEXT_KEYWORD, keyword);
        context.getContext().put(Constants.PAGE_REQUEST_CONTEXT_ORIGINAL_KEYWORD, keyword);
    }

    public static void setTaskUnique(AbstractProcessorContext context, Object obj) {
        context.getContext().put(Constants.TASK_UNIQUE_SIGN, obj);
    }

    public static Object getTaskUnique(AbstractProcessorContext context) {
        return context.getContext().get(Constants.TASK_UNIQUE_SIGN);
    }


    @SuppressWarnings("unchecked")
    private static Map<String, Object> getThreadLocalContext(AbstractProcessorContext context) {
        Map<String, Object> threadLocalContext = (Map<String, Object>) context.getThreadContext().get(Thread.currentThread());
        if (threadLocalContext == null) {
            threadLocalContext = new HashMap<String, Object>();
            context.getThreadContext().put(Thread.currentThread(), threadLocalContext);
        }
        return threadLocalContext;
    }

    public static void addThreadLocalResponse(AbstractProcessorContext context, Response response) {
        Map<String, Object> threadLocalContext = getThreadLocalContext(context);
        Object responseList = threadLocalContext.get(Constants.THREAD_LOCAL_RESPONSE);
        if (responseList == null) {
            responseList = new ArrayList<Response>();
            threadLocalContext.put(Constants.THREAD_LOCAL_RESPONSE, responseList);
        }
        ((List) responseList).add(response);
    }

    public static List<Response> getThreadLocalResponseList(AbstractProcessorContext context) {
        return (List<Response>) getThreadLocalContext(context).get(Constants.THREAD_LOCAL_RESPONSE);
    }

    public static void clearThreadLocalResponseList(AbstractProcessorContext context) {
        getThreadLocalContext(context).put(Constants.THREAD_LOCAL_RESPONSE, null);
    }

    public static void addThreadLocalLinkNode(AbstractProcessorContext context, LinkNode linkNode) {
        Map<String, Object> threadLocalContext = getThreadLocalContext(context);
        Object linkNodeList = threadLocalContext.get(Constants.THREAD_LOCAL_LINKNODE);
        if (linkNodeList == null) {
            linkNodeList = new ArrayList<LinkNode>();
            threadLocalContext.put(Constants.THREAD_LOCAL_LINKNODE, linkNodeList);
        }
        ((List) linkNodeList).add(linkNode);
    }

    public static void clearThreadLocalLinkNode(AbstractProcessorContext context) {
        getThreadLocalContext(context).put(Constants.THREAD_LOCAL_LINKNODE, null);
    }

    public static List<LinkNode> getThreadLocalLinkNode(AbstractProcessorContext context) {
        return (List<LinkNode>) getThreadLocalContext(context).get(Constants.THREAD_LOCAL_LINKNODE);
    }


    public static void setHttpState(AbstractProcessorContext context, HttpState state) {
        context.getContext().put(Constants.HTTP_STATE, state);
    }

    public static HttpState getHttpState(AbstractProcessorContext context) {
        return (HttpState) context.getContext().get(Constants.HTTP_STATE);
    }

}
