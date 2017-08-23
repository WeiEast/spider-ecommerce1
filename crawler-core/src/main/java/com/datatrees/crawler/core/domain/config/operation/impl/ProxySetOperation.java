/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.proxyset.Option;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:45:34 PM
 */
@Tag("operation")
@Path(".[@type='proxyset']")
public class ProxySetOperation extends AbstractOperation {

    /**
     *
     */
    private static final long serialVersionUID = -4650672405535297595L;
    private Option option;

    @Attr("option")
    public Option getOption() {
        return option;
    }

    @Node("@option")
    public void setOption(String option) {
        this.option = Option.getOptionType(option);
    }

}
