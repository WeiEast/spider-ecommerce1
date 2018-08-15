/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.page;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.URLUtil;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.domain.config.filter.FilterType;
import com.datatrees.crawler.core.domain.config.filter.UrlFilter;
import com.datatrees.crawler.core.domain.config.page.Regexp;
import com.datatrees.crawler.core.domain.config.page.Replacement;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.bean.Status;
import com.treefinance.crawler.framework.decode.DecodeUtils;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.filter.URLRegexFilter;
import com.treefinance.crawler.framework.process.search.URLHandler;
import com.datatrees.crawler.core.processor.search.SearchTemplateCombine;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.google.common.base.Preconditions;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.function.SpiderResponseFactory;
import com.treefinance.crawler.framework.context.pipeline.ProcessorInvokerAdapter;
import com.treefinance.crawler.framework.parser.HTMLParser;
import com.treefinance.crawler.framework.util.URLSplitter;
import com.treefinance.crawler.framework.util.UrlExtractor;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 24, 2014 6:35:43 PM
 */
public class PageImpl extends ProcessorInvokerAdapter {

    private static final String titleRegex     = PropertiesConfiguration.getInstance().get("page.title.regex", "<title>([^<]*)</title>");

    private static final int    URL_MAX_LENGTH = PropertiesConfiguration.getInstance().getInt("url.max.length", 1024);

    private final        Page   page;

    public PageImpl(@Nonnull Page page) {
        this.page = Objects.requireNonNull(page);
    }

    @Override
    public void process(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        LinkNode current = RequestUtil.getCurrentUrl(request);

        String content = RequestUtil.getContent(request);
        String searchTemplate = RequestUtil.getSearchTemplate(request);

        setPageNum(current);
        Map<String, LinkNode> pageUrlMap = findPageUrls(content, current, searchTemplate, request);// detect
        // page
        // linknode
        content = DecodeUtils.decodeContent(content, request);

        // check block pattern
        checkBlock(request, response);

        List<LinkNode> urlLinkNodes = new ArrayList<>();
        boolean needParse = needParser(ResponseUtil.getResponseStatus(response));
        // if block or no search result return
        if (needParse) {
            List<Replacement> replacements = page.getReplacementList();
            if (CollectionUtils.isNotEmpty(replacements)) {
                content = PageHelper.replaceText(content, replacements);
            }
            Regexp regexp = page.getRegexp();
            if (regexp != null) {
                content = PageHelper.getTextByRegexp(content, regexp);
            }
            request.setInput(content);

            if (StringUtils.isNotEmpty(content)) {
                // page title handler
                String pageTitle = getPageTitle(content);
                if (StringUtils.isNotBlank(pageTitle)) {
                    current.setPageTitle(pageTitle.trim());
                }

                // filter the invaild part of title
                if (StringUtils.isNotBlank(current.getPageTitle()) && StringUtils.isNotBlank(page.getPageTitleRegex())) {
                    current.setPageTitle(getFiltedPageTitle(current.getPageTitle(), page.getPageTitleRegex()));
                }

                // get segment
                List<AbstractSegment> segments = page.getSegmentList();

                Map<String, LinkNode> urlLists;
                if (CollectionUtils.isNotEmpty(segments)) {
                    // extract url with segment
                    urlLists = extractObjectsWithSegments(segments, request, response);
                } else {
                    // extract url
                    urlLists = extractUrlsWithOutSegments(content, request);
                    // EMPTY
                }
                urlLists.putAll(pageUrlMap);

                // add redirect url
                if (BooleanUtils.isTrue(page.getRedirectUrlAdd()) && StringUtils.isNotBlank(current.getRedirectUrl())) {
                    LinkNode redirectNode = new LinkNode(current.getRedirectUrl());
                    redirectNode.setReferer(current.getUrl());
                    redirectNode.setDepth(current.getDepth());
                    urlLists.put(redirectNode.getUrl(), redirectNode);
                    logger.info("add redirect url to urlLists, url: {}, referer: {}", current.getRedirectUrl(), current.getReferer());
                }

                // filter url set url depth
                urlLinkNodes = filterUrls(request, urlLists, current);
            } else {
                logger.warn("after page replace content is empty! LinkNode: {}", current);
            }

        } else {
            logger.warn("Need not Parse that no search result return or encounter block flag");
        }

        ResponseUtil.setResponseLinkNodes(response, urlLinkNodes);
    }

