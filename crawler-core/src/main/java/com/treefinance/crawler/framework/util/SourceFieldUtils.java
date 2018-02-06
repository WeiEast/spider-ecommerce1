/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.datatrees.common.protocol.util.CharsetUtil;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午4:03:20
 */
public class SourceFieldUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceFieldUtils.class);

    public static String getFieldValueAsString(Object input, String field) throws InterruptedException {
        return getFieldValueAsString(input, field, StringUtils.EMPTY);
    }

    public static String getFieldValueAsString(Object input, String field, String separator) throws InterruptedException {
        Object inputObject = getFieldValue(input, field);

        return formatValue(inputObject, separator);
    }

    public static Object getFieldValue(Object input, String field) {
        if (input instanceof Map) {
            return ((Map) input).get(field);
        }

        try {
            return BeanUtils.getFieldValue(input, field);
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception when getting bean field value. - " + input, e);
        }

        return null;
    }

    private static String formatValue(Object obj, String separator) throws InterruptedException {
        if (obj == null) {
            return StringUtils.EMPTY;
        } else if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof FileWapper) {
            try (FileInputStream inputStream = ((FileWapper) obj).getFileInputStream()) {
                return IOUtils.toString(inputStream, CharsetUtil.DEFAULT);
            } catch (IOException e) {
                LOGGER.error("Error reading file content. - " + obj, e);
            }

            return StringUtils.EMPTY;
        } else if (obj instanceof Collection) {
            String delimiter = StringUtils.defaultString(separator);

            StringBuilder builder = new StringBuilder();
            for (Object sub : (Collection) obj) {
                builder.append(formatValue(sub, delimiter)).append(delimiter);
            }
            return builder.toString();
        } else {
            return obj.toString();
        }
    }
}
