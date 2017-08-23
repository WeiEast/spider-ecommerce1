/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:38:12 PM
 */
@Tag("operation")
@Path(".[@type='xpath']")
public class XpathOperation extends AbstractOperation {

    /**
     *
     */
    private static final long serialVersionUID = 3159540420961407796L;
    private String  xpath;
    private Boolean emptyToNull;

    @Attr("empty-to-null")
    public Boolean getEmptyToNull() {
        return emptyToNull;
    }

    @Node("@empty-to-null")
    public void setEmptyToNull(Boolean emptyToNull) {
        this.emptyToNull = emptyToNull;
    }

    @Tag
    public String getXpath() {
        return xpath;
    }

    @Node("text()")
    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "XpathOperation [xpath=" + xpath + "]";
    }

}
