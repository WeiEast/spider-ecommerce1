/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.extractor;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月21日 下午1:23:40
 */
public enum FieldVisibleType {
    REQUEST("request"),
    CONTEXT("context"),
    PROCESSOR_RESULT("processor_result");
    private static Map<String, FieldVisibleType> fieldVisibleTypeMap = new HashMap<String, FieldVisibleType>();

    static {
        for (FieldVisibleType obj : values()) {
            fieldVisibleTypeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    FieldVisibleType(String value) {
        this.value = value;
    }

    public static FieldVisibleType getFieldVisibleType(String value) {
        return fieldVisibleTypeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
