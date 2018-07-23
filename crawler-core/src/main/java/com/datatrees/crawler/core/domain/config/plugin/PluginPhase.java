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
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 11:20:38 AM
 */
public enum PluginPhase {
    LOGIN("login"),
    SEARCH("search"),
    SOURCE("source"),
    FIELD("field"),
    COOKIE_REFRESH("cookie_refresh"),
    SERVICE("service");

    private static Map<String, PluginPhase> PluginPhaseMap = new HashMap<String, PluginPhase>();

    static {
        for (PluginPhase obj : values()) {
            PluginPhaseMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    PluginPhase(String value) {
        this.value = value;
    }

    public static PluginPhase getPluginPhase(String value) {
        return PluginPhaseMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
