/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.properties.cookie;

import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 24, 2014 6:09:51 PM
 */
public class BaseCookie extends AbstractCookie {

    /**
     *
     */
    private static final long    serialVersionUID = -1065814613979865015L;

    private              Boolean coexist;

    @Attr("coexist")
    public Boolean getCoexist() {
        return coexist;
    }

    @Node("@coexist")
    public void setCoexist(Boolean coexist) {
        this.coexist = coexist;
    }

}
