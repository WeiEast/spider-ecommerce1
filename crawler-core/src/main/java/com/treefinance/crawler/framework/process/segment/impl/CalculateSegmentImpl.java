/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.process.segment.impl;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

import com.datatrees.crawler.core.domain.config.segment.impl.CalculateSegment;
import com.treefinance.crawler.framework.process.segment.SegmentBase;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.expression.StandardExpression;
import com.treefinance.crawler.framework.util.CalculateUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年9月5日 下午8:16:43
 */
public class CalculateSegmentImpl extends SegmentBase<CalculateSegment> {

    public CalculateSegmentImpl(@Nonnull CalculateSegment segment) {
        super(segment);
    }

    @Override
    protected List<String> splitInputContent(String content, CalculateSegment segment, SpiderRequest request, SpiderResponse response) {
        List<String> result = new LinkedList<>();
        String expression = segment.getExpression();
        logger.info("start calculate segment processor with expression: {}", expression);

        // 1,3,1,+  从2开始到3(包含3)
        String[] arrays = expression.split(",");
        double start = CalculateUtils.calculate(arrays[0], request, response);
        double end = CalculateUtils.calculate(arrays[1], request, response);
        double interval = CalculateUtils.calculate(arrays[2], request, response);
        String formula = StandardExpression.eval(arrays[3], request, response);
        while (start < end) {
            start = CalculateUtils.calculate(start + formula + interval, null, Double.TYPE);
            result.add(start + "");
        }

        return result;
    }
}
