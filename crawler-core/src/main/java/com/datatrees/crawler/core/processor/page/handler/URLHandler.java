/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.page.handler;

import com.datatrees.crawler.core.processor.bean.LinkNode;

/**
 * link node handler usage: collect url , extract host url from current url
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 17, 2014 10:34:27 AM
 */
public interface URLHandler {

    /**
     * handle url from current request
     * @param current current request url , contains meta info like page title , imdb etc
     * @param fetched current fetched url
     */
    public boolean handle(LinkNode current, LinkNode fetched);

}
