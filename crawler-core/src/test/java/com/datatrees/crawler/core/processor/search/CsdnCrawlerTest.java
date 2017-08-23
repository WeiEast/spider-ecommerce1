/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.search;

import java.util.List;

import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.plugin.SimplePluginManager;
import com.datatrees.crawler.core.util.xml.exception.ParseException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 3:51:07 PM
 */
public class CsdnCrawlerTest extends BaseConfigTest {

    // @Ignore
    @Test
    public void testCrawler() throws ResultEmptyException {
        String conf = "csdnSearchTest.xml";
        try {
            SearchProcessorContext context = getProcessorContext(conf, "csdn.net");
            context.setPluginManager(new SimplePluginManager());
            //            context.setProxyManager(new SimpleProxyManager());
            context.setWebServiceUrl("http://localhost:8080");

            long start = System.currentTimeMillis();

            // 本用例没有使用登陆产生cookie，而是直接写入以下cookie，失效后请更换
            //            ProcessorContextUtil
            //                    .setCookieString(
            //                            context,
            //                            "CCSHOW=000001; ptui_loginuin=mail51bill@qq.com; pt2gguin=o3251057042; ptcz=8d3fe5f8f0e6da819dc0bd385889f034ff4176aa22742cb3be699f60e2a26296; edition=mail.qq.com; webp=0; wimrefreshrun=0&; qm_flag=0; qqmail_alias=mail51bill@qq.com; new_mail_num=593237554&441|-1043910254&0; qm_domain=http://mail.qq.com; foxacc=593237554&0|-1043910254&0; pt_clientip=93967f0000013407; pt_serverip=8ab30a93196f685b; p_uin=o3251057042; p_skey=RNzeP4-7fgXEdocdslhtaLBaSotq*cuIrwghzkEoiAM_; pt4_token=wkLPujB*gGcf7ujfRd3UCg__; qm_antisky=593237554&365441efa2264ad8de4f8e0b64943fbc7a1fb1beba2dcdd067d59cde7d52b34b|-1043910254&068233cf715f2cd5e8e0f2ce1d7590b6a748fad7ebca106035a4e8b50e06b8d0; qm_ptsk=593237554&@ZVFpLiAMz|-1043910254&@Ha1E3Fkwr; ptisp=ctc; uin=o3251057042; skey=@Ha1E3Fkwr; sid=-1043910254&772ee6053322f75a02da30ede2211998,qUk56ZVA0LTdmZ1hFZG9jZHNsaHRhTEJhU290cSpjdUlyd2doemtFb2lBTV8.; qm_username=3251057042; qm_sid=772ee6053322f75a02da30ede2211998,qUk56ZVA0LTdmZ1hFZG9jZHNsaHRhTEJhU290cSpjdUlyd2doemtFb2lBTV8.; ssl_edition=mail.qq.com; username=-1043910254&3251057042; pcache=956c60664fde397MTQzOTAyNTU1Mw@3251057042@0");
            //            String url =
            //                    "http://set3.mail.qq.com/cgi-bin/mail_list?sid=Sdsc4tMzdbIvpWQx&s=search&folderid=all&page=0&keyword=%D5%CB%B5%A5&sender=%D5%CB%B5%A5&receiver=%D5%CB%B5%A5&topmails=0&advancesearch=3&combinetype=or&loc=frame_html,,,7";
            //            

            ProcessorContextUtil.setCookieString(context, "__utma=17226283.1253581307.1436422380.1436450324.1436774335.3; __utmz=17226283.1436422380.1.1.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; CloudGuest=FiYNnAz/wP9KE53myN+j6gPU0ZABdlonUEO1ojDr3RaoTLuGSyZrrpU2IpKffl4mdyPUrv/+FZM/85B/RzJEj/bqw3zdJJe951YiRXlb3XzAyexl8XGDqJH320OYHjz9HbFfgJGb9aL/wxiguW/YMj/y8QxOam6uXIRVu+F8cHulIvr3c1Ykd7Ln+4XSJSdW; uuid_tt_dd=2294625119830033992_20150709; __gads=ID=3e752586937c7db4:T=1436422382:S=ALNI_MasGIzcg_whvBNXpr4Gq0UBKaL34A; __qca=P0-421444310-1436422384067; __message_sys_msg_id=0; __message_gu_msg_id=0; __message_cnel_msg_id=0; __message_district_code=000000; __message_in_school=0; UserName=wodwl; UserInfo=yNzEu7U%2FSCmLK4AudO7EPTwtwlgyhcEPo8vVpchtJ2%2FW2kMxLD0RbTEM2lclp5szP6bKrfW6jGmXtQIq0hhx5dZsimMChiGxO6a9QVzv3ahhZBd0ughiP0psHhaJ8qT0; UserNick=wodwl; AU=90B; UD=java-%E6%88%91%E6%84%BF%E6%84%8F%E7%94%A8%E6%88%91%E7%9A%84%E4%B8%80%E7%94%9F%E5%8E%BB%E6%8E%A2%E7%B4%A2%E4%BD%A0%E7%9A%84%E5%A5%A5%E7%A7%98; UN=wodwl; UE=\"wo_dwl@163.com\"; access-token=aee90039-0019-442f-8757-4623d978f0a1; __utmb=17226283.4.10.1436774335; __utmc=17226283; route=; dc_tos=nrf2rk; dc_session_id=1436775104376; __utmt=1");

            String url = "http://write.blog.csdn.net/postlist";
            // 临时这么书写，之后这个值会有xml配置扣出，放入上下文
            //  context.getContext().put("sid", PatternUtils.group(url, "sid=([^&]*)", 1));

            CrawlRequest request = CrawlRequest.build().setProcessorContext(context).setUrl(new LinkNode(url)).setSearchTemplateId("keyword-search-template").contextInit();

            CrawlResponse resp = Crawler.crawl(request);
            List<LinkNode> urls = ResponseUtil.getResponseLinkNodes(resp);

            for (LinkNode linkNode : urls) {
                System.out.println(linkNode + "  propertys " + linkNode.getPropertys());

            }

            //            for (LinkNode linkNode : urls) {
            //                System.out.println(linkNode + "  propertys " + linkNode.getPropertys());
            //                System.out.println("Crawler url " + linkNode.getUrl());
            //                CrawlRequest subRequest = CrawlRequest.build().setProcessorContext(context).setUrl(linkNode)
            //                    .setSearchTemplateId("keyword-search-template").contextInit();
            //                CrawlResponse subResp = Crawler.crawl(subRequest);
            //                List<Object> objs = ResponseUtil.getResponseObjectList(subResp);
            //                System.out.println("------------"+objs.size());
            //                for (Object obj : objs) {
            //                    System.out.println("  obj " + obj);
            //                }
            //            }
            System.out.println(resp.info());
            System.out.println(System.currentTimeMillis() - start);
        } catch (ParseException e) {
            Assert.fail("not well format config!");
        }
    }

}
