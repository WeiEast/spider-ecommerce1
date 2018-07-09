package com.treefinance.crawler.framework.context.control;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.domain.config.search.BusinessType;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;

/**
 * the filter for crawling business.
 * @author Jerry
 * @see BusinessType
 * @since 20:37 2018/5/9
 */
public interface IBusinessTypeFilter {

    boolean isFilter(String businessType, @Nonnull AbstractProcessorContext context);

    default boolean isFilter(BusinessType businessType, @Nonnull AbstractProcessorContext context) {
        return businessType != null && isFilter(businessType.getCode(), context);
    }
}
