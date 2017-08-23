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
 * @author <A HREF="mailto:yangzhiyong@datatrees.com.cn">Yang Zhiyong</A>
 * @version 1.0
 * @since Mar 10, 2014 3:51:07 PM
 */
public class AliPayCrawlerTest extends BaseConfigTest {

    private static final String beginTime = "00%3A00";   //00:00
    private static final String endTime   = "24%3A00";   //24:00
    private static final String beginDate = "2015.05.14";
    private static final String endDate   = "2015.07.14";
    private static final String cookie    = "ecss_identity=63924060560229337729; LATN_CODE_COOKIE=0760; TS9d76e8=d1f75b20b837482366fdaab4c88612b643c8cb743e5792ca55c813df; i_url=%5B%5BB%5D%5D; JSESSIONID=GG22VLXB1232G5NCV342RYhBLVLHKSLmhBkY2hVxkHTHGTYzh7xc!1197000092; svid=42B52A29499BAC47; i_sess=%20in_mpid%3Dkhzy-zcdh-fycx-wdxf-zdcx%3B; ijg=1439175878187; i_vnum=6; i_ppv=49; i_cc=true; APPCOOKIES=%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxf_ye.html%253Fin_cmpid%253Dkhzy-zcdh-fycx-wdxf-yecx%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxf_ye.html%253Fin_cmpid%253Dkhzy-zcdh-fycx-wdxf-yecx%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxf_ye.html%253Fin_cmpid%253Dkhzy-zcdh-fycx-wdxf-yecx%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxf_ye.html%253Fin_cmpid%253Dkhzy-zcdh-fycx-wdxf-yecx%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxf_ye.html%253Fin_cmpid%253Dkhzy-zcdh-fycx-wdxf-yecx%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxf_ye.html%253Fin_cmpid%253Dkhzy-zcdh-fycx-wdxf-yecx%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxf_ye.html%253Fin_cmpid%253Dkhzy-zcdh-fycx-wdxf-yecx%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxf_zd.html%253Fin_cmpid%253Dkhzy-zcdh-fycx-wdxf-zdcx%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxd_index.html%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxd_index.html%3BloginOldUri%3D%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxd_index.html%3BloginOldUri%3D%252Fservice%252Fhome%252Fquery%252Fxd_index.html; create_time=Fri Aug 07 2015 14:32:20 GMT+0800 (CST); s_sess=%20in_mpid%3Dkhzy-zcdh-fycx-wdxf-zdcx%3B%20s_cc%3Dtrue%3B%20s_sq%3Deship-gdt-prd%253D%252526pid%25253Dgd.189.cn%2525252Fservice%2525252Fhome%2525252Fquery%2525252Fxd_index.html%252526pidt%25253D1%252526oid%25253Djavascript%2525253Avoid%252525280%25252529%2525253B_8%252526oidt%25253D1%252526ot%25253DA%252526oi%25253D1%3B%20IsLogin%3Dyes%3B; s_pers=%20s_fid%3D42498F9EC2FB6B79-0C210D25EB021DB8%7C1502334278182%3B; BIGipServerTongYiSouSuo=3077482688.39455.0000; i_sq=eship-gdt-prd-new%3D%2526pid%253Dgd.189.cn%25252Fservice%25252Fhome%25252Fquery%25252Fxd_index.html%2526pidt%253D1%2526oid%253Dfunctionanonymous%252528e%252529%25257Bs_objectID%25253D%252527javascript%25253Avoid%2525280%252529%25253B_8%252527%25253Breturnthis.s_oc%25253Fthis.s_oc%252528e%252529%25253Atrue%25257D%2526oidt%253D2%2526ot%253DA; userKey=17708259794#ECSS#9014391764810775490; wt_sessionid=9014391764810775490; wt_userid=2760380577580000; wt_usertype=02; wt_acc_nbr=17708259794; wt_serv_type=CDMA; gdLogin=yes; gdUserInfo=custId%3D6513430967%3BLATN_CODE_COOKIE%3D0760%3BlatnCode%3D0760%3Bwt_userid%3D2760380577580000%3BuserId%3D17708259794%3Bwt_usertype%3D02%3BservId%3D6919680729%3BuserIdOld%3D17708259794%3Bwt_acc_nbr%3D17708259794%3Bwt_serv_type%3DCDMA%3BloginType%3Dpassword%3BuserType%3DCDMA%3B; referrer=%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3Dh7wEBw7bEboBZNoydis4IkKInXESMPmCtP8szqpl8mm%26wd%3D%26eqid%3Ddedab69400127ed00000000355c76b97; _visit_custid=1439132580055-429718; ijg_s=Less%20than%201%20day; i_invisit=1; i_PV=gd.189.cn%2Fservice%2Fhome%2Fquery%2Fxd_index.html";

