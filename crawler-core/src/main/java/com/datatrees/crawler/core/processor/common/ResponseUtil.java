/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import java.util.*;

import com.datatrees.common.protocol.ProtocolOutput;
import com.datatrees.crawler.core.domain.config.page.AbstractPage;
import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.extractor.FieldExtractResultSet;
import com.treefinance.crawler.framework.context.function.SpiderResponse;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 6, 2014 4:45:30 PM
 */
public class ResponseUtil {

    private ResponseUtil() {}

    public static List<Object> getResponseObjectList(SpiderResponse response) {
        return (List<Object>) response.getAttribute(Constants.PAGE_EXTRACT_OBJECT_LIST);
    }

    public static void setResponseObjectList(SpiderResponse response, List<Object> objects) {
        response.setAttribute(Constants.PAGE_EXTRACT_OBJECT_LIST, objects);
    }

    public static void setResponsePageExtractResultMap(SpiderResponse response, Map results) {
        response.setAttribute(Constants.PAGE_EXTRACT_RESULT_MAP, results);
    }

    public static Map getResponsePageExtractResultMap(SpiderResponse response) {
        return (Map) response.getAttribute(Constants.PAGE_EXTRACT_RESULT_MAP);
    }

    public static void setPageExtractor(SpiderResponse response, AbstractPage pageExtractor) {
        response.setAttribute(Constants.PAGE_EXTRACT, pageExtractor);
    }

    public static AbstractPage getPageExtractor(SpiderResponse response) {
        return (AbstractPage) response.getAttribute(Constants.PAGE_EXTRACT);
    }

    public static String getResponseContent(SpiderResponse response) {
        return (String) response.getAttribute(Constants.RESPONSE_CONTENT);
    }

    public static void setResponseContent(SpiderResponse response, String content) {
        response.setOutPut(content);
    }

    public static void setFieldExtractResultSet(SpiderResponse response, FieldExtractResultSet resultMap) {
        response.setAttribute(Constants.FIELDS_RESULT_MAP, resultMap);
    }

    public static FieldExtractResultSet getFieldExtractResultSet(SpiderResponse response) {
        return response.getAttribute(Constants.FIELDS_RESULT_MAP, FieldExtractResultSet.class);
    }

    public static Map<String, Object> getFieldExtractResultMap(SpiderResponse response) {
        FieldExtractResultSet fieldExtractResultSet = getFieldExtractResultSet(response);

        return fieldExtractResultSet == null ? Collections.emptyMap() : fieldExtractResultSet.resultMap();
    }

    public static String getResponseErrorMsg(SpiderResponse response) {
        return (String) response.getAttribute(Constants.RESPONSE_ERROR_MSG);
    }

    public static Integer getResponseStatus(SpiderResponse response) {
        return (Integer) response.getAttribute(Constants.RESPONSE_STATUS);
    }

    public static List<LinkNode> getResponseLinkNodes(SpiderResponse response) {
        return (List<LinkNode>) response.getAttribute(Constants.RESPONSE_LINKNODES);
    }

    public static void setResponseErrorMsg(SpiderResponse response, String msg) {
        response.setAttribute(Constants.RESPONSE_ERROR_MSG, msg);
    }

    public static void setResponseStatus(SpiderResponse response, int status) {
        response.setAttribute(Constants.RESPONSE_STATUS, status);
    }

    public static void setResponseLinkNodes(SpiderResponse response, List<LinkNode> nodes) {
        response.setAttribute(Constants.RESPONSE_LINKNODES, nodes);
    }

    public static void setProtocolResponse(SpiderResponse response, ProtocolOutput out) {
        response.setAttribute(Constants.RESPONSE_Protocol_OUTPUT, out);
    }

    public static ProtocolOutput getProtocolResponse(SpiderResponse response) {
        return (ProtocolOutput) response.getAttribute(Constants.RESPONSE_Protocol_OUTPUT);
    }

    public static Map<String, LinkNode> getSegmentsResultMap(SpiderResponse response) {
        return (Map<String, LinkNode>) response.getAttribute(Constants.SEGMENTS_RESULT_MAP);
    }

    public static void setSegmentsResultMap(SpiderResponse response, Map<String, LinkNode> map) {
        response.setAttribute(Constants.SEGMENTS_RESULT_MAP, map);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> prepareSegmentsResults(SpiderResponse response) {
        return (List<Object>) response.computeAttributeIfAbsent(Constants.SEGMENTS_RESULTS, k -> new ArrayList<>());
    }

    public static Object getSegmentsResults(SpiderResponse response) {
        return response.getAttribute(Constants.SEGMENTS_RESULTS);
    }

    public static void setSegmentsResults(SpiderResponse response, Object object) {
        response.setAttribute(Constants.SEGMENTS_RESULTS, object);
    }

    public static List<String> getSegmentsContent(SpiderResponse response) {
        return (List<String>) response.getAttribute(Constants.SEGMENTS_CONTENT);
    }

    public static void setSegmentsContent(SpiderResponse response, List<String> list) {
        response.setAttribute(Constants.SEGMENTS_CONTENT, list);
    }

    public static List<PageExtractor> getMatchedPageExtractorList(SpiderResponse response) {
        return (List<PageExtractor>) response.getAttribute(Constants.MATCHED_PAGE_EXTRACTORS);
    }

    public static Set<String> getBlackPageExtractorIdSet(SpiderResponse response) {
        return (Set<String>) response.getAttribute(Constants.BLACK_PAGE_EXTRACTOR_IDS);
    }

    public static void setMatchedPageExtractorList(SpiderResponse response, List<PageExtractor> list) {
        response.setAttribute(Constants.MATCHED_PAGE_EXTRACTORS, list);
    }

    public static void setBlackPageExtractorIdSet(SpiderResponse response, Set<String> set) {
        response.setAttribute(Constants.BLACK_PAGE_EXTRACTOR_IDS, set);
    }

}
