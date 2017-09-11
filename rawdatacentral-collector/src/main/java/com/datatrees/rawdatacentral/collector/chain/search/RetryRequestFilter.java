package com.datatrees.rawdatacentral.collector.chain.search;

import java.util.List;

import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.domain.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午3:19:21
 */
public class RetryRequestFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RetryRequestFilter.class);

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
        Task task = searchProcessor.getTask();
        List<LinkNode> threadLocalLinkNode = ProcessorContextUtil.getThreadLocalLinkNode(searchProcessor.getProcessorContext());
        if (threadLocalLinkNode != null) {
            for (LinkNode linkNode : threadLocalLinkNode) {
                if (!linkNode.isNeedRequeue()) {
                    if (log.isDebugEnabled()) {
                        log.debug("add new retry Count :" + linkNode.getRetryCount());
                    }
                    task.getRetryCount().addAndGet(linkNode.getRetryCount());
                } else {
                    // requeue linknode
                    linkNode.setNeedRequeue(false);
                    List<LinkNode> linkNodeList = ContextUtil.getFetchedLinkNodeList(context);
                    linkNodeList.add(linkNode);
                }
            }
        }
        log.debug("Execute RetryRequestFilter end...");
        filterChain.doFilter(context, filterChain);
    }

}
