/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.ReturnMatchOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.lang.StringUtils;

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
    protected Object doOperation(@Nonnull ReturnMatchOperation operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception {
        String input = (String) operatingData;

        String value = StandardExpression.eval(operation.getValue(), request, response);

        logger.debug("return match keys: {}", value);

        if (StringUtils.isBlank(value)) {
            return null;
        }

        String[] matchedKeys = value.split(",");

        String result = Arrays.stream(matchedKeys).map(String::trim).filter(key -> !key.isEmpty() && input.contains(key)).collect(Collectors.joining(","));

        return result.isEmpty() ? null : result;
    }

}
