/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.filter;

import java.io.Serializable;

import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:50:15 AM
 */
@Tag("url-filter")
public class UrlFilter implements Serializable {

    /**
     *
     */
    private static final long       serialVersionUID = 8670172857562280034L;

    private              FilterType type;

    private              String     filter;

    @Attr("type")
    public FilterType getType() {
        return type;
    }

    @Node("@type")
    public void setType(String type) {
        this.type = FilterType.getFilterType(type);
    }

    @Tag
    public String getFilter() {
        return filter;
    }

    @Node("text()")
    public void setFilter(String filter) {
        this.filter = filter;
    }
}
