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
import java.util.stream.Collectors;

import com.treefinance.crawler.exception.UncheckedInterruptedException;
import com.treefinance.crawler.framework.download.WrappedFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午4:03:20
 */
public final class FieldUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldUtils.class);

    private static final String DEFAULT_SEPARATOR = "  \r\n";

    private FieldUtils() {
    }

    public static boolean isNullOrEmptyString(Object object) {
        return object == null || (object instanceof String && ((String) object).isEmpty());
    }

    public static boolean isNullOrEmptyCollection(Object object) {
        return object == null || (object instanceof Collection && ((Collection) object).isEmpty());
    }

    public static String getFieldValueAsString(Object target, String field) {
        return getFormattedFieldValue(target, field, DEFAULT_SEPARATOR);
    }

    public static String getFieldValueAsString(Object target, String field, String separator) {
        return getFormattedFieldValue(target, field, separator == null ? DEFAULT_SEPARATOR : separator);
    }

    public static String getFormattedFieldValue(Object target, String field, String separator) {
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

    @SuppressWarnings("unchecked")
    private static String formatValue(Object value, String separator) {
        if (value == null) {
            return StringUtils.EMPTY;
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof WrappedFile) {
            try {
                return ((WrappedFile) value).readToString();
            } catch (InterruptedException e) {
                throw new UncheckedInterruptedException("unexpected interrupted exception!", e);
            } catch (IOException e) {
                LOGGER.error("Error reading file content. - " + value, e);
            }

            return StringUtils.EMPTY;
        } else if (value instanceof Collection) {
            String delimiter = StringUtils.defaultString(separator);

            return ((Collection<Object>) value).stream().map(sub -> formatValue(sub, delimiter)).collect(Collectors.joining(delimiter));
        } else {
            return value.toString();
        }
    }
}