    private Map<String, LinkNode> findPageUrls(String content, LinkNode current, String searchTemplate, SpiderRequest request) {
        Map<String, LinkNode> urlLists = new LinkedHashMap<>();
        String charset = RequestUtil.getContentCharset(request);
        String keyword = RequestUtil.getKeyWord(request);
        try {
            String contentRegex = page.getContentPageRegex();
            if (StringUtils.isNotEmpty(contentRegex) && StringUtils.isNotEmpty(searchTemplate) && StringUtils.isNotEmpty(content)) {
                Matcher matcher = RegExp.getMatcher(contentRegex, content);
                while (matcher.find()) {
                    String pNumber = matcher.group(1);
                    try {
                        int pNum = Integer.parseInt(pNumber);
                        logger.info("add paging number: {},  match-text: {}", pNum, matcher.group(0));
                        String pageUrl = SearchTemplateCombine.constructSearchURL(searchTemplate, keyword, charset, pNum, false,
                                request.getProcessorContext().getContext());
                        if (StringUtils.isNotEmpty(pageUrl)) {
                            logger.info("add page url: {}", pageUrl);
                            LinkNode tmp = new LinkNode(pageUrl).setReferer(current.getUrl());
                            tmp.setpNum(pNum);
                            urlLists.put(pageUrl, tmp);
                        }

                    } catch (Exception e) {}
                }
            }
        } catch (Exception e) {
            logger.error("extract page urls error!", e);
        }
        return urlLists;
    }

    private boolean needParser(Integer responseStatus) {
        return responseStatus != Status.NO_SEARCH_RESULT;
    }

    private void setPageNum(LinkNode current) {
        try {
            String pageRegex = page.getPageNumRegex();
            if (StringUtils.isNotEmpty(pageRegex)) {
                String pidS = RegExp.group(current.getUrl(), pageRegex, 1);

                int pNum = -1;
                try {
                    pNum = Integer.parseInt(pidS);
                } catch (NumberFormatException nfe) {
                    // eat it if not configured correctly.
                }
                logger.info("find page number from url: {}, num: {}", current.getUrl(), pNum);
                current.setpNum(pNum);
            }
        } catch (Exception e) {
            logger.error("set page num error!", e);
        }
    }

    private void checkBlock(SpiderRequest request, SpiderResponse response) {
        String content = RequestUtil.getContent(request);

        SearchProcessorContext context = (SearchProcessorContext) request.getProcessorContext();

        String templateId = RequestUtil.getCurrentTemplateId(request);

        SearchTemplateConfig templateConfig = context.getSearchTemplateConfig(templateId);
        if (templateConfig != null) {
            com.datatrees.crawler.core.domain.config.search.Request reqBean = templateConfig.getRequest();
            if (reqBean != null) {
                String blockPattern = reqBean.getBlockPattern();
                String noResultPattern = reqBean.getNoResultPattern();
                String lastPagePattern = reqBean.getLastPagePattern();

                setResponseStatus(response, Status.LAST_PAGE, lastPagePattern, content);
                setResponseStatus(response, Status.NO_SEARCH_RESULT, noResultPattern, content);
                setResponseStatus(response, Status.BLOCKED, blockPattern, content);

            }
        }
    }

    private void setResponseStatus(SpiderResponse response, int status, String pattern, String content) {
        if (StringUtils.isNotEmpty(pattern)) {
            if (RegExp.find(content, pattern)) {
                logger.info("set status: {}", Status.format(status));
                ResponseUtil.setResponseStatus(response, status);
            }
        }
    }

    private String getPageTitle(String content) {
        String title = RegExp.group(content, titleRegex, Pattern.CASE_INSENSITIVE, 1);
        if (StringUtils.isNotEmpty(title) && title.length() > 2048) {
            title = title.substring(0, 2048);
        }
        return title;
    }

    private String getFiltedPageTitle(String title, String regex) {
        return RegExp.group(title, regex, Pattern.CASE_INSENSITIVE, 1);
    }

