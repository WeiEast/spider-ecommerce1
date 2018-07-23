/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.segment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 4:07:39 PM
 */
public enum SegmentType {
    XPATH("xpath"),
    JSONPATH("jsonpath"),
    REGEX("regex"),
    SPLIT("split"),
    CALCULATE("calculate"),
    BASE("base");

    private static Map<String, SegmentType> segment = new HashMap<String, SegmentType>();

    static {
        for (SegmentType service : values()) {
            segment.put(service.getValue(), service);
        }
    }

    private final String value;

    SegmentType(String value) {
        this.value = value;
    }

    public static SegmentType getSegmentType(String value) {
        return segment.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
