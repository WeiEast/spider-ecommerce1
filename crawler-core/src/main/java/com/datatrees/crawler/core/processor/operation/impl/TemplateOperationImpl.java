/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;
import java.util.Set;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.TemplateOperation;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorWarpper;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:34 PM
 */
public class TemplateOperationImpl extends Operation<TemplateOperation> {

    @Override
    public void process(Request request, Response response) throws Exception {
        TemplateOperation op = getOperation();
        String template = op.getTemplate();
        Set<String> replaceList = ReplaceUtils.getReplaceList(template);
        logger.debug("replace list: {}", replaceList);
        @SuppressWarnings("unchecked") Map<String, FieldExtractorWarpper> fieldMap = ResponseUtil.getResponseFieldResult(response);
        logger.debug("field stack: {}", fieldMap);
        Object output;
        if (BooleanUtils.isTrue(op.getReturnObject())) {
            output = ReplaceUtils.getReplaceObject(replaceList, FieldExtractorWarpperUtil.fieldWrapperMapToField(fieldMap), RequestUtil.getSourceMap(request), template);
        } else {
            output = ReplaceUtils.replaceMap(replaceList, FieldExtractorWarpperUtil.fieldWrapperMapToField(fieldMap), RequestUtil.getSourceMap(request), template);
        }
        logger.debug("after template combine >> {}", output);
        if (output != null && output.equals(template) && CollectionUtils.isNotEmpty(ReplaceUtils.getReplaceList(template))) {
            logger.warn("template ops failed,set null...");
            output = null;
        }
        response.setOutPut(output);
    }

}
