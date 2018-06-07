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
import com.datatrees.crawler.core.domain.config.operation.impl.TemplateOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:34 PM
 */
public class TemplateOperationImpl extends Operation<TemplateOperation> {

    public TemplateOperationImpl(@Nonnull TemplateOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        TemplateOperation op = getOperation();
        String template = op.getTemplate();

        Object output;
        if (BooleanUtils.isTrue(op.getReturnObject())) {
            output = StandardExpression.evalWithObject(StringUtils.trim(template), request, response);
        } else {
            output = StandardExpression.eval(template, request, response);
        }

        response.setOutPut(output);
    }

}
