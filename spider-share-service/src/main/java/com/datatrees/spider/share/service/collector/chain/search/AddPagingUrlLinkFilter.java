package com.datatrees.spider.share.service.collector.chain.search;

import java.util.List;

import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.spider.share.service.collector.chain.Context;
import com.datatrees.spider.share.service.collector.chain.Filter;
import com.datatrees.spider.share.service.collector.chain.FilterChain;
import com.datatrees.spider.share.service.collector.search.SearchProcessor;
import com.treefinance.crawler.framework.process.search.SearchTemplateCombine;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午2:47:10
 */
public class AddPagingUrlLinkFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AddPagingUrlLinkFilter.class);

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        LinkNode linkNode = context.getCurrentLinkNode();
        SearchProcessor searchProcessor = context.getSearchProcessor();
        String lastPagingUrl = linkNode.getUrl();

        int pNum = linkNode.getpNum();
        if (pNum != -1) {
            log.info("current PageNum: {},pageLinkUrl: {}", pNum, lastPagingUrl);
            searchProcessor.getTask().getOpenPageCount().getAndIncrement();
            CrawlRequest request = context.getCrawlRequest();
            CrawlResponse response = context.getCrawlResponse();
            if (needAddPagingUrl(request, response)) {
                int newpNum = pNum + 1;
                String pageLinkUrl = getPageLinkUrl(newpNum, searchProcessor);
                if (!pageLinkUrl.equals(lastPagingUrl)) {
                    LinkNode pageNumUrlLink = new LinkNode(pageLinkUrl);
                    copyProperties(pageNumUrlLink, linkNode);
                    log.info("add newpNum: {},pageLinkUrl: {}", newpNum, pageLinkUrl);
                    pageNumUrlLink.setpNum(newpNum);

                    List<LinkNode> linkNodeList = context.getFetchedLinkNodeList();
                    linkNodeList.add(pageNumUrlLink);
                }
            }
        }

        filterChain.doFilter(context);
    }

    private boolean needAddPagingUrl(CrawlRequest request, CrawlResponse response) {
        int responseStatus = response.getStatus();

        if (responseStatus == Status.NO_SEARCH_RESULT || responseStatus == Status.LAST_PAGE) {
            log.warn("needAddPagingUrl : false, responseStatus: {}", responseStatus);
            return false;
        }

        if (StringUtils.isEmpty(RequestUtil.getContent(request))) {
            log.warn("needAddPagingUrl : false, pageContent is empty.");
            return false;
        }
        return true;
    }

    private String getPageLinkUrl(int currentPageNum, SearchProcessor searchProcessor) {
        log.info("getPageLinkUrl currentPageNum: {}", currentPageNum);
        return SearchTemplateCombine
                .constructSearchURL(searchProcessor.getSearchTemplate(), searchProcessor.getKeyword(), searchProcessor.getEncoding(), currentPageNum,
                        true, searchProcessor.getProcessorContext().getVisibleScope());
    }

    /**
     * @param current
     */
    private void copyProperties(LinkNode pageNumUrlLink, LinkNode current) {
        pageNumUrlLink.setDepth(current.getDepth());
        pageNumUrlLink.setReferer(current.getReferer());
        pageNumUrlLink.setPageTitle(current.getPageTitle());
        pageNumUrlLink.setpId(current.getPageTitle());
        pageNumUrlLink.setRetryCount(0);
        pageNumUrlLink.setpNum(current.getpNum());
        pageNumUrlLink.addHeaders(current.getHeaders());
    }
}
