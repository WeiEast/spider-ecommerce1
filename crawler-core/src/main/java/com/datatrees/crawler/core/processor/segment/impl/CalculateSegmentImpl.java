/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.segment.impl;

import java.util.LinkedList;
import java.util.List;

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.domain.config.segment.impl.CalculateSegment;
import com.datatrees.crawler.core.processor.common.CalculateUtil;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.treefinance.crawler.framework.expression.StandardExpression;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年9月5日 下午8:16:43
 */
public class CalculateSegmentImpl extends SegmentBase<CalculateSegment> {

    @Override
    protected List<String> getSplit(Request request) {
        List<String> result = new LinkedList<>();
        CalculateSegment segment = getSegment();
        String expression = segment.getExpression();
        logger.info("start do calculate segment with expression: {}", expression);

        try {// 1,3,1,+  从2开始到3(包含3)
            String[] arrays = expression.split(",");
            double start = CalculateUtil.calculate(arrays[0], request);
            double end = CalculateUtil.calculate(arrays[1], request);
            double interval = CalculateUtil.calculate(arrays[2], request);
            String formula = StandardExpression.eval(arrays[3], request, null);
            while (start < end) {
                start = CalculateUtil.calculate(start + formula + interval, null, Double.TYPE);
                result.add(start + "");
            }
        } catch (Exception e) {
            logger.error("error calculating for segment: {}, expression: {}", segment.getName(), expression, e);
        }

        return result;
    }
}
