/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.segment.impl;

import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 4:16:53 PM
 */
@Path(".[@type='split']")
public class SplitSegment extends AbstractSegment {

    /**
     *
     */
    private static final long    serialVersionUID = -4391709858547716289L;

    private              String  splitString;

    private              Boolean append;

    @Attr("append")
    public Boolean getAppend() {
        return append;
    }

    @Node("@append")
    public void setAppend(Boolean append) {
        this.append = append;
    }

    @Attr("value")
    public String getSplitString() {
        return splitString;
    }

    @Node("@value")
    public void setSplitString(String splitString) {
        this.splitString = splitString;
    }
}
