/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.ReplaceOperation;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:19 PM
 */
public class ReplaceOperationImpl extends Operation<ReplaceOperation> {


    @Override
    public void process(Request request, Response response) throws Exception {
        ReplaceOperation op = getOperation();
        String from = op.getFrom();
        String to = op.getTo();
        Map<String, Object> sourceMap = RequestUtil.getSourceMap(request);

        // result context
        Map<String, Object> fieldContext = FieldExtractorWarpperUtil.fieldWrapperMapToField(ResponseUtil.getResponseFieldResult(response));

        if (StringUtils.isNotBlank(from)) {
            from = ReplaceUtils.replaceMap(ReplaceUtils.getReplaceList(from), fieldContext, sourceMap, from);
        }
        if (StringUtils.isNotBlank(to)) {
            to = ReplaceUtils.replaceMap(ReplaceUtils.getReplaceList(to), fieldContext, sourceMap, to);
        }

        String orginal = OperationHelper.getStringInput(request, response);

        String dest = orginal.replaceAll(from, to); // change replace to regex

        if (logger.isDebugEnabled()) {
            logger.debug("replace result:" + dest);
        }
        response.setOutPut(dest);
        // finally invoke next valve
    }
}
