/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.common;

import junit.framework.Assert;

import org.junit.Test;

import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.domain.config.segment.impl.BaseSegment;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.segment.SegmentBase;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 2:11:42 PM
 */
public class TypeRegistryTest {

    @Test
    public void testGetSegmentExists() {
        AbstractSegment segment = new BaseSegment();
        try {
            SegmentBase base = ProcessorFactory.getSegment(segment);
            Assert.assertNotNull(base);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetSegmentNotExists() {
        AbstractSegment segment = new BaseSegment();
        segment.setType("xxx");
        try {
            SegmentBase base = ProcessorFactory.getSegment(segment);
            Assert.assertNull(base);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

}
