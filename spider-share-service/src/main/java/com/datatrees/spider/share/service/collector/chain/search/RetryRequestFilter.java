package com.datatrees.spider.share.service.collector.chain.search;

import javax.annotation.Nonnull;
import java.util.List;

import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.spider.share.service.collector.chain.Context;
import com.datatrees.spider.share.service.collector.search.SearchProcessor;
import com.datatrees.spider.share.domain.model.Task;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午3:19:21
 */
public class RetryRequestFilter extends LinkNodesFilter {

    @Override
    protected void doInternalFilter(@Nonnull List<LinkNode> linkNodes, SearchProcessor searchProcessor, Context context) {
        Task task = searchProcessor.getTask();
        for (LinkNode linkNode : linkNodes) {
            if (!linkNode.isNeedRequeue()) {
                log.debug("add new retry Count : {}", linkNode.getRetryCount());

                task.getRetryCount().addAndGet(linkNode.getRetryCount());
            } else {
                // requeue linknode
                linkNode.setNeedRequeue(false);
                List<LinkNode> linkNodeList = context.getFetchedLinkNodeList();
                linkNodeList.add(linkNode);
            }
        }
    }

}