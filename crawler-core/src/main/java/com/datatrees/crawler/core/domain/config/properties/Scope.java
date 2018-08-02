/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:34:34 AM
 */
public enum Scope {
    REQUEST("request"),
    SESSION("session");

    private static Map<String, Scope> scopeMap = new HashMap<String, Scope>();

    static {
        for (Scope obj : values()) {
            scopeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    Scope(String value) {
        this.value = value;
    }

    public static Scope getScope(String value) {
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
