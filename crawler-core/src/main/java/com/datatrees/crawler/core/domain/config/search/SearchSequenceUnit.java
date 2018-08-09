/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.search;

import java.io.Serializable;

import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 5:26:35 PM
 */
@Tag("page")
public class SearchSequenceUnit implements Serializable {

    /**
     *
     */
    private static final long    serialVersionUID = -3034367943949209346L;

    private              Integer depth;

    private              Page    page;

    @Attr("depth")
    public Integer getDepth() {
        return depth;
    }

    @Node("@depth")
    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    @Attr(value = "ref", referenced = true)
    public Page getPage() {
        return page;
    }

    @Node(value = "@ref", referenced = true)
    public void setPage(Page page) {
        this.page = page;
    }

}
