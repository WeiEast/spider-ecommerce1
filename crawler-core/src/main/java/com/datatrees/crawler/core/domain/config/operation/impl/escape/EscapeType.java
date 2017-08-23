/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl.escape;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:24:48 PM
 */
public enum EscapeType {
    HTML("html"),
    JAVA("java"),
    JS("js"),
    XML("xml");
    private static Map<String, EscapeType> escapeTypeMap = new HashMap<String, EscapeType>();

    static {
        for (EscapeType obj : values()) {
            escapeTypeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    EscapeType(String value) {
        this.value = value;
    }

    public static EscapeType getEscapeType(String value) {
        return escapeTypeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
