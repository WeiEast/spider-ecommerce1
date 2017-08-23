/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.page.impl;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:34:34 AM
 */
public enum RetryMode {
    RETRY("retry"),
    REQUEUE("requeue"),
    PROXY_RETRY("proxy_retry"),
    PROXY_REQUEUE("proxy_requeue");
    private static Map<String, RetryMode> retryModeMap = new HashMap<String, RetryMode>();

    static {
        for (RetryMode obj : values()) {
            retryModeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    RetryMode(String value) {
        this.value = value;
    }

    public static RetryMode getRetryMode(String value) {
        return retryModeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
