package com.datatrees.rawdatacentral.collector.chain.search;

import javax.annotation.Nonnull;
import java.util.List;

import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 17:18 2018/4/17
 */
abstract class ResponsesFilter implements Filter {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);

        List<Response> responses = ProcessorContextUtil.getThreadLocalResponseList(searchProcessor.getProcessorContext());
        if (CollectionUtils.isNotEmpty(responses)) {
            doInternalFilter(responses, searchProcessor, context);
        }

        filterChain.doFilter(context);
    }

    protected abstract void doInternalFilter(@Nonnull List<Response> responses, SearchProcessor searchProcessor, Context context);
}
