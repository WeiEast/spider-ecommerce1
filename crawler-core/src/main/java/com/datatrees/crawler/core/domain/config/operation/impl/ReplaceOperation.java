/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
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
 * @since Feb 7, 2014 2:33:05 PM
 */
@Tag("operation")
@Path(".[@type='replace']")
public class ReplaceOperation extends AbstractOperation {

    /**
     *
     */
    private static final long serialVersionUID = 5074040034906359688L;

    private String from;

    private String to;

    @Attr("from")
    public String getFrom() {
        return from;
    }

    @Node("@from")
    public void setFrom(String from) {
        this.from = from;
    }

    @Attr("to")
    public String getTo() {
        return to;
    }

    @Node("@to")
    public void setTo(String to) {
        this.to = to;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ReplaceOperation [from=" + from + ", to=" + to + "]";
    }

}
