/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.util.CookieFormater;
import com.datatrees.crawler.core.domain.Cookie;
import com.datatrees.crawler.core.domain.WebsiteAccount;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 8:14:36 PM
 */
public class ProcessorContextUtil {

    public static void setCookieString(AbstractProcessorContext context, String cookieString) {
        if (StringUtils.isBlank(cookieString)) {
            return;
        }
        context.addAttribute(Constants.COOKIE_STRING, cookieString);
        boolean retainQuote = context instanceof SearchProcessorContext && ((SearchProcessorContext) context).getCookieConf() != null ? ((SearchProcessorContext) context).getCookieConf().getRetainQuote() : false;
        Map<String, String> cookieMap = CookieFormater.INSTANCE.parserCookieToMap(cookieString, retainQuote);
        context.addAttribute(Constants.COOKIE, cookieMap);
    }

    public static Map<String, String> getCookieMap(AbstractProcessorContext context) {
        Map<String, String> cookieMap = (Map<String, String>) context.getAttribute(Constants.COOKIE);
        return cookieMap == null ? new HashMap<String, String>() : cookieMap;
    }

    public static String getCookieString(AbstractProcessorContext context) {
        return (String) context.getAttribute(Constants.COOKIE_STRING);
    }

    public static void setCookieObject(AbstractProcessorContext context, Cookie cookie) {
        context.addAttribute(Constants.USERNAME, cookie.getUserName());
        ProcessorContextUtil.setCookieString(context, cookie.getCookie());
    }

    public static void setAccountKey(AbstractProcessorContext context, String accountKey) {
        context.addAttribute(Constants.ACCOUNT_KEY, accountKey);
    }

    public static String getAccountKey(AbstractProcessorContext context) {
        return (String) context.getAttribute(Constants.ACCOUNT_KEY);
    }

    public static void setAccount(AbstractProcessorContext context, WebsiteAccount account) {
        context.addAttribute(Constants.USERNAME, account.getUserName());
        context.addAttribute(Constants.PASSWORD, account.getPassword());
    }

    public static void setValue(AbstractProcessorContext context, String key, Object value) {
        context.addAttribute(key, value);
    }

    public static void addValues(AbstractProcessorContext context, Map values) {
        context.addAttributes(values);
    }

    public static Object getValue(AbstractProcessorContext context, String key) {
        return context.getAttribute(key);
    }

    public static void setKeyword(AbstractProcessorContext context, String keyword) {
        context.addAttribute(Constants.PAGE_REQUEST_CONTEXT_KEYWORD, keyword);
        context.addAttribute(Constants.PAGE_REQUEST_CONTEXT_ORIGINAL_KEYWORD, keyword);
    }

    public static void setTaskUnique(AbstractProcessorContext context, Object obj) {
        context.addAttribute(Constants.TASK_UNIQUE_SIGN, obj);
    }

    public static Object getTaskUnique(AbstractProcessorContext context) {
        return context.getAttribute(Constants.TASK_UNIQUE_SIGN);
    }

    public static void addThreadLocalResponse(AbstractProcessorContext context, Response response) {
        Object responseList = context.computeThreadAttrIfAbsent(Thread.currentThread(), Constants.THREAD_LOCAL_RESPONSE,k -> new ArrayList<Response>());
        ((List) responseList).add(response);
    }

    public static List<Response> getThreadLocalResponseList(AbstractProcessorContext context) {
        return (List<Response>) context.getThreadAttr(Thread.currentThread(), Constants.THREAD_LOCAL_RESPONSE);
    }

    public static void clearThreadLocalResponseList(AbstractProcessorContext context) {
        context.removeThreadAttr(Thread.currentThread(), Constants.THREAD_LOCAL_RESPONSE);
    }

    public static void addThreadLocalLinkNode(AbstractProcessorContext context, LinkNode linkNode) {
        Object linkNodeList = context.computeThreadAttrIfAbsent(Thread.currentThread(), Constants.THREAD_LOCAL_LINKNODE, k -> new ArrayList<LinkNode>());
        ((List) linkNodeList).add(linkNode);
    }

    public static List<LinkNode> getThreadLocalLinkNode(AbstractProcessorContext context) {
        return (List<LinkNode>) context.getThreadAttr(Thread.currentThread(), Constants.THREAD_LOCAL_LINKNODE);
    }

    public static void clearThreadLocalLinkNode(AbstractProcessorContext context) {
        context.removeThreadAttr(Thread.currentThread(), Constants.THREAD_LOCAL_LINKNODE);
    }

    public static void setHttpState(AbstractProcessorContext context, HttpState state) {
        context.addAttribute(Constants.HTTP_STATE, state);
    }

    public static HttpState getHttpState(AbstractProcessorContext context) {
        return (HttpState) context.getAttribute(Constants.HTTP_STATE);
    }

}
