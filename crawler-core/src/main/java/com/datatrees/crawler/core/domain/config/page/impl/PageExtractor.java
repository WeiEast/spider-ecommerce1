/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.page.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.extractor.PageSource;
import com.datatrees.crawler.core.domain.config.page.AbstractPage;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:16:10 PM
 */
@Tag("page-extractor")
public class PageExtractor extends AbstractPage {

    private List<PageSource> pageSourceList;
    private Boolean          disAlternative;

    public PageExtractor() {
        super();
        pageSourceList = new ArrayList<PageSource>();
    }

    @Attr("dis-alternative")
    public Boolean getDisAlternative() {
        return disAlternative;
    }

    @Node("@dis-alternative")
    public void setDisAlternative(Boolean disAlternative) {
        this.disAlternative = disAlternative;
    }

    @Tag("page-sources")
    public List<PageSource> getPageSourceList() {
        return Collections.unmodifiableList(pageSourceList);

    }

    @Node("page-sources/source")
    public void setPageSourceList(PageSource pageSource) {
        this.pageSourceList.add(pageSource);
    }

}
