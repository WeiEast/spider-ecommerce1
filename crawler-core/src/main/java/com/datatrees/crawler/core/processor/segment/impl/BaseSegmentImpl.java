/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.segment.impl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.processor.segment.SegmentBase;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 10:37:37 AM
 */
public class BaseSegmentImpl extends SegmentBase<AbstractSegment> {

    public BaseSegmentImpl(@Nonnull AbstractSegment segment) {
        super(segment);
    }

    @Override
    public List<String> splitInputContent(String content, AbstractSegment segment, Request request, Response response) {
        return Collections.singletonList(content);
    }

}
