/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.segment.impl;

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 10:37:37 AM
 */
public class BaseSegmentImpl extends SegmentBase<AbstractSegment> {

    /*
     * (non-Javadoc)
     * empty imple
     * @see
     */
    @Override
    public List<String> getSplit(Request request) {
        return Collections.singletonList(RequestUtil.getContent(request));
    }

}
