/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:34:34 AM
 */
public enum ProxyStatus {
    SUCCESS("success"),
    FAIL("fail"),
    RELEASE("release");

    private static Map<String, ProxyStatus> proxyStatusMap = new HashMap<String, ProxyStatus>();

    static {
        for (ProxyStatus obj : values()) {
            proxyStatusMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    ProxyStatus(String value) {
        this.value = value;
    }

    public static ProxyStatus getProxyStatus(String value) {
        return proxyStatusMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
