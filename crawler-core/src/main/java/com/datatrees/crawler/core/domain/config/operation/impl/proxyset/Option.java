package com.datatrees.crawler.core.domain.config.operation.impl.proxyset;

/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:24:48 PM
 */
public enum Option {
    OFF("off"),
    ON("on"),
    NORMAL("normal"),
    AUTO("auto");

    private static Map<String, Option> OptionTypeMap = new HashMap<String, Option>();

    static {
        for (Option obj : values()) {
            OptionTypeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    Option(String value) {
        this.value = value;
    }

    public static Option getOptionType(String value) {
        return OptionTypeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
