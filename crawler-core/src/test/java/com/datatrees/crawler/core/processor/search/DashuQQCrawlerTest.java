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
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
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
public class DashuQQCrawlerTest extends BaseConfigTest {

    @Test
    public void testParser() throws Exception {
        String conf = "qq/dashuQQSearchTest.xml";
        try {
            SearchProcessorContext context = getProcessorContext(conf, "qq.com");
            context.setPluginManager(new SimplePluginManager());
            //            context.setProxyManager(new SimpleProxyManager());
            context.setWebServiceUrl("http://localhost:8080");
            context.setLoginResource(new SimpleLoginResource());
            ProcessorContextUtil.setCookieString(context, "luin=o3316398614; lskey=000100000765e4269409e0d830d9f00373ad63ad00305d2d9fdaff52c9229d247465d24f275037fbde049d32; p_luin=o3316398614; p_lskey=00040000193c9369d978d17829dbf4c8a6a2f333d3013e00e20f0118cfecc681dae07f82d9cb117cbbefea61; pgv_info=ssid=s1378104380; pgv_pvid=7709114380; o_cookie=3316398614; ptcz=875a672a3199df083157b1b71141cb0ec0151b082ecb839aee7871d87ebb1160; pt2gguin=o0593237554; uin=o0593237554; skey=@iPfwrIO01; wimrefreshrun=0&; qm_flag=0; qqmail_alias=593237554@qq.com; sid=593237554&71336e42a55e5d9c6b962bdbe7abc4cc,cgwms_oO3wrk.; qm_username=593237554; qm_domain=https://mail.qq.com; qm_ptsk=593237554&@iPfwrIO01; foxacc=593237554&0; ssl_edition=sail.qq.com; edition=mail.qq.com; username=593237554&593237554; CCSHOW=000001; new_mail_num=593237554&591; webp=1; ptisp=ctc; qm_sk=593237554&W8jT5vNw; qm_ssum=593237554&a4d9668c2b031c6fe245e091c067e404; qm_sid=71336e42a55e5d9c6b962bdbe7abc4cc,cgwms_oO3wrk.; device=");

            context.getContext().put("endurl", "https://w.mail.qq.com/cgi-bin/mobile?sid=NgRYAxWyF0utGjlv,4&t=phone");
            long start = System.currentTimeMillis();

            context.init();

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

    @Ignore
    @Test
    public void testBasicVersion() throws Exception {
        String conf = "qq/basicVersion.xml";

        try {
            SearchProcessorContext context = getProcessorContext(conf, "qq.com");
            context.setPluginManager(new SimplePluginManager());
            //            context.setProxyManager(new SimpleProxyManager());
            context.setWebServiceUrl("http://localhost:8080");
            context.setLoginResource(new SimpleLoginResource());
            ProcessorContextUtil.setCookieString(context, "pt2gguin=o0476691856; luin=o0476691856; lskey=00010000981b5e6629718434d73e1bf53c516e6347fcb60d53d4c4234104afe323e7e050c8d8aede280af066; qqmail_alias=476691856@qq.com; edition=mail.qq.com; CCSHOW=000001; webp=0; ptui_loginuin=476691856; ptcz=cdd1706f2b42b5467d6df2c6459dca5cc11a43e6ae03ba1880fb82f28e0619af; p_luin=o0476691856; p_lskey=000400009a3536fee94253fb89f4d4fcaeeb1669c65f6c97b4ab82652dc98479d201709166f3f05805c9fdfa; mcookie=0&y; qm_flag=0; new_mail_num=476691856&50; wimrefreshrun=0&; qm_antisky=476691856&a38f41fd82e71479987b8b568b0464e239d690dd548f8fe43836058b2c63274b; qm_domain=https://mail.qq.com; qm_ptsk=476691856&@VW5IjJbEf; qm_ptlsk=476691856&000100004150547102d5c78bd178827dc71c57a731d9bd0de0d13735ddb440a180858ee9e2f06b3f71c974a5; foxacc=476691856&0; msid=nw92UO0FP1YULzNlXcLT5vNw,4,qKklGSGxqZXFuM0VGSVJzUzU1NEZ0WGp0MHpzLWoyZlVYYmNIbkV2c2o1NF8.; p_uin=o0476691856; p_skey=*IFHljeqn3EFIRsS554FtXjt0zs-j2fUXbcHnEvsj54_; pt4_token=0Q--a5U7dG*omqlz2CRsTA__; sid=476691856&d3fbb6e18b331ae368c70692e45038c8,qKklGSGxqZXFuM0VGSVJzUzU1NEZ0WGp0MHpzLWoyZlVYYmNIbkV2c2o1NF8.; ssl_edition=sail.qq.com; username=476691856&476691856; pt_clientip=10c47f000001ab10; pt_serverip=e2dd0abf06644853; uin=o0476691856; qm_username=476691856; qm_sid=d3fbb6e18b331ae368c70692e45038c8,qKklGSGxqZXFuM0VGSVJzUzU1NEZ0WGp0MHpzLWoyZlVYYmNIbkV2c2o1NF8.; ptisp=ctc; skey=@G5svxnmvp; pcache=341875b78b795e2MTQ0OTgxNzQ2NQ@476691856@4; device=; qm_sk=476691856&UcTT5vNw; qm_ssum=476691856&5fab69c2081fe77106a62757fa5f0cfd");

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
                    String url = SearchTemplateCombine.constructSearchURL(template, "广告", context.getSearchConfig().getProperties().getEncoding(), 0, true, context.getContext());
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

    @Ignore
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