    /**
     * using white and black list to filter web url adjust depth
     */
    private List<LinkNode> filterUrls(SpiderRequest req, Map<String, LinkNode> urlLists, LinkNode current) {
        SearchProcessorContext wrapper = (SearchProcessorContext) req.getProcessorContext();
        SearchConfig config = wrapper.getSearchConfig();
        String template = RequestUtil.getCurrentTemplateId(req);

        URLHandler urlHandler = RequestUtil.getURLHandler(req);

        SearchTemplateConfig searchTemplateConfig = wrapper.getSearchTemplateConfig(RequestUtil.getCurrentTemplateId(req));
        String revisitPattern = null;
        if (searchTemplateConfig != null && searchTemplateConfig.getRequest() != null &&
                StringUtils.isNotBlank(searchTemplateConfig.getRequest().getReVisitPattern())) {
            revisitPattern = searchTemplateConfig.getRequest().getReVisitPattern();
        }
        List<LinkNode> nodes = new ArrayList<LinkNode>();
        List<UrlFilter> filters = addDefaultFilter(config.getUrlFilterList(), req);
        URLRegexFilter fl = new URLRegexFilter(filters);

        if (MapUtils.isNotEmpty(urlLists)) {
            for (Entry<String, LinkNode> entry : urlLists.entrySet()) {
                String originalURL = entry.getKey();
                LinkNode node = entry.getValue();
                Collection<String> urls = URLSplitter.split(originalURL);
                for (String url : urls) {
                    boolean removed = false;
                    LinkNode tmp;
                    if (urls.size() > 1) {
                        tmp = GsonUtils.fromJson(GsonUtils.toJson(node), LinkNode.class);
                    } else {
                        tmp = node;
                    }

                    tmp.setUrl(url);
                    // filll page title
                    tmp.setPageTitle(current.getPageTitle());
                    logger.debug("filter url : {}", url);
                    String dest = fl.filter(url);
                    // run url handler
                    if (urlHandler != null) {
                        try {
                            removed = urlHandler.handle(current, tmp);

                            // return true/false remove node
                        } catch (Exception e) {
                            logger.error("invoke url handler exception!", e);
                        }
                    }

                    if (!removed) {
                        if (tmp.isHosting() || StringUtils.isNotEmpty(dest)) {
                            String referer = StringUtils.isNotEmpty(current.getRedirectUrl()) ? current.getRedirectUrl() : current.getUrl();
                            if (StringUtils.isEmpty(tmp.getReferer())) {
                                tmp.setReferer(referer);
                            }
                            int depth = current.getDepth();
                            if (revisitPattern != null) {
                                try {
                                    depth = RegExp.find(url, revisitPattern) ? depth - 1 : depth;
                                } catch (Exception e) {
                                    logger.error("check revisit error!", e);
                                }
                            }

                            // adjust depth
                            wrapper.adjustUrlDepth(tmp, template, depth);
                            nodes.add(tmp);
                            logger.debug("{} @@accept url: {}", current.getUrl(), url);
                        } else {
                            logger.debug("{} @@filter url: {}", current.getUrl(), url);
                        }
                    }

                    /*
                     * if (StringUtils.isNotEmpty(dest) && !removed) { String referer =
                     * StringUtils.isNotEmpty(current.getRedirectUrl()) ? current.getRedirectUrl() :
                     * current.getUrl(); if (StringUtils.isEmpty(tmp.getReferer())) {
                     * tmp.setReferer(referer); } int depth = current.getDepth(); if (revisitPattern
                     * != null) { try { depth = PatternUtils.match(revisitPattern, url) ? depth - 1
                     * : depth; } catch (Exception e) { log.error("check revisit error!", e); } }
                     *
                     * // adjust depth wrapper.adjustUrlDepth(tmp, template, depth); nodes.add(tmp);
                     * log.debug(current.getUrl() + "@@accept url:" + url); } else {
                     * log.debug(current.getUrl() + "@@filter url:" + url); }
                     */
                }
            }
        }
        logger.debug("After url filter... original={} @@after={}", urlLists.size(), nodes.size());
        // nodes.add(current);
        return nodes;
    }

    private List<UrlFilter> addDefaultFilter(List<UrlFilter> filters, SpiderRequest req) {
        Configuration conf = req.getConfiguration();
        SearchProcessorContext wrapper = (SearchProcessorContext) req.getProcessorContext();
        String blackList = Constants.URL_BLACK_LIST;
        if (conf != null) {
            blackList = conf.get("url.blacklist", blackList);
        }
        List<UrlFilter> urlFilters = new ArrayList<>(filters);

        UrlFilter filter = new UrlFilter();
        filter.setFilter(blackList);
        filter.setType(FilterType.BLACK_LIST.getValue());
        urlFilters.add(filter);

        // add default domain white filter
        String domain = wrapper.getWebsite().getWebsiteDomain();
        if (StringUtils.isNotEmpty(domain)) {
            filter = new UrlFilter();
            filter.setFilter(domain.toLowerCase());
            filter.setType(FilterType.WHITE_LIST.getValue());
            urlFilters.add(filter);
        }

        return urlFilters;
    }

    private Map<String, LinkNode> extractUrlsWithOutSegments(String content, SpiderRequest request) {
        Map<String, LinkNode> linkNodeMap = new LinkedHashMap<>();

        LinkNode current = RequestUtil.getCurrentUrl(request);
        String baseURL = (StringUtils.isEmpty(current.getBaseUrl()) ? current.getBaseUrl() : current.getUrl());

        // parser url from html
        extractHtmlLinks(content, baseURL, current, linkNodeMap);

        extractTextUrls(current.getUrl(), content, linkNodeMap);

        return linkNodeMap;
    }

