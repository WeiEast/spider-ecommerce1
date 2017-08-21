/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 *
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2016年5月30日 下午2:01:49
 */
@Tag("operation")
@Path(".[@type='returnmatch']")
public class ReturnMatchOperation extends AbstractOperation {

    private static final long serialVersionUID = 4155105411045428793L;
    private String value;

    @Tag
    public String getValue() {
        return value;
    }

    @Node("text()")
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ReturnMatchOperation [value=" + value + "]";
    }


}
