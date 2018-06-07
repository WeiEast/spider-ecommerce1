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
import com.datatrees.crawler.core.domain.config.operation.impl.ReturnOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:34 PM
 */
public class ReturnOperationImpl extends Operation<ReturnOperation> {

    public ReturnOperationImpl(@Nonnull ReturnOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        String input = OperationHelper.getStringInput(request, response);

        logger.debug("Return current input : {}", input);
        response.setOutPut(input);
    }

}
