/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.ParserOperation;
import com.datatrees.crawler.core.domain.config.parser.Parser;
import com.datatrees.crawler.core.processor.filter.FieldRequestFilter;
import com.datatrees.crawler.core.processor.filter.ParserUrlListFilter;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.parser.ParserImpl;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.exception.InvalidOperationException;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:43:36 PM
 */
public class ParserOperationImpl extends Operation<ParserOperation> {

    private static final FieldRequestFilter  fieldFilter         = new FieldRequestFilter();
    private static final ParserUrlListFilter parserUrlListFilter = new ParserUrlListFilter();

    public ParserOperationImpl(@Nonnull ParserOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected void validate(@Nonnull ParserOperation operation, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        super.validate(operation, request, response);

        if (operation.getParser() == null) {
            throw new InvalidOperationException("Invalid parser operation! - Reference 'parser' must not be null.");
        }
    }

    @Override
    protected Object doOperation(@Nonnull ParserOperation operation, @Nonnull Object operatingData, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        Parser parser = operation.getParser();

        FieldExtractor field = getExtractor();
        logger.debug("field name: {}", field);

        boolean needRequest = false;
        String fieldResult = fieldFilter.filter(field.getField());
        if (StringUtils.isNotEmpty(fieldResult)) {
            needRequest = true;
        }

        boolean needReturnUrlList = false;
        String urlList = parserUrlListFilter.filter(field.getField());
        if (StringUtils.isNotEmpty(urlList)) {
            needReturnUrlList = true;
        }

        logger.debug("invoke parser process: {}", field);
        try {
            ParserImpl parserImpl = new ParserImpl(parser, needRequest, needReturnUrlList);
            return parserImpl.parse((String) operatingData, request, response);
        } finally {
            logger.debug("success invoke parser process: {}", field);
        }
    }

}
