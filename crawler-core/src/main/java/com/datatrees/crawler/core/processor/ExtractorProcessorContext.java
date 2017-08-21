/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.google.common.base.Preconditions;


/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 7, 2014 2:14:19 PM
 */
public class ExtractorProcessorContext extends AbstractProcessorContext {


    private final Map<String, PageExtractor> pageExtractorMap = new HashMap<String, PageExtractor>();

    /**
     * @param website
     */
    public ExtractorProcessorContext(Website website) {
        super(website);
        Preconditions.checkNotNull(website.getExtractorConfig(), "website extractor config should not be empty!");
    }

    @Override
    public void init() {

        List<PageExtractor> PageExtractorList = this.website.getExtractorConfig().getPageExtractorList();
        if (CollectionUtils.isNotEmpty(PageExtractorList)) {
            for (PageExtractor p : PageExtractorList) {
                pageExtractorMap.put(p.getId(), p);
            }
        }
        // init plugin
        List<AbstractPlugin> plugins = website.getExtractorConfig().getPluginList();
        if (CollectionUtils.isNotEmpty(plugins)) {
            for (AbstractPlugin plugin : plugins) {
                pluginMaps.put(plugin.getId(), plugin);
            }
        }
    }

    /**
     * @return the pageExtractorMap
     */
    public Map<String, PageExtractor> getPageExtractorMap() {
        return pageExtractorMap;
    }


}
