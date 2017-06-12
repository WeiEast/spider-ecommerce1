/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.chain.common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年8月7日 上午11:32:56 
 */
public enum WebsiteType {
    MAIL("1"), OPERATOR("2"), ECOMMERCE("3"), BANK("4"),INTERNAL("5");
    private final String value;

    WebsiteType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static Map<String, WebsiteType> WebsiteTypeMap = new HashMap<String, WebsiteType>();

    static {
        for (WebsiteType obj : values()) {
            WebsiteTypeMap.put(obj.getValue(), obj);
        }
    }

    public static WebsiteType getWebsiteType(String value) {
        return WebsiteTypeMap.get(value);
    }

}
