/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 11:17:43 AM
 */
public enum PluginType {
    JAVA("jar"),
    SHELL("sh"),
    PYTHON("py");
    private static Map<String, PluginType> PluginTypeMap = new HashMap<String, PluginType>();

    static {
        for (PluginType obj : values()) {
            PluginTypeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    PluginType(String value) {
        this.value = value;
    }

    public static PluginType getPluginType(String value) {
        return PluginTypeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
