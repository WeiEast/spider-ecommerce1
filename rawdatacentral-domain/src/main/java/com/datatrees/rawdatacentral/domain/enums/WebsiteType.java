package com.datatrees.rawdatacentral.domain.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/8/31
 */
public enum WebsiteType {
    MAIL("mail", "1"),
    OPERATOR("operator", "2"),
    ECOMMERCE("ecommerce", "3"),
    BANK("bank", "4"),
    INTERNAL("internal", "5");
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

}
