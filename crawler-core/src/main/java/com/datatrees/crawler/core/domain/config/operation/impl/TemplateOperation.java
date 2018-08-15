/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
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
 * @since Feb 7, 2014 2:41:26 PM
 */
@Tag("operation")
@Path(".[@type='template']")
public class TemplateOperation extends AbstractOperation {

    /**
     *
     */
    private static final long    serialVersionUID = 2782067152892732984L;

    private              String  template;

    private              Boolean returnObject;

    @Tag
    public String getTemplate() {
        return template;
    }

    @Node("text()")
    public void setTemplate(String template) {
        this.template = template;
    }

    @Attr("return-object")
    public Boolean getReturnObject() {
        return returnObject;
    }

    @Node("@return-object")
    public void setReturnObject(Boolean returnObject) {
        this.returnObject = returnObject;
    }

}
