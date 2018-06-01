package com.datatrees.rawdatacentral.collector.chain.search;

import javax.annotation.Nonnull;
import java.util.List;

import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 18:41 2018/4/17
 */
abstract class LinkNodesFilter implements Filter {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        SearchProcessor searchProcessor = context.getSearchProcessor();
        List<LinkNode> linkNodes = ProcessorContextUtil.getThreadLocalLinkNode(searchProcessor.getProcessorContext());
        if (CollectionUtils.isNotEmpty(linkNodes)) {
            doInternalFilter(linkNodes, searchProcessor, context);
        }

        filterChain.doFilter(context);
    }

    protected abstract void doInternalFilter(@Nonnull List<LinkNode> linkNodes, SearchProcessor searchProcessor, Context context);

}
