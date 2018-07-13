/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Path;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 26, 2014 8:02:11 PM
 */
@Tag("operation")
@Path(".[@type='set']")
public class SetOperation extends AbstractOperation {

    /**
     *
     */
    private static final long serialVersionUID = 8770876253603267778L;
    private String value;

    /**
     * @return the value
     */
    @Tag
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    @Node("text()")
    public void setValue(String value) {
        this.value = value;
    }

}
