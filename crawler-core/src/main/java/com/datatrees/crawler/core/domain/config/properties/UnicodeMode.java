/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 5, 2014 7:38:29 PM
 */
public enum UnicodeMode {
    STANDARD("standard"),
    HEX("hex"),
    DECIMAL("decimal"),
    SPECIAL("special"),
    COMPLEX("complex"),
    DEFAULT("default");

    private static Map<String, UnicodeMode> UnicodeModeMap = new HashMap<String, UnicodeMode>();

    static {
        for (UnicodeMode obj : values()) {
            UnicodeModeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    UnicodeMode(String value) {
        this.value = value;
    }

    public static UnicodeMode getUnicodeMode(String value) {
        return UnicodeModeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
