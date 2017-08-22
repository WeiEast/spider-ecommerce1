/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.common;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.protocol.util.HeaderParser;
import com.datatrees.common.protocol.util.UrlUtils;
import com.datatrees.common.util.ReflectionUtils;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.parser.ParserURLCombiner;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月3日 上午12:57:30
 */
public class ResultMapConverter {
    private static final Logger log = LoggerFactory.getLogger(ResultMapConverter.class);


    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void convert(Object input, String baseURL, LinkNode current, List results) {
        if (input instanceof HashMap) {
            Map<String, Object> resultMap = (Map<String, Object>) input;
            String className = null;
            try {
                if (resultMap == null || resultMap.isEmpty()
                        || StringUtils.isBlank((className = (String) resultMap.get(Constants.SEGMENT_RESULT_CLASS_NAMES)))) {
                    results.add(resultMap);
                    return;
                }
                if (className.equals("LinkNode") || className.equals("com.datatrees.crawler.core.processer.bean.LinkNode") && current != null) {// linknode
                    results.addAll((extractLinkNodes(baseURL, resultMap, current)));
                    return;
                } else {
                    if (className.contains(".")) {// package class name,try reflect
                        try {
                            Object instance = ReflectionUtils.newInstance(className);
                            if (instance instanceof Map) {// no need to Convert
                                resultMap.remove(Constants.SEGMENT_RESULT_CLASS_NAMES);
                                ((Map) instance).putAll(resultMap);
                            } else {
                                Class userClass = (Class) instance.getClass();
                                Field[] fs = userClass.getDeclaredFields();
                                for (int i = 0; i < fs.length; i++) {
                                    Field f = fs[i];
                                    f.setAccessible(true); // set Accessible
                                    Object value = resultMap.get(f.getName());
                                    f.set(instance, value);// set value
                                    if (log.isDebugEnabled()) {
                                        log.debug("set name:" + f.getName() + "\t value = " + resultMap.get(f.getName()));
                                    }
                                }
                            }
                            results.add(instance);
                            return;
                        } catch (Exception e) {
                            log.warn("instance reflect error :" + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.error(resultMap + " convert to " + className + " error.", e);
                }
            }
            results.add(resultMap);
        } else {
            results.add(input);
        }

    }

    @SuppressWarnings("unchecked")
    protected static Collection<LinkNode> extractLinkNodes(String baseURL, Map<String, Object> linkNodeMap, LinkNode current) {
        Map<String, LinkNode> linkNodes = new LinkedHashMap<String, LinkNode>();

        Object urlObj = linkNodeMap.remove(Constants.CRAWLER_URL_FIELD);
        if (urlObj != null) {
            if (urlObj instanceof String) {
                log.debug("normal string url " + urlObj);
                String url = (String) urlObj;
                String resolvedUrl = UrlUtils.resolveUrl(baseURL, url);
                LinkNode tmp = new LinkNode(resolvedUrl);
                tmp.setReferer(current.getUrl());
                tmp.setFromParser(true);
                tmp.addPropertys(linkNodeMap);
                if (StringUtils.isNotBlank(resolvedUrl) && linkNodes.put(resolvedUrl, tmp) == null) {
                    log.debug("new url extracted in field: " + resolvedUrl);
                } else {
                    log.debug("url exists in field: " + resolvedUrl);
                }
            }

            if (urlObj instanceof List) {
                List<String> urlsList = (List<String>) urlObj;
                log.debug("segment url size..." + urlsList.size() + baseURL);
                for (String url : urlsList) {
                    String[] pairs = ParserURLCombiner.decodeParserUrl(url);
                    String u = pairs[0];
                    String refer = pairs[1];
                    String headers = pairs[2];
                    String resolvedUrl = UrlUtils.resolveUrl(baseURL, u);
                    LinkNode tmp = new LinkNode(resolvedUrl);
                    tmp.setFromParser(true);
                    tmp.addPropertys(linkNodeMap);
                    if (StringUtils.isNotEmpty(refer)) {
                        tmp.setReferer(refer);
                    } else {
                        tmp.setReferer(current.getUrl());
                    }
                    if (StringUtils.isNotEmpty(headers)) {
                        tmp.addHeaders(HeaderParser.getHeaderMaps(headers));
                    }
                    if (StringUtils.isNotBlank(resolvedUrl) && linkNodes.put(resolvedUrl, tmp) == null) {
                        log.debug("new url extracted in field: " + resolvedUrl);
                    } else {
                        log.debug("field extractor url exists: " + resolvedUrl);
                    }
                }
            }
        } else {
            log.warn("field url is not a list! in field combine " + baseURL);
        }
        return linkNodes.values();
    }
}
