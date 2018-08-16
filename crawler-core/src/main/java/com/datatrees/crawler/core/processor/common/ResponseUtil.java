/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.datatrees.common.protocol.ProtocolOutput;
import com.datatrees.crawler.core.domain.config.page.AbstractPage;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.process.fields.FieldExtractResultSet;

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

    public static void setPageExtractor(SpiderResponse response, AbstractPage pageExtractor) {
        response.setAttribute(Constants.PAGE_EXTRACT, pageExtractor);
    }

    public static AbstractPage getPageExtractor(SpiderResponse response) {
        return (AbstractPage) response.getAttribute(Constants.PAGE_EXTRACT);
    }

    public static FieldExtractResultSet prepareFieldExtractResultSet(SpiderResponse response) {
        return response.computeAttributeIfAbsent(Constants.FIELDS_RESULT_MAP, key -> new FieldExtractResultSet(), FieldExtractResultSet.class);
    }

    public static void setFieldExtractResultSet(SpiderResponse response, FieldExtractResultSet resultMap) {
        response.setAttribute(Constants.FIELDS_RESULT_MAP, resultMap);
    }

    public static FieldExtractResultSet getFieldExtractResultSet(SpiderResponse response) {
        return response.getAttribute(Constants.FIELDS_RESULT_MAP, FieldExtractResultSet.class);
    }

    public static FieldExtractResultSet removeFieldExtractResultSet(SpiderResponse response) {
        return (FieldExtractResultSet) response.removeAttribute(Constants.FIELDS_RESULT_MAP);
    }

    public static Map<String, Object> getFieldExtractResultMap(SpiderResponse response) {
        FieldExtractResultSet fieldExtractResultSet = getFieldExtractResultSet(response);

        return fieldExtractResultSet == null ? Collections.emptyMap() : fieldExtractResultSet.resultMap();
    }

    public static List<LinkNode> getResponseLinkNodes(SpiderResponse response) {
        return (List<LinkNode>) response.getAttribute(Constants.RESPONSE_LINKNODES);
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

}
