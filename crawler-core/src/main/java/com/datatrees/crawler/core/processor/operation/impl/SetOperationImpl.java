/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.SetOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:34 PM
 */
public class SetOperationImpl extends Operation<SetOperation> {

    private static final Pattern EMPTY_PATTERN = Pattern.compile("^\\s*\\$\\{empty}\\s*$", Pattern.CASE_INSENSITIVE);

    public SetOperationImpl(@Nonnull SetOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected Object doOperation(@Nonnull SetOperation operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception {
        String value = operation.getValue();

        if(EMPTY_PATTERN.matcher(value).matches()){
            value = StringUtils.EMPTY;
        }

        return value;
    }

}
