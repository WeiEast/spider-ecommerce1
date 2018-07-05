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
import com.datatrees.crawler.core.domain.config.operation.impl.MatchGroupOperation;
import com.datatrees.crawler.core.processor.common.SourceUtil;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:34 PM
 */
public class MatchGroupOperationImpl extends Operation<MatchGroupOperation> {

    public MatchGroupOperationImpl(@Nonnull MatchGroupOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected Object doOperation(@Nonnull MatchGroupOperation operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception {
        String sourceId = operation.getSourceId();
        Matcher matcher = null;
        String result = null;
        if (StringUtils.isNotEmpty(sourceId)) {
            matcher = (Matcher) SourceUtil.getSourceMap(sourceId, request, response);
        }
        if (matcher != null) {
            result = matcher.group(operation.getGroupIndex());
        }
        logger.debug("After match group. index: {}, result: {}", operation.getGroupIndex(), result);
        return result;
    }

}
