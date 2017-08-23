/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.search;

import java.util.List;
import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.domain.config.search.SearchType;
import com.datatrees.crawler.core.login.SimpleLoginResource;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.login.Login;
import com.datatrees.crawler.core.processor.plugin.SimplePluginManager;
import com.datatrees.crawler.core.util.xml.exception.ParseException;
import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 3:51:07 PM
 */
public class CrawlerTest extends BaseConfigTest {

    @Test
    public void testParser() throws Exception {
        String conf = "ParserTestconfig.xml";
        try {
            SearchProcessorContext context = getProcessorContext(conf, "baidu");
            context.setPluginManager(new SimplePluginManager());
            //            context.setProxyManager(new SimpleProxyManager());
            context.setWebServiceUrl("http://localhost:8080");
            context.setLoginResource(new SimpleLoginResource());
            context.init();
            long start = System.currentTimeMillis();

            if (context.needLogin()) {
                Login.INSTANCE.doLogin(context);
            }
            for (SearchTemplateConfig SearchTemplateConfig : context.getSearchTempldateConfigList(SearchType.CATEGORY_SEARCH)) {
                String headerString = SearchTemplateConfig.getRequest().getDefaultHeader();
                Map<String, String> defaultHeader = (Map<String, String>) GsonUtils.fromJson(headerString, Map.class);
                if (MapUtils.isNotEmpty(defaultHeader)) {
                    context.getDefaultHeader().putAll(defaultHeader);
                }

                for (String template : SearchTemplateConfig.getRequest().getSearchTemplateList()) {
                    String url = SearchTemplateCombine.constructSearchURL(template, "账单", context.getSearchConfig().getProperties().getEncoding(), 0, true, context.getContext());
                    url = ReplaceUtils.replaceMap(context.getContext(), url);

                    CrawlRequest request = CrawlRequest.build().setProcessorContext(context).setUrl(new LinkNode(url)).setSearchTemplateId("keyword-search-template").setSearchTemplate(template).contextInit();

                    CrawlResponse resp = Crawler.crawl(request);
                    List<LinkNode> urls = ResponseUtil.getResponseLinkNodes(resp);
                    // 把url 放入队列。。。

                    for (LinkNode linkNode : urls) {
                        System.out.println(linkNode + "  propertys " + linkNode.getPropertys() + " ,headers " + linkNode.getHeaders());
                        System.out.println("Crawler url " + linkNode.getUrl());
                    }
                    System.out.println(resp.info());
                    System.out.println(System.currentTimeMillis() - start);
                }
            }

        } catch (ParseException e) {
            Assert.fail("not well format config!");
        }
    }

    @Test
    public void testCrawler() throws Exception {
        String conf = "qqSearchTest.xml";
        try {
            SearchProcessorContext context = getProcessorContext(conf, "qq");
            context.setPluginManager(new SimplePluginManager());
            //            context.setProxyManager(new SimpleProxyManager());
            context.setWebServiceUrl("http://localhost:8080");
            context.setLoginResource(new SimpleLoginResource());

            long start = System.currentTimeMillis();

            if (context.needLogin()) {
                Login.INSTANCE.doLogin(context);
            }
            for (SearchTemplateConfig SearchTemplateConfig : context.getSearchTempldateConfigList(SearchType.KEYWORD_SEARCH)) {
                String headerString = SearchTemplateConfig.getRequest().getDefaultHeader();
                Map<String, String> defaultHeader = (Map<String, String>) GsonUtils.fromJson(headerString, Map.class);
                if (MapUtils.isNotEmpty(defaultHeader)) {
                    context.getDefaultHeader().putAll(defaultHeader);
                }

                for (String template : SearchTemplateConfig.getRequest().getSearchTemplateList()) {
                    String url = SearchTemplateCombine.constructSearchURL(template, "账单", context.getSearchConfig().getProperties().getEncoding(), 0, true, context.getContext());
                    url = ReplaceUtils.replaceMap(context.getContext(), url);

                    CrawlRequest request = CrawlRequest.build().setProcessorContext(context).setUrl(new LinkNode(url)).setSearchTemplateId("keyword-search-template").setSearchTemplate(template).contextInit();

                    CrawlResponse resp = Crawler.crawl(request);
                    List<LinkNode> urls = ResponseUtil.getResponseLinkNodes(resp);
                    // 把url 放入队列。。。

                    for (LinkNode linkNode : urls) {
                        System.out.println(linkNode + "  propertys " + linkNode.getPropertys());
                        System.out.println("Crawler url " + linkNode.getUrl());
                        CrawlRequest subRequest = CrawlRequest.build().setProcessorContext(context).setUrl(linkNode).setSearchTemplateId("keyword-search-template").contextInit();
                        CrawlResponse subResp = Crawler.crawl(subRequest);

                        // check bloack
                        if (Status.BLOCKED == (int) ResponseUtil.getResponseStatus(subResp)) {
                            System.out.println("Block !!! Exit...");
                            System.out.println(ResponseUtil.getProtocolResponse(subResp).getContent().getContentAsString());
                            return;
                        } else if (Status.NO_SEARCH_RESULT == (int) ResponseUtil.getResponseStatus(subResp)) {
                            System.out.println("no result ...");
                        }

                        List<Object> objs = ResponseUtil.getResponseObjectList(subResp);

                        // 保存 做处理 。。。
                        for (Object obj : objs) {
                            System.out.println("  obj " + obj);
                        }

                        // 从queue pop
                    }
                    System.out.println(resp.info());
                    System.out.println(System.currentTimeMillis() - start);
                }
            }

        } catch (ParseException e) {
            Assert.fail("not well format config!");
        }
    }

    @Ignore
    @Test
    public void testCrawlResponse() {

    }
}
