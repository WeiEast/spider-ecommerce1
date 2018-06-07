/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.ReturnMatchOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
import com.treefinance.crawler.framework.expression.StandardExpression;

/**
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2016年5月30日 下午8:33:11
 */
public class ReturnMatchOperationImpl extends Operation<ReturnMatchOperation> {

    public ReturnMatchOperationImpl(@Nonnull ReturnMatchOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        ReturnMatchOperation operation = getOperation();
        String value = operation.getValue();
        String orginal = OperationHelper.getStringInput(request, response);

        value = StandardExpression.eval(value, request, response);

        logger.debug("input: {}", value);

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

        response.setOutPut(result.toString());
    }

}
