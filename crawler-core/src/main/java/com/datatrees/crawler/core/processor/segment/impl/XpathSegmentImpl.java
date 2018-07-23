/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.segment.impl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.segment.impl.XpathSegment;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 25, 2014 10:03:41 AM
 */
public class XpathSegmentImpl extends SegmentBase<XpathSegment> {

    public XpathSegmentImpl(@Nonnull XpathSegment segment) {
        super(segment);
    }

    @Override
    public List<String> splitInputContent(String content, XpathSegment segment, Request request, Response response) {
        if (StringUtils.isEmpty(content)) {
            return Collections.emptyList();
        }

        String xpath = segment.getXpath();

        logger.debug("Xpath: {}", xpath);

        xpath = StringUtils.trimToEmpty(xpath);

        if (!xpath.isEmpty()) {
            xpath = StandardExpression.eval(xpath, request, response);

            logger.debug("Actual xpath: {}", xpath);

            List<String> segments = XPathUtil.getXpath(xpath, content);

            logger.info("jsonpath: {}, segments size: {}", xpath, segments.size());

            return segments;
        }

        return Collections.singletonList(content);
    }

}
