/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午11:32:45
 */
public enum ResultType {
    EBANKBILL("EBANKBILL"),
    MAILBILL("MAILBILL"),
    OPERATOR("OPERATOR"),
    ECOMMERCE("ECOMMERCE"),
    DEFAULT("DEFAULT");

    private static Map<String, ResultType> ResultTypeMap = new HashMap<String, ResultType>();

    static {
        for (ResultType obj : values()) {
            ResultTypeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    ResultType(String value) {
        this.value = value;
    }

    public static ResultType getResultType(String value) {
        return ResultTypeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
