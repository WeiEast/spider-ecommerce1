package com.datatrees.rawdatacentral.collector.chain.common;

import java.util.List;

import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.FilterConstant;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.collector.search.URLHandlerImpl;
import com.datatrees.rawdatacentral.collector.worker.deduplicate.DuplicateChecker;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午2:34:43
 */
public class ContextUtil {

    public static LinkNode getCurrentLinkNode(Context context) {
        return (LinkNode) context.getAttribute(FilterConstant.CURRENT_LINK_NODE);
    }

    public static void setCurrentLinkNode(Context context, LinkNode node) {
        context.setAttribute(FilterConstant.CURRENT_LINK_NODE, node);
    }

    public static SearchProcessor getSearchProcessor(Context context) {
        return (SearchProcessor) context.getAttribute(FilterConstant.SEARCH_PROCESSOR);
    }

    public static void setSearchProcessor(Context context, SearchProcessor searchProcessor) {
        context.setAttribute(FilterConstant.SEARCH_PROCESSOR, searchProcessor);
    }

    public static CrawlRequest getCrawlRequest(Context context) {
        return (CrawlRequest) context.getAttribute(FilterConstant.CURRENT_REQUEST);
    }

    public static void setCrawlRequest(Context context, CrawlRequest request) {
        context.setAttribute(FilterConstant.CURRENT_REQUEST, request);
    }

    public static CrawlResponse getCrawlResponse(Context context) {
        return (CrawlResponse) context.getAttribute(FilterConstant.CURRENT_RESPONSE);
    }

    public static void setCrawlResponse(Context context, CrawlResponse response) {
        context.setAttribute(FilterConstant.CURRENT_RESPONSE, response);
    }

    public static LinkNode getFetchLinkNode(Context context) {
        return (LinkNode) context.getAttribute(FilterConstant.FECHED_LINK_NODE);
    }

    public static void setFetchLinkNode(Context context, LinkNode fetchLinkNode) {
        context.setAttribute(FilterConstant.FECHED_LINK_NODE, fetchLinkNode);
    }

    public static URLHandlerImpl getURLHandlerImpl(Context context) {
        return (URLHandlerImpl) context.getAttribute(FilterConstant.URL_HANDLER);
    }

    public static void setURLHandlerImpl(Context context, URLHandlerImpl handler) {
        context.setAttribute(FilterConstant.URL_HANDLER, handler);
    }

    public static List<LinkNode> getFetchedLinkNodeList(Context context) {
        return (List<LinkNode>) context.getAttribute(FilterConstant.FETCHED_LINK_NODE_LIST);
    }

    public static void setFetchedLinkNodeList(Context context, List<LinkNode> linkNodeList) {
        context.setAttribute(FilterConstant.FETCHED_LINK_NODE_LIST, linkNodeList);
    }

    public static DuplicateChecker getDuplicateChecker(Context context) {
        return (DuplicateChecker) context.getAttribute(FilterConstant.DUPLICATE_CHECKER);
    }

    public static void setDuplicateChecker(Context context, DuplicateChecker handler) {
        context.setAttribute(FilterConstant.DUPLICATE_CHECKER, handler);
    }

}
