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

import com.datatrees.crawler.core.domain.config.extractor.ExtratorSelector;
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
    private static final long serialVersionUID = 7882181962225972799L;
    private List<ExtratorSelector> extratorSelectorList;
    private List<PageExtractor>    pageExtractorList;

    public ExtractorConfig() {
        super();
        pageExtractorList = new ArrayList<PageExtractor>();
        extratorSelectorList = new ArrayList<ExtratorSelector>();
    }

    @Tag("page-extractor-definition")
    public List<PageExtractor> getPageExtractorList() {
        return Collections.unmodifiableList(pageExtractorList);
    }

    @Node(value = "page-extractor-definition/page-extractor", registered = true)
    public void setPageExtractorList(PageExtractor pageExtractor) {
        this.pageExtractorList.add(pageExtractor);
    }

    @Tag("extrator-selectors")
    public List<ExtratorSelector> getExtratorSelectorList() {
        return Collections.unmodifiableList(extratorSelectorList);
    }

    @Node("extrator-selectors/selector")
    public void setExtratorSelectorList(ExtratorSelector extratorSelector) {
        this.extratorSelectorList.add(extratorSelector);
    }

    public void clone(ExtractorConfig cloneFrom) {
        super.clone(cloneFrom);
        if (CollectionUtils.isEmpty(extratorSelectorList)) {
            this.extratorSelectorList.addAll(cloneFrom.getExtratorSelectorList());
        }
        if (CollectionUtils.isEmpty(pageExtractorList)) {
            this.pageExtractorList.addAll(cloneFrom.getPageExtractorList());
        }
    }
}
