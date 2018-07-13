/**
 * This document and its contents are protected by copyright 2005 and owned by Treefinance.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Path;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月9日 下午4:52:35
 */
@Tag("operation")
@Path(".[@type='mailparser']")
public class MailParserOperation extends AbstractOperation {

    /**
     *
     */
    private static final long serialVersionUID = 5545210782890757180L;
    private Boolean bodyParser;

    @Attr("body-parser")
    public Boolean getBodyParser() {
        return bodyParser;
    }

    @Node("@body-parser")
    public void setBodyParser(Boolean bodyParser) {
        this.bodyParser = bodyParser;
    }

}
