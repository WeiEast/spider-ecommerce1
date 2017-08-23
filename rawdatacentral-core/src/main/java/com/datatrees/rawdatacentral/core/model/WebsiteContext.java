/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model;

import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 下午8:04:43
 */
public class WebsiteContext {

    private ExtractorProcessorContext extractorProcessorContext;
    private SearchProcessorContext    searchProcessorContext;

    /**
     * @return the extractorProcessorContext
     */
    public ExtractorProcessorContext getExtractorProcessorContext() {
        return extractorProcessorContext;
    }

    /**
     * @param extractorProcessorContext the extractorProcessorContext to set
     */
    public void setExtractorProcessorContext(ExtractorProcessorContext extractorProcessorContext) {
        this.extractorProcessorContext = extractorProcessorContext;
    }

    /**
     * @return the searchProcessorContext
     */
    public SearchProcessorContext getSearchProcessorContext() {
        return searchProcessorContext;
    }

    /**
     * @param searchProcessorContext the searchProcessorContext to set
     */
    public void setSearchProcessorContext(SearchProcessorContext searchProcessorContext) {
        this.searchProcessorContext = searchProcessorContext;
    }

}
