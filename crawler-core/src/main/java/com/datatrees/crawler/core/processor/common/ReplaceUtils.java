/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.PatternUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 21, 2014 4:08:38 PM
 */
public class ReplaceUtils {

    private static final Logger      log                  = LoggerFactory.getLogger(ReplaceUtils.class);
    private static final String      varPat               = ("\\$\\{[^\\}\\$\u0020]+\\}");
    private static final int         MAX_SUBST            = 20;
    private static       Set<String> replacedEncodeKeySet = new HashSet<String>(Arrays.asList(PropertiesConfiguration.getInstance().get("replaced.encode.keys", "keyword").split(",")));

    /**
     *
     * @param template
     * @return
     */
    public static Set<String> getReplaceList(String template) {
        Preconditions.checkNotNull(template);
        Matcher match = PatternUtils.matcher(varPat, template);
        Set<String> result = new HashSet<String>();
        for (int s = 0; s < MAX_SUBST; s++) {
            if (match.find()) {
                String var = match.group();
                var = var.substring(2, var.length() - 1);
                result.add(var);
            }
        }
        return result;
    }

    public static String replaceMap(Set<String> replaceList, Map<String, Object> fieldMap, String template) {
        return replaceMap(replaceList, fieldMap, null, template);
    }

    // public static String replaceObjectMap(Set<String> replaceList, Map<String, Object> fieldMap,
    // String template) {
    // Preconditions.checkNotNull(replaceList);
    // Preconditions.checkNotNull(template);
    // Preconditions.checkNotNull(fieldMap);
    // String result = template;
    // for (String replace : replaceList) {
    // String from = "${" + replace + "}";
    // Object to = fieldMap.get(replace);
    // if (to != null && !(to instanceof List)) {
    // String toS = String.valueOf(to);
    // result = StringUtils.replace(result, from, toS);
    // }
    // }
    // return result;
    // }

    public static String replaceMap(Map<String, Object> fieldMap, String template) {
        Set<String> replaceList = getReplaceList(template);

        return replaceMap(replaceList, fieldMap, template);
    }

    public static String replaceMapWithCheck(Map<String, Object> fieldMap, String template) {
        Set<String> replaceList = getReplaceList(template);
        String result = replaceMap(replaceList, fieldMap, template);
        if (template != null && result != null && template.equals(result)) {
            return "";
        } else {
            return result;
        }
    }

    public static String replace(String from, String to, String src) {
        return StringUtils.replace(src, from, to);
    }

    private static String getReplacedValue(Map<String, Object> fieldResultMap, String replaceString) {
        String key = StringUtils.substringBefore(replaceString, ".");
        String after = StringUtils.substringAfter(replaceString, ".");
        Object object = fieldResultMap.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof Map) {
            if (after != null && after.contains(".")) {
                if (after.contains("\\.")) {
                    return ((Map) object).get(after.replace("\\.", ".")) != null ? ((Map) object).get(after.replace("\\.", ".")).toString() : null;
                } else {
                    return getReplacedValue((Map) object, after);
                }
            } else {
                return ((Map) object).get(after) != null ? ((Map) object).get(after).toString() : null;
            }
        } else {
            return object.toString();
        }
    }

    public static Object getReplaceObject(Set<String> needReplaced, Map<String, Object> fieldResultMap, Map<String, Object> defaultMap, String source) {
        Preconditions.checkNotNull(needReplaced);
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(fieldResultMap);
        for (String replace : needReplaced) {
            Object to = getReplacedObject(fieldResultMap, replace);
            if (to == null && MapUtils.isNotEmpty(defaultMap)) {
                to = getReplacedObject(defaultMap, replace);
            }
            return to;
        }
        return null;
    }

    private static Object getReplacedObject(Map<String, Object> fieldResultMap, String replaceString) {
        String key = StringUtils.substringBefore(replaceString, ".");
        String after = StringUtils.substringAfter(replaceString, ".");
        Object object = fieldResultMap.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof Map) {
            if (after != null && after.contains(".")) {
                if (after.contains("\\.")) {
                    return ((Map) object).get(after.replace("\\.", "."));
                } else {
                    return getReplacedObject((Map) object, after);
                }
            } else {
                return ((Map) object).get(after);
            }
        } else {
            return object;
        }
    }

    public static String replaceMap(Map<String, Object> fieldResultMap, Map<String, Object> defaultMap, String source) {
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(fieldResultMap);
        Set<String> replaceList = getReplaceList(source);
        return replaceMap(replaceList, fieldResultMap, defaultMap, source);
    }

    /**
     *
     * @param needReplaced
     * @param fieldResultMap
     * @param defaultMap
     * @param source
     * @return
     */
    public static String replaceMap(Set<String> needReplaced, Map<String, Object> fieldResultMap, Map<String, Object> defaultMap, String source) {
        Preconditions.checkNotNull(needReplaced);
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(fieldResultMap);

        String result = source;
        for (String replace : needReplaced) {
            String from = "${" + replace + "}";
            // Object to = fieldResultMap.get(replace);
            String to = getReplacedValue(fieldResultMap, replace);

            if (to == null && MapUtils.isNotEmpty(defaultMap)) {
                to = getReplacedValue(defaultMap, replace);
            }
            if (to != null) {
                result = StringUtils.replace(result, from, to);
            }
        }
        return result;
    }

    public static String replaceURLMap(Set<String> needReplaced, Map<String, Object> fieldResultMap, Map<String, Object> defaultMap, String source, String urlCharset) {
        Preconditions.checkNotNull(needReplaced);
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(fieldResultMap);

        String result = source;
        for (String replace : needReplaced) {
            String from = "${" + replace + "}";
            // Object to = fieldResultMap.get(replace);
            String to = getReplacedValue(fieldResultMap, replace);

            if (to == null && MapUtils.isNotEmpty(defaultMap)) {
                to = getReplacedValue(defaultMap, replace);
            }
            if (to != null) {
                try {
                    if (replacedEncodeKeySet != null && replacedEncodeKeySet.contains(replace)) {
                        log.warn(replace + ":" + to + ", need to be encoded replace...");
                        if (StringUtils.isNotBlank(urlCharset) && Charset.isSupported(urlCharset)) {
                            to = URLEncoder.encode(to, urlCharset);
                        } else {
                            log.warn("Charset unsupported, use UTF-8 as default, url charset: " + urlCharset);
                            to = URLEncoder.encode(to, "UTF-8");
                        }
                    }
                    result = StringUtils.replace(result, from, (String) to);
                } catch (UnsupportedEncodingException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }

}
