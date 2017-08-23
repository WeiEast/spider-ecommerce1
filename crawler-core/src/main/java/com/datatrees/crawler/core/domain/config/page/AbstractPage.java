/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.domain.config.segment.impl.*;
import com.datatrees.crawler.core.util.xml.annotation.ChildTag;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.definition.AbstractBeanDefinition;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 上午11:43:48
 */
public abstract class AbstractPage extends AbstractBeanDefinition {

    private List<AbstractSegment> segmentList;

    /**
     *
     */
    public AbstractPage() {
        super();
        segmentList = new ArrayList<AbstractSegment>();
    }

    @ChildTag("object-segment")
    public List<AbstractSegment> getSegmentList() {
        return Collections.unmodifiableList(segmentList);
    }

    @Node(value = "object-segment", types = {XpathSegment.class, JsonPathSegment.class, RegexSegment.class, SplitSegment.class, CalculateSegment.class, BaseSegment.class})
    public void setSegmentList(AbstractSegment segment) {
        this.segmentList.add(segment);
    }

}
