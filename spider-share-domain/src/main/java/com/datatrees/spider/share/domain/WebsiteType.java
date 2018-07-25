/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月7日 上午11:32:56
 */
public enum WebsiteType {
    MAIL("mail", "1"),
    OPERATOR("operator", "2"),
    ECOMMERCE("ecommerce", "3"),
    BANK("bank", "4"),
    INTERNAL("internal", "5"),
    EDUCATION("education", "6");

    private static Map<String, WebsiteType> WebsiteTypeMap = new HashMap<String, WebsiteType>();

    static {
        for (WebsiteType obj : values()) {
            WebsiteTypeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    private final String type;

    WebsiteType(String type, String value) {
        this.value = value;
        this.type = type;
    }

    public static WebsiteType getWebsiteType(String value) {
        return WebsiteTypeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public byte val() {
        return Byte.parseByte(value);
    }
}
