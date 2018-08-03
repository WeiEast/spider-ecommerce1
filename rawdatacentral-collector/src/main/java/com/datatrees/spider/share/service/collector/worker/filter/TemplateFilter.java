package com.datatrees.spider.share.service.collector.worker.filter;

import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import org.apache.commons.lang.StringUtils;

/**
 * @author Jerry
 * @since 12:17 09/01/2018
 */
public final class TemplateFilter {

    private TemplateFilter() {
    }

    public static boolean isFilter(SearchTemplateConfig templateConfig, String expectedTemplateId) {
        if (StringUtils.isNotBlank(expectedTemplateId)) {
            return !expectedTemplateId.contains(templateConfig.getId());
        }

        return Boolean.FALSE.equals(templateConfig.getAutoStart());
    }
}
