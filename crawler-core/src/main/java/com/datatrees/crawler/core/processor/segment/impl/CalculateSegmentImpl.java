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
import com.datatrees.crawler.core.processor.common.SourceUtil;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年9月5日 下午8:16:43
 */
public class CalculateSegmentImpl extends SegmentBase<CalculateSegment> {

    private static final Logger log = LoggerFactory.getLogger(CalculateSegmentImpl.class);

    // private String sourceExpression(Request request, String expression) {
    // Set<String> replaceList = ReplaceUtils.getReplaceList(expression);
    // return ReplaceUtils.replaceMap(replaceList, RequestUtil.getSourceMap(request), expression);
    // }
    //
    //
    // private double sourceCalculate(Request request, String expression) {
    // expression = this.sourceExpression(request, expression);
    // String result = PatternUtils.group(expression, "([\\d\\.]+)", 1);
    // if (result != null && expression.equals(result)) {
    // return Double.parseDouble(expression);
    // } else if (!expression.contains("$")) {
    // log.info("do sourceCalculate with expression:" + expression);
    // return Arithmetic.arithmetic(expression);
    // } else {
    // log.info("return 0 ,with expression:" + expression);
    // return 0;
    // }
    // }

    @Override
    protected List<String> getSplit(Request request) {
        List<String> result = new LinkedList<>();
        try {// 1,3,1,+  从2开始到3(包含3)
            CalculateSegment segment = getSegment();
            String expression = segment.getExpression();
            log.info("start do calculate segment with expression:" + expression);
            String[] arrays = expression.split(",");
            double start = CalculateUtil.sourceCalculate(request, arrays[0], 0d);
            double end = CalculateUtil.sourceCalculate(request, arrays[1], 0d);
            double interval = CalculateUtil.sourceCalculate(request, arrays[2], 0d);
            String formula = SourceUtil.sourceExpression(request, arrays[3]);
            while (start < end) {
                start = CalculateUtil.calculate(start + " " + formula + " " + interval, end);
                result.add(start + "");
            }
        } catch (Exception e) {
            log.error("do CalculateSegmentImpl error" + e.getMessage(), e);
        }

        return result;
    }
}
