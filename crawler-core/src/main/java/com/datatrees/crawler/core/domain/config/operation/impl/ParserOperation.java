/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.domain.config.parser.Parser;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Path;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:35:15 PM
 */
@Tag("operation")
@Path(".[@type='parser']")
public class ParserOperation extends AbstractOperation {

    /**
     *
     */
    private static final long serialVersionUID = 7608874201077082625L;
    private Parser parser;

    @Attr(value = "ref", referenced = true)
    public Parser getParser() {
        return parser;
    }

    @Node(value = "@ref", referenced = true)
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ParserOperation [parser=" + parser + "]";
    }
}
