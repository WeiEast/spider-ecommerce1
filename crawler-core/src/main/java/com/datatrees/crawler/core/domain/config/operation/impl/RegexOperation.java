/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:45:34 PM
 */
@Tag("operation")
@Path(".[@type='regex']")
public class RegexOperation extends AbstractOperation {

    /**
     *
     */
    private static final long    serialVersionUID = 4494685192256482511L;

    private              String  regex;

    private              Integer groupIndex;

    @Tag
    public String getRegex() {
        return regex;
    }

    @Node("text()")
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Attr("index")
    public Integer getGroupIndex() {
        return groupIndex;
    }

    @Node("@index")
    public void setGroupIndex(Integer groupIndex) {
        this.groupIndex = groupIndex;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RegexOperation [regex=" + regex + ", groupIndex=" + groupIndex + "]";
    }

}
