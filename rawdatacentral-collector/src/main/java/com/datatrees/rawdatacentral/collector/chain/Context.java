package com.datatrees.rawdatacentral.collector.chain;

import java.util.List;

import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.collector.search.URLHandlerImpl;
import com.datatrees.rawdatacentral.collector.worker.deduplicate.DuplicateChecker;
import com.treefinance.crawler.lang.AtomicAttributes;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午2:34:43
 */
public class Context extends AtomicAttributes {

    private final static String FETCHED_LINK_NODE_LIST = "FETCHED_LINK_NODE_LIST";

    private final static String CURRENT_REQUEST        = "CURRENT_REQUEST";

    private final static String CURRENT_RESPONSE       = "CURRENT_RESPONSE";

    // search processor filter
    private final static String SEARCH_PROCESSOR       = "SEARCH_PROCESSOR";

    private final static String DUPLICATE_CHECKER      = "DUPLICATE_CHECKER";

    private final static String FECHED_LINK_NODE       = "FECHED_LINK_NODE";

    // hosting site & redlist filter
    private final static String URL_HANDLER            = "URL_HANDLER";

    // common proerites
    private final static String CURRENT_LINK_NODE      = "CURRENT_LINK_NODE";

    public LinkNode getCurrentLinkNode() {
        return (LinkNode) getAttribute(CURRENT_LINK_NODE);
    }

    public void setCurrentLinkNode(LinkNode node) {
        setAttribute(CURRENT_LINK_NODE, node);
    }

    public SearchProcessor getSearchProcessor() {
        return (SearchProcessor) getAttribute(SEARCH_PROCESSOR);
    }

    public void setSearchProcessor(SearchProcessor searchProcessor) {
        setAttribute(SEARCH_PROCESSOR, searchProcessor);
    }

    public CrawlRequest getCrawlRequest() {
        return (CrawlRequest) getAttribute(CURRENT_REQUEST);
    }

    public void setCrawlRequest(CrawlRequest request) {
        setAttribute(CURRENT_REQUEST, request);
    }

    public CrawlResponse getCrawlResponse() {
        return (CrawlResponse) getAttribute(CURRENT_RESPONSE);
    }

    public void setCrawlResponse(CrawlResponse response) {
        setAttribute(CURRENT_RESPONSE, response);
    }

    public LinkNode getFetchLinkNode() {
        return (LinkNode) getAttribute(FECHED_LINK_NODE);
    }

    public void setFetchLinkNode(LinkNode fetchLinkNode) {
        setAttribute(FECHED_LINK_NODE, fetchLinkNode);
    }

    public URLHandlerImpl getURLHandlerImpl() {
        return (URLHandlerImpl) getAttribute(URL_HANDLER);
    }

    public void setURLHandlerImpl(URLHandlerImpl handler) {
        setAttribute(URL_HANDLER, handler);
    }

    public List<LinkNode> getFetchedLinkNodeList() {
        return (List<LinkNode>) getAttribute(FETCHED_LINK_NODE_LIST);
    }

    public void setFetchedLinkNodeList(List<LinkNode> linkNodeList) {
        setAttribute(FETCHED_LINK_NODE_LIST, linkNodeList);
    }

    public DuplicateChecker getDuplicateChecker() {
        return (DuplicateChecker) getAttribute(DUPLICATE_CHECKER);
    }

    public void setDuplicateChecker(DuplicateChecker handler) {
        setAttribute(DUPLICATE_CHECKER, handler);
    }

}
