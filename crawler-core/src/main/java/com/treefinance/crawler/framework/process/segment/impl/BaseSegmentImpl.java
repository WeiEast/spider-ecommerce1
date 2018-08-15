/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.process.segment.impl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.segment.impl.BaseSegment;
import com.treefinance.crawler.framework.process.segment.SegmentBase;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 10:37:37 AM
 */
public class BaseSegmentImpl extends SegmentBase<BaseSegment> {

    public BaseSegmentImpl(@Nonnull BaseSegment segment) {
        super(segment);
    }

    @Override
    public List<String> splitInputContent(String content, BaseSegment segment, SpiderRequest request, SpiderResponse response) {
        return Collections.singletonList(content);
    }

}
