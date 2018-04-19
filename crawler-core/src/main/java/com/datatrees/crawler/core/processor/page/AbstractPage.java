/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.page;

import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.processor.common.Processor;
import com.datatrees.crawler.core.processor.page.handler.BusinessTypeFilterHandler;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 5:02:13 PM
 */
public abstract class AbstractPage extends Processor {

    protected Page page = null;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

}
