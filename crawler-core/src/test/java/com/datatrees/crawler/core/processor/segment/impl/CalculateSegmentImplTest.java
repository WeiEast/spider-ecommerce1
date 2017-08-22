package com.datatrees.crawler.core.processor.segment.impl;

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.domain.config.segment.impl.CalculateSegment;
import org.junit.Test;

/**
 * @author Jerry
 * @since 23:14 21/05/2017
 */
public class CalculateSegmentImplTest {
    @Test
    public void getSplit() throws Exception {
        CalculateSegmentImpl segmentImpl = new CalculateSegmentImpl();
        CalculateSegment segment = new CalculateSegment();
        segmentImpl.setSegment(segment);
        segment.setExpression("1,3,1,+");
        System.out.println(segmentImpl.getSplit(new Request()));
    }

}