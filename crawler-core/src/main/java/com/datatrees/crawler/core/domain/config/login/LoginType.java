/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.login;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:55:58 AM
 */
public enum LoginType {
    SERVER("server"),
    CLIENT("client"),
    APP("app"),
    PLUGIN("plugin"),
    NONE("none");
    private static Map<String, LoginType> LoginTypeMap = new HashMap<String, LoginType>();

    static {
        for (LoginType obj : values()) {
            LoginTypeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    LoginType(String value) {
        this.value = value;
    }

    public static LoginType getLoginType(String value) {
        return LoginTypeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
