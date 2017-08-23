/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.ParserOperation;
import com.datatrees.crawler.core.domain.config.parser.Parser;
import com.datatrees.crawler.core.processor.filter.FieldRequestFilter;
import com.datatrees.crawler.core.processor.filter.ParserUrlListFilter;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.parser.ParserImpl;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:43:36 PM
 */
public class ParserOperationImpl extends Operation {

    private static final Logger              log                 = LoggerFactory.getLogger(ParserOperationImpl.class);
    private static final FieldRequestFilter  fieldFilter         = new FieldRequestFilter();
    private static final ParserUrlListFilter parserUrlListFilter = new ParserUrlListFilter();

    /*
     * (non-Javadoc)
     * 
     */
    @Override
    public void process(Request request, Response response) throws Exception {

        ParserOperation op = (ParserOperation) getOperation();
        Parser parser = op.getParser();
        boolean needRequest = false;
        boolean needReturnUrlList = false;
        Preconditions.checkNotNull(parser, "ParserOperation parser element should not be null!");
        FieldExtractor field = getExtractor();
        log.debug("field name:\t" + field);
        String fieldResult = fieldFilter.filter(field.getField());
        String urlList = parserUrlListFilter.filter(field.getField());
        if (StringUtils.isNotEmpty(fieldResult)) {
            needRequest = true;
        }
        if (StringUtils.isNotEmpty(urlList)) {
            needReturnUrlList = true;
        }
        log.debug("invoke parser process :\t" + field);
        ParserImpl parserImpl = new ParserImpl(needRequest, parser, needReturnUrlList);
        parserImpl.invoke(request, response);
        log.debug("success invoke parser process :\t" + field);
    }

}
