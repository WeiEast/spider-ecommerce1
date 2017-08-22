/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.extractor;

import com.datatrees.crawler.core.processor.BaseConfigTest;
import org.junit.Test;

import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.util.xml.exception.ParseException;

/**
 * 
 * @version 1.0
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @since Mar 16, 2014 7:47:32 PM
 */
public class ExtractorCrawlerTest extends BaseConfigTest {

    // http:www.uploadable.ch/file/GsAU7mtN3qUn/qa5dj.The.Hunted.2003.part1.rar

    @Test
    public void testExtractor() throws ParseException {
        CrawlRequest request = CrawlRequest.build();
//        String conf = "parserTest.xml";
//        WebsiteConfigWrapper wrapper = getConfigWrapper(conf, "youku.com");
//        LinkNode hostUrl = new LinkNode("http://filehon.com/contents/view.php?idx=3305204");
//        ProxyManager pm = new SimpleProxyManager();
//        wrapper.setProxyManager(pm);
//        request.setWebsiteConfig(wrapper);
//        request.setUrl(hostUrl);
//        //Response response = ExtractorCrawler.crawl(request);
//
//        System.out.println(response.getAttribute(Constants.PAGE_EXTRACTOR_URL_KEYID));
//        System.out.println(response.getAttribute(Constants.PAGE_EXTRACTOR_URL_FILENAME));
//        System.out.println(response.getAttribute(Constants.PAGE_EXTRACTOR_URL_NORMALIZERURL));
//
//        List<Map<String, Object>> resultList = (List<Map<String, Object>>) response.getAttribute(Constants.SEGMENTS_RESULTS_LIST);
//        if (null == resultList) {
//            System.out.println("resultList is null");
//            return ;
//        }
//        for (Map<?, ?> one : resultList) {
//            System.out.println(one.toString());
//        }

    }
}
