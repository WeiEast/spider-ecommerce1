/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation;

import javax.annotation.Nonnull;
import java.util.Objects;

import com.datatrees.common.pipeline.ProcessingException;
import com.datatrees.common.pipeline.ProcessorValve;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.treefinance.toolkit.util.json.GsonUtils;
import com.treefinance.toolkit.util.json.Jackson;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 1:45:54 PM
 */
public abstract class Operation<T extends AbstractOperation> extends ProcessorValve {

    protected final T              operation;
    protected final FieldExtractor extractor;
    private boolean needReturn = true;

    public Operation(@Nonnull T operation, @Nonnull FieldExtractor extractor) {
        this(operation, extractor, true);
    }

    public Operation(T operation, FieldExtractor extractor, boolean needReturn) {
        this.operation = Objects.requireNonNull(operation);
        this.extractor = Objects.requireNonNull(extractor);
        this.needReturn = needReturn;
    }

    public T getOperation() {
        return operation;
    }

    public FieldExtractor getExtractor() {
        return extractor;
    }

    @Override
    protected boolean isSkipped(@Nonnull Request request, @Nonnull Response response) {
        boolean skipped = getOperatingEntity(request, response).isEmpty();

        // invalid operation and skip
        return skipped || isSkipped(operation, request, response);
    }

    protected boolean isSkipped(T operation, Request request, Response response) {
        return false;
    }

    @Override
    protected void triggerAfterSkipped(@Nonnull Request request, @Nonnull Response response) {
        logger.warn("Skipped operation processor : {}", GsonUtils.toJson(operation));

        OperationEntity entity = getOperatingEntity(request, response);
        entity.skip(operation);
    }

    @Override
    protected final void preProcess(@Nonnull Request request, @Nonnull Response response) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting operation processing >> {}", Jackson.toJSONString(operation));
        }

        validate(operation, request, response);
    }

    protected void validate(T operation, Request request, Response response) throws Exception {

    }

    @Override
    public final void process(@Nonnull Request request, @Nonnull Response response) throws Exception {
        OperationEntity entity = getOperatingEntity(request, response);

        Object data = entity.getData();
        logger.debug("Operating data: {}", data);

        try {
            Object result = doOperation(operation, data, request, response);

            if (needReturn) {
                logger.debug("Operating result: {}", result);
                entity.update(result, operation);
            } else {
                entity.update(operation);
            }
        } catch (Exception e) {
            throw new ProcessingException("Error doing operation[ " + Jackson.toJSONString(operation) + "]", e);
        }
    }

    protected abstract Object doOperation(@Nonnull T operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception;

    @Override
    protected final void postProcess(@Nonnull Request request, @Nonnull Response response) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Completed operation processing >> {}", Jackson.toJSONString(operation));
        }
    }

    @Override
    protected final boolean isEnd(@Nonnull Request request, @Nonnull Response response) {
        return getOperatingEntity(request, response).isEmpty();
    }

    protected final OperationEntity getOperatingEntity(Request request, Response response) {
        OperationEntity entity = (OperationEntity) response.getOutPut();
        if (entity == null) {
            String content = (String) request.getInput();
            entity = OperationEntity.wrap(content);
            response.setOutPut(entity);
        }
        return entity;
    }

}
