/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.datatrees.crawler.core.processor.bean.FileWapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午4:03:20
 */
public class FieldUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldUtils.class);

    public static String getFieldValueAsString(Object target, String field) throws InterruptedException {
        return getFieldValueAsString(target, field, StringUtils.EMPTY);
    }

    public static String getFieldValueAsString(Object target, String field, String separator) throws InterruptedException {
        Object value = getFieldValue(target, field);

        return formatValue(value, separator);
    }

    public static Object getFieldValue(Object target, String field) {
        if (target instanceof Map) {
            return ((Map) target).get(field);
        }

        try {
            return BeanUtils.getFieldValue(target, field);
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception when getting bean field value. - " + target, e);
        }

        return null;
    }

    private static String formatValue(Object value, String separator) throws InterruptedException {
        if (value == null) {
            return StringUtils.EMPTY;
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof FileWapper) {
            try {
                return ((FileWapper) value).readToString();
            } catch (IOException e) {
                LOGGER.error("Error reading file content. - " + value, e);
            }

            return StringUtils.EMPTY;
        } else if (value instanceof Collection) {
            String delimiter = StringUtils.defaultString(separator);

            StringBuilder builder = new StringBuilder();
            for (Object sub : (Collection) value) {
                builder.append(formatValue(sub, delimiter)).append(delimiter);
            }
            return builder.toString();
        } else {
            return value.toString();
        }
    }
}