    // @Ignore
    @Test
    public void testCrawler() throws InterruptedException, ResultEmptyException {
        String conf = "gd189SearchConfig.xml";
        try {
            SearchProcessorContext context = getProcessorContext(conf, "189");
            context.setPluginManager(new SimplePluginManager());
            //            context.setProxyManager(new SimpleProxyManager());
            context.setWebServiceUrl("http://localhost:8080");

            long start = System.currentTimeMillis();

            ProcessorContextUtil.setCookieString(context, cookie);

            //String url = "http://gd.189.cn/query/json/realTimeFee.action?queryType=0&a.c=0&a.u=user&a.p=pass&a.s=ECSS";
            String url = "http://gd.189.cn/J/J10053.j\"a.c=0&a.u=user&a.p=pass&a.s=ECSS&c.n=账单查询&c.t=02&c.i=02-004&d.d01=201507&d.d02=1&d.d03=&d.d04=";
            LinkNode ln = new LinkNode(url);
            String referUrl = "http://gd.189.cn/service/home/query/xf_zd.html?in_cmpid=khzy-zcdh-fycx-wdxf-zdcx";
            ln.setReferer(referUrl);
            CrawlRequest request = CrawlRequest.build().setProcessorContext(context).setUrl(ln).setSearchTemplateId("zhangdan-search-template").contextInit();

            //craw first page
            CrawlResponse resp = Crawler.crawl(request);
            List<LinkNode> urls = ResponseUtil.getResponseLinkNodes(resp);
            List<Object> list = ResponseUtil.getResponseObjectList(resp);

            System.out.println();
            //            //craw page 2...
            //            List<String> repeadPageNumber = new LinkedList<String>();
            //            while (urls != null && urls.size() > 0) {
            //                String urlStr = urls.get(0).getUrl();
            //                if (urlStr.indexOf("pageNum") != -1) {
            //                    String pageNum = urlStr.substring(urlStr.indexOf("pageNum") + "pageNum".length() + 1);
            //                    if (repeadPageNumber.contains(pageNum)) {
            //                        request = CrawlRequest.build().setProcessorContext(context).setUrl(urls.get(0))
            //                            .setSearchTemplateId("keyword-search-template").contextInit();
            //                        resp = Crawler.crawl(request);
            //                        urls = ResponseUtil.getResponseLinkNodes(resp);
            //                        continue;
            //                    }
            //                    repeadPageNumber.add(pageNum);
            //                    System.out.println("Handle page number: " + pageNum);
            //                }
            //
            //                System.out.println("URL size:" + urls.size() + "," + urls.get(0));
            //
            //                request = CrawlRequest.build().setProcessorContext(context).setUrl(urls.get(0))
            //                    .setSearchTemplateId("keyword-search-template").contextInit();
            //                resp = Crawler.crawl(request);
            //                list.addAll(ResponseUtil.getResponseObjectList(resp));
            //                urls = ResponseUtil.getResponseLinkNodes(resp);
            //
            //                Thread.sleep(100);
            //            }
            //
            //            for (Object object : list) {
            //                AliPayObj aliPayObj = (AliPayObj) object;
            //                System.out.println(aliPayObj);
            //            }
            //
            //            System.out.println("Total:" + list.size());
            //            System.out.println(System.currentTimeMillis() - start);

        } catch (ParseException e) {
            Assert.fail("not well format config!");
        }
    }

}
