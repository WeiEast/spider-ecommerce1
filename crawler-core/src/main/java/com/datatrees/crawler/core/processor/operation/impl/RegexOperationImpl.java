/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.RegexOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.treefinance.crawler.framework.expression.StandardExpression;
import com.treefinance.toolkit.util.RegExp;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:57:53 PM
 */
public class RegexOperationImpl extends Operation<RegexOperation> {

    public RegexOperationImpl(@Nonnull RegexOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected Object doOperation(@Nonnull RegexOperation operation, @Nonnull Object operatingData, @Nonnull Request request,
            @Nonnull Response response) throws Exception {
        String regex = operation.getRegex();
        String orginal = (String) operatingData;

        regex = StandardExpression.eval(regex, request, response);

        Object result = null;
        if (operation.getGroupIndex() == null || operation.getGroupIndex() < 0) {
            result = RegExp.getMatcher(regex, orginal);
            if (!((Matcher) result).find()) {
                result = null;
            }
        } else {
            int index = operation.getGroupIndex();
            logger.debug("regex: {}, index: {}", regex, index);

            result = RegExp.group(orginal, regex, index, null);

            logger.debug("original: {}, dest: {}", orginal, result);
        }
        return result;
    }
}
