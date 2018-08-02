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
import com.datatrees.crawler.core.domain.config.operation.impl.ProxySetOperation;
import com.datatrees.crawler.core.processor.operation.Operation;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:34 PM
 */
public class ProxySetOperationImpl extends Operation<ProxySetOperation> {

    public ProxySetOperationImpl(@Nonnull ProxySetOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected boolean isSkipped(@Nonnull Request request, @Nonnull Response response) {
        logger.warn("Unsupported proxy-setting operation and skip!");
        return true;
    }

    @Override
    protected Object doOperation(@Nonnull ProxySetOperation operation, @Nonnull Object operatingData, @Nonnull Request request,
            @Nonnull Response response) throws Exception {
        throw new UnsupportedOperationException("Unsupported proxy-set operation!");
    }

}
