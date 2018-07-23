/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.escape.EscapeType;
import com.datatrees.crawler.core.domain.config.operation.impl.escape.HandlingType;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 26, 2014 8:02:11 PM
 */
@Tag("operation")
@Path(".[@type='escape']")
public class EscapeOperation extends AbstractOperation {

    /**
     *
     */
    private static final long         serialVersionUID = 2187657297214938947L;

    private              EscapeType   escapeType;

    private              HandlingType handlingType;

    @Attr("escape-type")
    public EscapeType getEscapeType() {
        return escapeType;
    }

    @Node("@escape-type")
    public void setEscapeType(String escapeType) {
        this.escapeType = EscapeType.getEscapeType(escapeType);
    }

    @Attr("handling-type")
    public HandlingType getHandlingType() {
        return handlingType;
    }

    @Node("@handling-type")
    public void setHandlingType(String handlingType) {
        this.handlingType = HandlingType.getOperationType(handlingType);
    }

}
