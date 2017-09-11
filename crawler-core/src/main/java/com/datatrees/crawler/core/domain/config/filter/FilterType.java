/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.filter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:50:40 AM
 */
public enum FilterType {
    WHITE_LIST("+"),
    BLACK_LIST("-");
    private static Map<String, FilterType> FilterTypeMap = new HashMap<String, FilterType>();

    static {
        for (FilterType service : values()) {
            FilterTypeMap.put(service.getValue(), service);
        }
    }

    private final String value;

    FilterType(String value) {
        this.value = value;
    }

    public static FilterType getFilterType(String value) {
        return FilterTypeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
