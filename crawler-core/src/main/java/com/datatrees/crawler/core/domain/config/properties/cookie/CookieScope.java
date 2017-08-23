/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.properties.cookie;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:33:45 AM
 */
public enum CookieScope implements Serializable {
    REQUEST("request"),
    USER_SESSION("user_session"),
    SESSION("session"),
    CUSTOM("custom");
    private static Map<String, CookieScope> scopeMap = new HashMap<String, CookieScope>();

    static {
        for (CookieScope obj : values()) {
            scopeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    CookieScope(String value) {
        this.value = value;
    }

    public static CookieScope getCookieScope(String value) {
        return scopeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

}
