/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.segment.impl;

import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 4:16:27 PM
 */
@Path(".[@type='regex']")
public class RegexSegment extends AbstractSegment {

    /**
     *
     */
    private static final long    serialVersionUID = 3272694924275477445L;

    private              String  regex;

    private              Integer groupIndex;

    @Attr("value")
    public String getRegex() {
        return regex;
    }

    @Node("@value")
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Attr("group-index")
    public Integer getGroupIndex() {
        return groupIndex == null ? 0 : groupIndex;
    }

    @Node("@group-index")
    public void setGroupIndex(Integer groupIndex) {
        this.groupIndex = groupIndex;
    }

}
