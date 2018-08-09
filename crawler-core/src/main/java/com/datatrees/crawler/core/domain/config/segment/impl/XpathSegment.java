/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.segment.impl;

import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Path;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 4:11:19 PM
 */
@Path(".[@type='xpath']")
public class XpathSegment extends AbstractSegment {

    private static final long   serialVersionUID = -8540951198783881443L;

    private              String xpath;

    @Attr("value")
    public String getXpath() {
        return xpath;
    }

    @Node("@value")
    public void setXpath(String xpath) {
        this.xpath = xpath;
    }
}
