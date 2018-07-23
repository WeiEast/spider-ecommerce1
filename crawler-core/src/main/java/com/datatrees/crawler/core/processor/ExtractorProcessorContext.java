/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.ExtractorConfig;
import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.treefinance.toolkit.util.Preconditions;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 7, 2014 2:14:19 PM
 */
public class ExtractorProcessorContext extends AbstractProcessorContext {

    private final Map<String, PageExtractor> pageExtractorMap = new HashMap<>();

    public ExtractorProcessorContext(Website website, Long taskId) {
        super(website, taskId);

        Preconditions.notNull("extractor-config", website.getExtractorConfig());
    }

    @Override
    public void init() {
        List<PageExtractor> PageExtractorList = getExtractorConfig().getPageExtractors();
        if (CollectionUtils.isNotEmpty(PageExtractorList)) {
            for (PageExtractor p : PageExtractorList) {
                pageExtractorMap.put(p.getId(), p);
            }
        }

        // init plugin
        registerPlugins(getExtractorConfig().getPluginList());
    }

    public ExtractorConfig getExtractorConfig() {
        return getWebsite().getExtractorConfig();
    }

    /**
     * @return the pageExtractorMap
     */
    public Map<String, PageExtractor> getPageExtractorMap() {
        return Collections.unmodifiableMap(pageExtractorMap);
    }

}