    private void extractHtmlLinks(String content, String baseURL, LinkNode current, Map<String, LinkNode> linkNodeMap) {
        // parser url from html
        HTMLParser htmlParser = new HTMLParser();
        htmlParser.parse(content, baseURL);
        for (Entry<String, String> fl : htmlParser.getLinks().entrySet()) {
            String key = fl.getKey();
            if (!linkNodeMap.containsKey(key)) {
                linkNodeMap.put(key, new LinkNode(fl.getKey()).setReferer(current.getUrl()));
                logger.debug(" extractor url >> {}", fl.getKey());
            }
        }
    }

    /**
     * get url list need too steps first extract field urls second extract page by regex finally
     * resolve url by base url
     */
    private Map<String, LinkNode> extractObjectsWithSegments(List<AbstractSegment> segments, SpiderRequest req, SpiderResponse resp) throws ResultEmptyException {
        Map<String, LinkNode> linkNodes = new LinkedHashMap<>();
        StringBuilder pageContent = new StringBuilder();

        LinkNode current = RequestUtil.getCurrentUrl(req);
        Preconditions.checkNotNull(current, "source url should not be null!");
        String baseURL = (StringUtils.isNotEmpty(current.getBaseUrl()) ? current.getBaseUrl() : current.getUrl());

        List<Map<String, Object>> segmentResult = new ArrayList<>();
        logger.info("URL: {}, segment-size: {}", baseURL, segments.size());
        for (AbstractSegment abstractSegment : segments) {
            try {
                SpiderResponse segResponse = SpiderResponseFactory.make();
                SegmentBase segmentBase = ProcessorFactory.getSegment(abstractSegment);

                segmentBase.invoke(req, segResponse);

                @SuppressWarnings("unchecked") List<String> contentSplit = ResponseUtil.getSegmentsContent(segResponse);

                @SuppressWarnings("unchecked") Object segResult = ResponseUtil.getSegmentsResults(segResponse);

                if (segResult instanceof Map) {
                    segmentResult.add((Map) segResult);
                } else if (segResult instanceof List) {
                    segmentResult.addAll((List) segResult);
                } else {
                    logger.warn("unaccepted segResult type: {}", segResult);
                }

                if (BooleanUtils.isTrue(page.getUrlExtract())) {
                    // append content split to detect url
                    for (String split : contentSplit) {
                        pageContent.append(split);
                    }
                }
            } catch (ResultEmptyException e) {
                throw e;
            } catch (Exception e) {
                logger.error("invoke segment processor error!", e);
            }
        }

        // combine field urls
        logger.debug("start to extract field url: {}", baseURL);

        List<Object> instanceList = new ArrayList<>();
        for (Object obj : segmentResult) {
            if (obj instanceof LinkNode) {
                linkNodes.put(((LinkNode) obj).getUrl(), (LinkNode) obj);
            } else {
                instanceList.add(obj);
            }
        }

        if (BooleanUtils.isTrue(page.getUrlExtract())) {
            String content = pageContent.toString();
            logger.debug("after segment page content: {}", content);

            logger.debug("start to extract html link urls: {}", baseURL);
            extractHtmlLinks(content, baseURL, current, linkNodes);
            // extract text url add to map
            logger.debug("start to extract text urls: {}", baseURL);
            extractTextUrls(current.getUrl(), content, linkNodes);
        }

        // return the page objects
        ResponseUtil.setResponseObjectList(resp, instanceList);
        // return all link-nodes in current page
        return linkNodes;
    }

    public Page getPage() {
        return page;
    }

    protected void extractTextUrls(String currentUrl, String content, Map<String, LinkNode> linkNodeMap) {
        List<String> textUrls = UrlExtractor.extract(content);
        for (String nextURL : textUrls) {
            try {
                nextURL = URLUtil.urlFormat(nextURL);
                if (nextURL == null || nextURL.length() > URL_MAX_LENGTH) {
                    continue;
                }
            } catch (Exception e) {
                logger.error("url format error...", e);
                continue;
            }

            LinkNode tmp = new LinkNode(nextURL).setReferer(currentUrl);
            if (StringUtils.isNotBlank(nextURL) && !linkNodeMap.containsKey(nextURL)) {
                linkNodeMap.put(nextURL, tmp);
                logger.debug("new url extracted in text extractor: {}", nextURL);
            } else {
                logger.debug("text extractor url exists: {}", nextURL);
            }
        }
    }

}
