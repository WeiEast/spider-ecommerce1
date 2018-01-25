/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.ReturnMatchOperation;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;

/**
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2016年5月30日 下午8:33:11
 */
public class ReturnMatchOperationImpl extends Operation<ReturnMatchOperation> {

    @Override
    public void process(Request request, Response response) throws Exception {
        ReturnMatchOperation operation = getOperation();
        String value = operation.getValue();
        String orginal = OperationHelper.getStringInput(request, response);

        Map<String, Object> fieldContext = FieldExtractorWarpperUtil.fieldWrapperMapToField(ResponseUtil.getResponseFieldResult(response));
        Map<String, Object> sourceMap = RequestUtil.getSourceMap(request);

        value = ReplaceUtils.replaceMap(fieldContext, sourceMap, value);
        StringBuilder result = new StringBuilder();
        String[] matchedKeys = value.split(",");
        for (String matchedKey : matchedKeys) {
            if (orginal.contains(matchedKey)) {
                result.append(matchedKey + ",");
            }
        }
        if (result.length() >= 1) {
            if (result.charAt(result.length() - 1) == ',') {
                result = new StringBuilder(result.substring(0, result.length() - 1));
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("ReturnMatchOperation input: " + String.format("value: %s", value));
        }
        response.setOutPut(result.toString());
    }

}
