/**
 * www.gf-dai.com.cn Copyright (c) 2015 All Rights Reserved.
 */
package com.datatrees.crawler.core.domain.config.operation.impl.datetime;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author likun
 * @version $Id: BaseType.java, v 0.1 Jul 22, 2015 12:37:13 PM likun Exp $
 */
public enum BaseType {
    NOW("now"), FIRST_DAY_OF_THIS_WEEK("firstdayofthisweek"), LAST_DAY_OF_THIS_WEEK("lastdayofthisweek"), FIRST_DAY_OF_THIS_MONTH(
            "firstdayofthismonth"), LAST_DAY_OF_THIS_MONTH("lastdayofthismonth"), FIRST_DAY_OF_THIS_YEAR("firstdayofthisyear"), LAST_DAY_OF_THIS_YEAR(
            "lastdayofthisyear"), CUSTOM("custom");

    private final String value;

    BaseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static Map<String, BaseType> baseTypeMap = new HashMap<String, BaseType>();

    static {
        for (BaseType obj : values()) {
            baseTypeMap.put(obj.getValue(), obj);
        }
    }

    public static BaseType getBaseType(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return baseTypeMap.get(value);
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
