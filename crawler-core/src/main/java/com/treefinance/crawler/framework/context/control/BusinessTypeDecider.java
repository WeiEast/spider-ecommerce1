package com.treefinance.crawler.framework.context.control;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import com.datatrees.crawler.core.domain.config.search.BusinessType;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;

/**
 * the decider to decide whether the given crawling business need to be call.
 * @author Jerry
 * @see IBusinessTypeFilter
 * @see BusinessType
 * @since 20:40 2018/5/9
 */
public final class BusinessTypeDecider {

    private static final AtomicReference<Queue<IBusinessTypeFilter>> REF = new AtomicReference<>();

    private BusinessTypeDecider() {
    }

    private static Queue<IBusinessTypeFilter> queue() {
        return REF.get();
    }

    private static Queue<IBusinessTypeFilter> ensureQueue() {
        while (true) {
            Queue<IBusinessTypeFilter> collection = queue();
            if (collection != null) return collection;
            collection = new ConcurrentLinkedQueue<>();
            if (REF.compareAndSet(null, collection)) return collection;
        }
    }

    public static void registerFilters(Collection<IBusinessTypeFilter> filters) {
        if (filters != null) {
            filters.forEach(BusinessTypeDecider::registerFilter);
        }
    }

    public static void registerFilter(IBusinessTypeFilter filter) {
        if (filter != null) {
            ensureQueue().add(filter);
        }
    }

    public static boolean support(BusinessType businessType, AbstractProcessorContext processorContext) {
        // 解析阶段不控制过滤
        if (processorContext instanceof ExtractorProcessorContext) {
            return true;
        }

        Queue<IBusinessTypeFilter> queue = queue();
        if (queue != null) {
            for (IBusinessTypeFilter filter : queue) {
                if (filter.isFilter(businessType, processorContext)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean support(String businessType, AbstractProcessorContext processorContext) {
        // 解析阶段不控制过滤
        if (processorContext instanceof ExtractorProcessorContext) {
            return true;
        }

        Queue<IBusinessTypeFilter> queue = queue();
        if (queue != null) {
            for (IBusinessTypeFilter filter : queue) {
                if (filter.isFilter(businessType, processorContext)) {
                    return false;
                }
            }
        }

        return true;
    }
}
