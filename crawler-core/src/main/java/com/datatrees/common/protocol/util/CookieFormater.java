/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2014
 */
package com.datatrees.common.protocol.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jul 29, 2014 1:51:32 PM
 */
public enum CookieFormater {
    INSTANCE;


    private static final Logger logger = LoggerFactory.getLogger(CookieFormater.class);

    /**
     * 
     * @param cookies
     * @return
     */
    public String listToString(Map<String, String> cookies) {
        String append = "; ";
        StringBuilder result = new StringBuilder();
        if (MapUtils.isNotEmpty(cookies)) {
            for (Entry<String, String> entry : cookies.entrySet()) {
                result.append(entry).append(append);
            }
            return result.toString().substring(0, (result.length() - 2));
        }
        return result.toString();
    }

    public String listToString(String[] cookies) {
        String append = "; ";
        StringBuilder result = new StringBuilder();
        if (cookies != null) {
            for (String cookie : cookies) {
                result.append(cookie).append(append);
            }
            return result.toString().substring(0, (result.length() - 2));
        }
        return result.toString();
    }

    public String parserCookie(String[] cookieVals) {
        return parserCookie(cookieVals, false);
    }

    public String parserCookie(String[] cookieVals, boolean retainQuote) {
        return listToString(this.parserCookietToMap(cookieVals, retainQuote));
    }

    public Map<String, String> parserCookietToMap(String[] cookieVals) {
        return parserCookietToMap(cookieVals, false);
    }

    public Map<String, String> parserCookietToMap(String[] cookieVals, boolean retainQuote) {
        Map<String, String> cookieMap = new HashMap<String, String>();
        if (ArrayUtils.isNotEmpty(cookieVals)) {
            for (String one : cookieVals) {
                this.cookieFormat(one, cookieMap, retainQuote);
            }
        }
        return cookieMap;
    }

    public String parserCookie(String cookieVals, boolean retainQuote) {
        return listToString(parserCookieToMap(cookieVals, retainQuote));
    }

    public Map<String, String> parserCookieToMap(String cookieVals) {
        return parserCookieToMap(cookieVals, false);
    }


    public Map<String, String> parserCookieToMap(String cookieVals, boolean retainQuote) {
        Map<String, String> cookieMap = null;
        if (cookieVals != null) {
            cookieMap = parserCookietToMap(cookieVals.split(";"), retainQuote);
        } else {
            cookieMap = new HashMap<String, String>();
        }
        return cookieMap;
    }

    public String[] parserCookieToArray(String cookieVals, boolean retainQuote) {
        if (cookieVals != null) {
            return parserCookieToArray(cookieVals.split(";"), retainQuote);
        } else {
            return new String[0];
        }
    }

    public String[] parserCookieToArray(String[] cookieVals, boolean retainQuote) {
        String[] cookieArray = new String[0];
        if (cookieVals != null) {
            for (String one : cookieVals) {
                cookieArray = this.cookieFormat(one, cookieArray, retainQuote);
            }
        }
        return cookieArray;
    }

    /*
     * public Cookie[] parserCookies(String cookieVals, String domain) { String[] cookieStringArrays
     * = cookieVals.split(";"); if (ArrayUtils.isNotEmpty(cookieStringArrays)) { Cookie[] cookies =
     * new Cookie[cookieStringArrays.length]; for (int i = 0; i < cookieStringArrays.length; i++) {
     * HttpCookie tempCookie = HttpCookie.parse(cookieStringArrays[i]).get(0); cookies[i] = new
     * Cookie(domain, tempCookie.getName(), tempCookie.getValue()); } return cookies; } return null;
     * }
     */

    private void cookieFormat(String one, Map<String, String> cookieMap, boolean retainQuote) {
        try {
            List<HttpCookie> cookies = HttpCookie.parse(one, false, retainQuote);
            HttpCookie cookie = cookies.get(0);
            if (!cookie.hasExpired()) {
                logger.debug("set cookie:\t" + cookie.getName() + " value: " + cookie.getValue());
                cookieMap.put(cookie.getName().trim(), cookie.getValue());
            } else {
                logger.debug("cookie:\t" + cookie.getName() + " value: " + cookie.getValue() + " has expired.");
            }
        } catch (Exception e) {
            logger.error(one + " parser cookie error!", e);
        }
    }

    private String[] cookieFormat(String one, String[] cookieArray, boolean retainQuote) {
        try {
            List<HttpCookie> cookies = HttpCookie.parse(one, false, retainQuote);
            HttpCookie cookie = cookies.get(0);
            if (!cookie.hasExpired()) {
                logger.debug("set cookie:\t" + cookie.getName() + " value: " + cookie.getValue());
                cookieArray = (String[]) ArrayUtils.add(cookieArray, cookie.getName().trim() + "=" + cookie.getValue());
            } else {
                logger.debug("cookie:\t" + cookie.getName() + " value: " + cookie.getValue() + " has expired.");
            }
        } catch (Exception e) {
            logger.error(one + " parser cookie error!", e);
        }
        return cookieArray;
    }

}
