/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2014
 */

package com.datatrees.common.protocol.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 13, 2014 5:31:53 PM
 */
public class CookieParser {

    public static final Logger LOG        = LoggerFactory.getLogger(CookieParser.class);

    public static final String LINE_SPLIT = ";";

    public static final String PAIR_SPLIT = "=";

    public static List<Cookie> getCookies(String domain, String lineSplit, String keySplit, String data) {
        List<Cookie> cookies = new ArrayList<Cookie>();

        lineSplit = StringUtils.defaultIfEmpty(lineSplit, LINE_SPLIT);
        keySplit = StringUtils.defaultIfEmpty(keySplit, PAIR_SPLIT);

        String[] pairs = split(data, lineSplit);
        if (ArrayUtils.isNotEmpty(pairs)) {
            Cookie ck = null;
            for (String pair : pairs) {
                String[] cookiePairs = split(pair, keySplit);
                if (cookiePairs.length >= 2) {
                    ck = new Cookie(domain, cookiePairs[0], cookiePairs[1], "/", 1577808000, false);
                    cookies.add(ck);
                }
            }
        }
        return cookies;
    }

    public static String[] split(String data, String splitChar) {
        if (StringUtils.isNotEmpty(data) && StringUtils.isNotEmpty(splitChar)) {
            return StringUtils.split(data, splitChar);
        }
        return null;
    }

    public static String formatCookie(Cookie cookie) {
        LOG.trace("enter CookieSpecBase.formatCookie(Cookie)");
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        StringBuffer buf = new StringBuffer();
        buf.append(cookie.getName());
        buf.append("=");
        String s = cookie.getValue();
        if (s != null) {
            buf.append(s);
        }
        return buf.toString();
    }

    public static String formatCookieFull(Cookie cookie) {
        LOG.trace("enter CookieSpecBase.formatCookie(Cookie)");
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        StringBuffer buf = new StringBuffer();
        buf.append(cookie.getName());
        buf.append("=");
        String s = cookie.getValue();
        if (s != null) {
            buf.append(s);
        }
        if (cookie.getDomain() != null) {
            buf.append("; domain=" + cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            buf.append("; path=" + cookie.getPath());
        }
        return buf.toString();
    }

    public static String formatCookies(Cookie[] cookies) throws IllegalArgumentException {
        LOG.trace("enter CookieSpecBase.formatCookies(Cookie[])");
        if (cookies == null) {
            throw new IllegalArgumentException("Cookie array may not be null");
        }
        if (cookies.length == 0) {
            throw new IllegalArgumentException("Cookie array may not be empty");
        }

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < cookies.length; i++) {
            if (i > 0) {
                buffer.append("; ");
            }
            buffer.append(formatCookie(cookies[i]));
        }
        return buffer.toString();
    }

    public static String formatCookies(Header[] headers) throws IllegalArgumentException {
        LOG.trace("enter CookieSpecBase.formatCookies(Cookie[])");
        if (headers == null) {
            throw new IllegalArgumentException("Cookie array may not be null");
        }
        if (headers.length == 0) {
            throw new IllegalArgumentException("Cookie array may not be empty");
        }
        List<String> cookieList = new ArrayList<String>();
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].getName().equals("Cookie")) {
                cookieList.add(headers[i].getValue());
            }
        }
        return StringUtils.join(cookieList, "; ");
    }

    /**
     * not imple
     * @param cookie
     * @param url
     * @return
     */
    public static HttpState createStateFromCookie(String cookie, String url) {
        HttpState state = new HttpState();
        String website = com.datatrees.common.util.StringUtils.getWebDomain(url);
        List<Cookie> cks = getCookies(website, null, null, cookie);
        state.addCookies(cks.toArray(new Cookie[cks.size()]));
        return state;
    }

}
