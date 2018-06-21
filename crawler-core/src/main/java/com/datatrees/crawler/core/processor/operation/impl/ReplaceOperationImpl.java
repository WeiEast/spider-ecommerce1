/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.ReplaceOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.treefinance.crawler.framework.expression.ExpressionEngine;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:19 PM
 */
public class ReplaceOperationImpl extends Operation<ReplaceOperation> {

    public ReplaceOperationImpl(@Nonnull ReplaceOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected boolean isSkipped(ReplaceOperation operation, Request request, Response response) {
        logger.warn("empty 'from' value in replace operation and skip.");
        return StringUtils.isEmpty(operation.getFrom());
    }

    @Override
    protected Object doOperation(@Nonnull ReplaceOperation operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception {
        ExpressionEngine expressionEngine = null;

        String from = operation.getFrom();
        if (StringUtils.isNotBlank(from)) {
            expressionEngine = new ExpressionEngine(request, response);
            from = expressionEngine.eval(from);
        }

        logger.debug("Actual replace from: {}", from);

        String to = StringUtils.defaultString(operation.getTo());
        if (StringUtils.isNotBlank(to)) {
            if (expressionEngine == null) {
                expressionEngine = new ExpressionEngine(request, response);
            }
            to = expressionEngine.eval(to);
        }

        logger.debug("Actual replace to: {}", to);

        String input = (String) operatingData;

        return input.replaceAll(from, to);
    }
}
