/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.extractor;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 4:29:24 PM
 */
public enum ResultType {
    String("string"),
    NUMBER("number"),
    DATE("date"),
    PAYMENT("payment"),
    RESOURCE_STRING("resource_string"),
    FILE("file"),
    CURRENCY("currency"),
    CURRENCY_PAYMENT("currency_payment"),
    RMB("rmb"),
    BOOLEAN("boolean"),
    INT("int"),
    LONG("long");

    private final String value;

    ResultType(String value) {
        this.value = value;
    }

    public static ResultType getResultType(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        return Arrays.stream(values()).filter(item -> item.getValue().equalsIgnoreCase(value)).findFirst().orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
