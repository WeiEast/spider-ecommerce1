package com.datatrees.rawdatacentral.collector.chain.search;

import java.util.List;

import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.search.SearchTemplateCombine;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午2:47:10
 */
public class AddPagingUrlLinkFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AddPagingUrlLinkFilter.class);

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        LinkNode linkNode = ContextUtil.getCurrentLinkNode(context);
        SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
        String lastPagingUrl = linkNode.getUrl();

        int pNum = linkNode.getpNum();
        if (pNum != -1) {
            log.info("current PageNum: " + pNum + " ,pageLinkUrl: " + lastPagingUrl);
            searchProcessor.getTask().getOpenPageCount().getAndIncrement();
            CrawlRequest request = ContextUtil.getCrawlRequest(context);
            CrawlResponse response = ContextUtil.getCrawlResponse(context);
            if (needAddPagingUrl(request, response)) {
                int newpNum = pNum + 1;
                String pageLinkUrl = getPageLinkUrl(newpNum, searchProcessor);
                if (!pageLinkUrl.equals(lastPagingUrl)) {
                    LinkNode pageNumUrlLink = new LinkNode(pageLinkUrl);
                    copyProperties(pageNumUrlLink, linkNode);
                    log.info("add newpNum: " + newpNum + " ,pageLinkUrl: " + pageLinkUrl);
                    pageNumUrlLink.setpNum(newpNum);

                    List<LinkNode> linkNodeList = ContextUtil.getFetchedLinkNodeList(context);
                    linkNodeList.add(pageNumUrlLink);
                }
            }
        }

        filterChain.doFilter(context);
    }

    private boolean needAddPagingUrl(CrawlRequest request, CrawlResponse response) {
        int responseStatus = ResponseUtil.getResponseStatus(response);
        if (responseStatus == Status.NO_SEARCH_RESULT || responseStatus == Status.LAST_PAGE || StringUtils.isEmpty(RequestUtil.getContent(request))) {
            log.warn("isContinuousPageFlag : " + " responseStatus " + responseStatus + " pageContent is empty : " + StringUtils.isEmpty(RequestUtil.getContent(request)));
            return false;
        }
        return true;
    }

    private String getPageLinkUrl(int currentPageNum, SearchProcessor searchProcessor) {
        log.info("getPageLinkUrl currentPageNum:  " + currentPageNum);
        String pageLinkUrl = SearchTemplateCombine.constructSearchURL(searchProcessor.getSearchTemplate(), searchProcessor.getKeyword(), searchProcessor.getEncoding(), currentPageNum, true, searchProcessor.getProcessorContext().getContext());
        return pageLinkUrl;
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
