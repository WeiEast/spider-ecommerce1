/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.page;

import java.io.Serializable;

import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:06:47 PM
 */
@Tag("replace")
public class Replacement implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6456178618030598235L;
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

}
