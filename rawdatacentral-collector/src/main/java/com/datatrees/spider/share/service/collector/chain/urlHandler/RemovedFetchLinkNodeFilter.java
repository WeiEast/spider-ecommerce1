package com.datatrees.spider.share.service.collector.chain.urlHandler;

import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.spider.share.service.collector.chain.Context;
import com.datatrees.spider.share.service.collector.search.SearchProcessor;

/**
 * @author Jerry
 * @since 21:31 2018/4/17
 */
abstract class RemovedFetchLinkNodeFilter extends FetchLinkNodeFilter {

    @Override
    protected boolean doInternalFilter(LinkNode fetchLinkNode, SearchProcessor searchProcessor, Context context) {
        doProcess(fetchLinkNode, searchProcessor, context);

        if (!fetchLinkNode.isRemoved()) {
            return true;
        }
        searchProcessor.getTask().getFilteredCount().getAndIncrement();
        return false;
    }

    protected abstract void doProcess(LinkNode fetchLinkNode, SearchProcessor searchProcessor, Context context);
}
