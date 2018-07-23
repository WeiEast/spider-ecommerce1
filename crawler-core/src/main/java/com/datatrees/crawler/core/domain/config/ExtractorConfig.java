/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.extractor.ExtractorSelector;
import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:10:32 PM
 */
@Tag("config")
@Path("/config")
public class ExtractorConfig extends AbstractWebsiteConfig {

    /**
     *
     */
    private static final long                    serialVersionUID   = 7882181962225972799L;

    private              List<ExtractorSelector> extractorSelectors = new ArrayList<>();

    private              List<PageExtractor>     pageExtractors     = new ArrayList<>();

    public ExtractorConfig() {
        super();
    }

    @Tag("page-extractor-definition")
    public List<PageExtractor> getPageExtractors() {
        return Collections.unmodifiableList(pageExtractors);
    }

    @Node(value = "page-extractor-definition/page-extractor", registered = true)
    public void setPageExtractors(PageExtractor pageExtractor) {
        this.pageExtractors.add(pageExtractor);
    }

    @Tag("extrator-selectors")
    public List<ExtractorSelector> getExtractorSelectors() {
        return Collections.unmodifiableList(extractorSelectors);
    }

    @Node("extrator-selectors/selector")
    public void setExtractorSelectors(ExtractorSelector extractorSelector) {
        this.extractorSelectors.add(extractorSelector);
    }

    public void clone(ExtractorConfig cloneFrom) {
        super.clone(cloneFrom);
        if (CollectionUtils.isEmpty(extractorSelectors)) {
            this.extractorSelectors.addAll(cloneFrom.getExtractorSelectors());
        }
        if (CollectionUtils.isEmpty(pageExtractors)) {
            this.pageExtractors.addAll(cloneFrom.getPageExtractors());
        }
    }
}
