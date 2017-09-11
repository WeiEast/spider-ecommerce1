/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor.util;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import com.datatrees.common.protocol.util.CharsetUtil;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午4:03:20
 */
public class SourceFieldUtil {

    private static final Logger log = LoggerFactory.getLogger(SourceFieldUtil.class);

    public static String getInputFieldString(Object input, String field, String split) {
        split = StringUtils.isEmpty(split) ? "" : split;
        if (input instanceof Map) {
            Object inputObject = ((Map) input).get(field);
            return valueFormat(inputObject, split);
        } else {
            Class userClass = (Class) input.getClass();
            try {
                Field f = userClass.getDeclaredField(field);
                f.setAccessible(true); // set Accessible
                Object inputObject = f.get(input);
                return valueFormat(inputObject, split);
            } catch (Exception e) {
                log.error("get field value error from" + input, e);
            }
        }
        return "";
    }

    public static String getInputFieldString(Object input, String field) {
        return getInputFieldString(input, field, "");
    }

    public static Object getInputFieldObject(Object input, String field) {
        if (input instanceof Map) {
            Object inputObject = ((Map) input).get(field);
            return inputObject;
        } else {
            Class userClass = (Class) input.getClass();
            try {
                Field f = userClass.getDeclaredField(field);
                f.setAccessible(true); // set Accessible
                Object inputObject = f.get(input);
                return inputObject;
            } catch (Exception e) {
                log.error("get field value error from" + input, e);
            }
        }
        return "";
    }

    private static String valueFormat(Object obj, String split) {
        if (obj == null) {
            log.warn("valueFormat with empty input");
            return "";
        } else if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof FileWapper) {
            FileWapper fileWapper = (FileWapper) obj;
            FileInputStream stream = null;
            String content = "";
            try {
                stream = fileWapper.getFileInputStream();
                content = IOUtils.toString(stream, CharsetUtil.DEFAULT);
            } catch (Exception e) {
                log.error("read fileWapper error " + obj, e);
            } finally {
                IOUtils.closeQuietly(stream);
            }
            return content;
        } else if (obj instanceof Collection) {
            StringBuilder builder = new StringBuilder();
            for (Object sub : (Collection) obj) {
                builder.append(valueFormat(sub, split)).append(split);
            }
            return builder.toString();
        } else {
            return obj.toString();
        }
    }

}
