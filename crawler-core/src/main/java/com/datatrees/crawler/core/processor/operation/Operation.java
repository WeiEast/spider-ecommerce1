/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.pipeline.Valve;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.processor.common.Processor;
import com.datatrees.crawler.core.processor.common.exception.OperationException;
import com.google.common.base.Preconditions;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 1:45:54 PM
 */
public abstract class Operation<T extends AbstractOperation> extends Processor {

    protected            T operation = null;
    protected            FieldExtractor    extractor = null;

    public T getOperation() {
        return operation;
    }

    public void setOperation(T operation) {
        this.operation = operation;
    }

    public FieldExtractor getExtractor() {
        return extractor;
    }

    public void setExtractor(FieldExtractor extractor) {
        this.extractor = extractor;
    }

    protected void postProcess(Request request, Response response) throws Exception {
        AbstractOperation nextOperation = null;
        try {
            Preconditions.checkNotNull(response.getOutPut(), operation + " output should not empty!");
            Valve next = getNext();
            if (next != null) {
                nextOperation = next instanceof Operation ? ((Operation) next).getOperation() : null;
                next.invoke(request, response);
            }
        } catch (Exception e) {
            if (e instanceof OperationException) {
                throw e;
            } else {
                String messageString = nextOperation != null ? nextOperation + " operate error " + e.getMessage() : e.getMessage();
                throw new OperationException(messageString, e);
            }
        }

    }

    protected void preProcess(Request request, Response response) throws Exception {
        Preconditions.checkNotNull(operation, "operation should not be empty!");
        Preconditions.checkNotNull(request.getInput(), "input content should not be empty!");
    }

    public abstract void process(Request request, Response response) throws Exception;

}
