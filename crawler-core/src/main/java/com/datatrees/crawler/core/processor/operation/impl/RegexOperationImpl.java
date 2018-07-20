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
import com.treefinance.crawler.framework.exception.InvalidOperationException;
import com.treefinance.crawler.framework.expression.StandardExpression;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:57:53 PM
 */
public class RegexOperationImpl extends Operation<RegexOperation> {

    public RegexOperationImpl(@Nonnull RegexOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected void validate(RegexOperation operation, Request request, Response response) throws Exception {
        super.validate(operation, request, response);

        if (StringUtils.isEmpty(operation.getRegex())) {
            throw new InvalidOperationException("Invalid regex operation! - 'regex/text()' must not be empty.");
        }
    }

    @Override
    protected Object doOperation(@Nonnull RegexOperation operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception {
        String regex = StandardExpression.eval(operation.getRegex(), request, response);

        logger.debug("Actual regexp: {}", regex);

        String input = (String) operatingData;

        Integer groupIndex = operation.getGroupIndex();
        if (groupIndex == null || groupIndex < 0) {
            Matcher result = RegExp.getMatcher(regex, input);
            if (result.find()) {
                return result;
            }

            return null;
        }

        logger.debug("regex: {}, index: {}", regex, groupIndex);

        return RegExp.group(input, regex, groupIndex, null);
    }
}
