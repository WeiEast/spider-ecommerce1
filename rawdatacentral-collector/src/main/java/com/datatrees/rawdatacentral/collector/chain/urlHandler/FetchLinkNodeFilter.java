package com.datatrees.rawdatacentral.collector.chain.urlHandler;

import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 21:25 2018/4/17
 */
abstract class FetchLinkNodeFilter implements Filter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        LinkNode fetchLinkNode = ContextUtil.getFetchLinkNode(context);
        SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);

        boolean filtered = false;
        try {
            filtered = doInternalFilter(fetchLinkNode, searchProcessor, context);
        } catch (Exception e) {
            logger.error("Error invoking filter", e);
        }

        if (filtered) {
            filterChain.doFilter(context);
        }
    }

    protected abstract boolean doInternalFilter(LinkNode fetchLinkNode, SearchProcessor searchProcessor, Context context);
}
