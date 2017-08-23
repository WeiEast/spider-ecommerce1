/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import java.util.Map;
import java.util.Set;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorWarpper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月21日 下午7:24:26
 */
public class SourceUtil {

    private static final Logger log = LoggerFactory.getLogger(SourceUtil.class);

    public static Object getSourceMap(String sourceId, Request request, Response response) {
        Object result = null;
        Map<String, FieldExtractorWarpper> fieldMap = ResponseUtil.getResponseFieldResult(response);
        FieldExtractorWarpper fieldWrapper = null;
        if (fieldMap != null && (fieldWrapper = fieldMap.get(sourceId)) != null && fieldWrapper.getResult() != null) {
            result = fieldWrapper.getResult();
        }
        if (result == null) {
            result = RequestUtil.getSourceMap(request).get(sourceId);
        }
        if (log.isDebugEnabled()) {
            log.debug("source from sourceId:" + sourceId + ",result:" + result);
        }
        return result;
    }

    public static String sourceExpression(Request request, String expression) {
        Set<String> replaceList = ReplaceUtils.getReplaceList(expression);
        return ReplaceUtils.replaceMap(replaceList, RequestUtil.getSourceMap(request), expression);
    }

    public static String sourceExpression(Request request, Response response, String expression) {
        Set<String> replaceList = ReplaceUtils.getReplaceList(expression);
        Map<String, FieldExtractorWarpper> fieldMap = ResponseUtil.getResponseFieldResult(response);
        return ReplaceUtils.replaceMap(replaceList, FieldExtractorWarpperUtil.fieldWrapperMapToField(fieldMap), RequestUtil.getSourceMap(request), expression);
    }

    public static String sourceExpression(Map<String, Object> map, String expression) {
        Set<String> replaceList = ReplaceUtils.getReplaceList(expression);
        return ReplaceUtils.replaceMap(replaceList, map, expression);
    }

}
