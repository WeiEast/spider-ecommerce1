/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.properties.cookie;

import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Path;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 24, 2014 6:20:40 PM
 */
@Path(".[@scope='custom']")
public class CustomCookie extends BaseCookie {

    /**
     *
     */
    private static final long serialVersionUID = 6827347510874254280L;
    private String failPattern;
    private String handleConfig;// pugnigid or url

    @Attr("fail-pattern")
    public String getFailPattern() {
        return failPattern;
    }

    @Node("@fail-pattern")
    public void setFailPattern(String failPattern) {
        this.failPattern = failPattern;
    }

    @Tag
    public String getHandleConfig() {
        return handleConfig;
    }

    @Node("text()")
    public void setHandleConfig(String handleConfig) {
        this.handleConfig = handleConfig;
    }

}
