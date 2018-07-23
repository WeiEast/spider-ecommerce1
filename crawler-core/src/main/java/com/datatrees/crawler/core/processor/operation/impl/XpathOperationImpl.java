/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.XpathOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:48 PM
 */
public class XpathOperationImpl extends Operation<XpathOperation> {

    public XpathOperationImpl(@Nonnull XpathOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected Object doOperation(@Nonnull XpathOperation operation, @Nonnull Object operatingData, @Nonnull Request request,
            @Nonnull Response response) throws Exception {
        String xpath = operation.getXpath();

        xpath = StandardExpression.eval(xpath, request, response);

        String result;
        List<String> segments = XPathUtil.getXpath(xpath, (String) operatingData);
        if (CollectionUtils.isNotEmpty(segments)) {
            result = segments.stream().collect(Collectors.joining());
        } else {
            logger.warn("xpath extract empty content! - {}", xpath);
            result = StringUtils.EMPTY;
        }

        if (result.isEmpty() && BooleanUtils.isTrue(operation.getEmptyToNull())) {
            result = null;
        }

        return result;
    }
}
