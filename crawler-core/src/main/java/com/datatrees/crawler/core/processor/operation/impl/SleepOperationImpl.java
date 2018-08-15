/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.SleepOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:34 PM
 */
public class SleepOperationImpl extends Operation<SleepOperation> {

    public SleepOperationImpl(@Nonnull SleepOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor, false);
    }

    @Override
    protected boolean isSkipped(@Nonnull SleepOperation operation, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) {
        // invalid sleep operation and skip
        boolean flag = operation.getValue() == null;
        if (flag) {
            logger.warn("invalid sleep operation and skip");
        }
        return flag;
    }

    @Override
    protected Object doOperation(@Nonnull SleepOperation operation, @Nonnull Object operatingData, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        Integer sleepTime = operation.getValue();
        logger.debug("Start to Sleep: {}", sleepTime);
        Thread.sleep(sleepTime);

        return null;
    }

}
