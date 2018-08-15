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
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月21日 下午1:23:40
 */
public enum FieldVisibleType {
    /**
     * request scope
     */
    REQUEST("request"),
    /**
     * context scope, like that some fields only was stored in search context or extract context.
     * <p>notice: the context scope contains request scope</p>
     */
    CONTEXT("context"),
    /**
     * only stored in <code>processor_result</code> of task context.
     * @see com.datatrees.crawler.core.processor.SearchProcessorContext#processorResult
     */
    PROCESSOR_RESULT("processor_result");

    private final String value;

    FieldVisibleType(String value) {
        this.value = value;
    }

    public static FieldVisibleType getFieldVisibleType(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        return Arrays.stream(values()).filter(e -> e.getValue().equalsIgnoreCase(value)).findFirst().orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
