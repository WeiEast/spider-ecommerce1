/**
 * www.gf-dai.com.cn
 * Copyright (c) 2015 All Rights Reserved.
 */
package com.datatrees.crawler.core.domain.config.operation.impl.datetime;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author likun
 * @version $Id: DateTimeFieldType.java, v 0.1 Jul 22, 2015 12:44:58 PM likun Exp $
 */
public enum DateTimeFieldType {
    YEAR("year"), MONTH("month"), WEEK_YEAR("weekyear"), WEEK("week"), DATE("date"), HOUR("hour"), MINUTE("minute"), SECOND(
                                                                                                                            "second");

    private final String value;

    DateTimeFieldType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static Map<String, DateTimeFieldType> dateTimeFieldTypeMap = new HashMap<String, DateTimeFieldType>();

    static {
        for (DateTimeFieldType obj : values()) {
            dateTimeFieldTypeMap.put(obj.getValue(), obj);
        }
    }

    public static DateTimeFieldType getDateTimeFieldType(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return dateTimeFieldTypeMap.get(value);
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
