/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.ProtocolOutput;
import com.datatrees.crawler.core.domain.config.page.AbstractPage;
import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorWarpper;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 6, 2014 4:45:30 PM
 */
public class ResponseUtil {

    private ResponseUtil() {}

    ;

    /**
     * parser result list
     * @param response
     * @return
     */
    public static List<Object> getResponseObjectList(Response response) {
        return (List<Object>) response.getAttribute(Constants.PAGE_EXTRACT_OBJECT_LIST);
    }

    public static void setResponseObjectList(Response response, List<Object> objects) {
        response.setAttribute(Constants.PAGE_EXTRACT_OBJECT_LIST, objects);
    }

    public static void setResponsePageExtractResultMap(Response response, Map results) {
        response.setAttribute(Constants.PAGE_EXTRACT_RESULT_MAP, results);
    }

    public static Map getResponsePageExtractResultMap(Response response) {
        return (Map) response.getAttribute(Constants.PAGE_EXTRACT_RESULT_MAP);
    }

    public static void setPageExtractor(Response response, AbstractPage pageExtractor) {
        response.setAttribute(Constants.PAGE_EXTRACT, pageExtractor);
    }

    public static AbstractPage getPageExtractor(Response response) {
        return (AbstractPage) response.getAttribute(Constants.PAGE_EXTRACT);
    }

    // public static List<String> getResponseList(Response response) {
    // return (List<String>) response.getAttribute(Constants.RESPONSE_LIST);
    // }
    //

    public static String getResponseContent(Response response) {
        return (String) response.getAttribute(Constants.RESPONSE_CONTENT);
    }

    // public static void setResponseList(Response response, List<String> contentList) {
    // response.setAttribute(Constants.RESPONSE_LIST, contentList);
    // }

    public static void setResponseContent(Response response, String content) {
        response.setAttribute(Constants.RESPONSE_CONTENT, content);
    }

    public static void setResponseFieldResult(Response response, Map<String, FieldExtractorWarpper> resultMap) {
        response.setAttribute(Constants.FIELDS_RESULT_MAP, resultMap);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, FieldExtractorWarpper> getResponseFieldResult(Response response) {
        return (Map<String, FieldExtractorWarpper>) response.getAttribute(Constants.FIELDS_RESULT_MAP);
    }

    public static String getResponseErrorMsg(Response response) {
        return (String) response.getAttribute(Constants.RESPONSE_ERROR_MSG);
    }

    public static Integer getResponseStatus(Response response) {
        return (Integer) response.getAttribute(Constants.RESPONSE_STATUS);
    }

    public static List<LinkNode> getResponseLinkNodes(Response response) {
        return (List<LinkNode>) response.getAttribute(Constants.RESPONSE_LINKNODES);
    }

    public static void setResponseErrorMsg(Response response, String msg) {
        response.setAttribute(Constants.RESPONSE_ERROR_MSG, msg);
    }

    public static void setResponseStatus(Response response, int status) {
        response.setAttribute(Constants.RESPONSE_STATUS, status);
    }

    public static void setResponseLinkNodes(Response response, List<LinkNode> nodes) {
        response.setAttribute(Constants.RESPONSE_LINKNODES, nodes);
    }

    public static void setProtocolResponse(Response response, ProtocolOutput out) {
        response.setAttribute(Constants.RESPONSE_Protocol_OUTPUT, out);
    }

    public static ProtocolOutput getProtocolResponse(Response response) {
        return (ProtocolOutput) response.getAttribute(Constants.RESPONSE_Protocol_OUTPUT);
    }

    public static Map<String, LinkNode> getSegmentsResultMap(Response response) {
        return (Map<String, LinkNode>) response.getAttribute(Constants.SEGMENTS_RESULT_MAP);
    }

    public static void setSegmentsResultMap(Response response, Map<String, LinkNode> map) {
        response.setAttribute(Constants.SEGMENTS_RESULT_MAP, map);
    }

    public static Object getSegmentsResults(Response response) {
        return response.getAttribute(Constants.SEGMENTS_RESULTS);
    }

    public static void setSegmentsResults(Response response, Object object) {
        response.setAttribute(Constants.SEGMENTS_RESULTS, object);
    }

    public static List<String> getSegmentsContent(Response response) {
        return (List<String>) response.getAttribute(Constants.SEGMENTS_CONTENT);
    }

    public static void setSegmentsContent(Response response, List<String> list) {
        response.setAttribute(Constants.SEGMENTS_CONTENT, list);
    }

    public static List<PageExtractor> getMatchedPageExtractorList(Response response) {
        return (List<PageExtractor>) response.getAttribute(Constants.MATCHED_PAGE_EXTRACTORS);
    }

    public static Set<String> getBlackPageExtractorIdSet(Response response) {
        return (Set<String>) response.getAttribute(Constants.BLACK_PAGE_EXTRACTOR_IDS);
    }

    public static void setMatchedPageExtractorList(Response response, List<PageExtractor> list) {
        response.setAttribute(Constants.MATCHED_PAGE_EXTRACTORS, list);
    }

    public static void setBlackPageExtractorIdSet(Response response, Set<String> set) {
        response.setAttribute(Constants.BLACK_PAGE_EXTRACTOR_IDS, set);
    }

}
