/**
 * www.gf-dai.com.cn
 * Copyright (c) 2015 All Rights Reserved.
 */
package com.datatrees.crawler.core.processor.operation.impl;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.protocol.ProtocolStatusCodes;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.plugin.SimplePluginManager;
import com.datatrees.crawler.core.processor.search.Crawler;
import com.datatrees.crawler.core.util.xml.exception.ParseException;

/**
 * 
 * @author likun
 * @version $Id: DateTimeOperationTest.java, v 0.1 Jul 23, 2015 10:25:17 AM likun Exp $
 */
public class DateTimeOperationTest extends BaseConfigTest {
    private final static Logger logger = LoggerFactory.getLogger(DateTimeOperationTest.class);

    @Test
    public void test() throws ResultEmptyException {
        String conf = "datetimeoperation-test.xml";

        try {
            SearchProcessorContext context = getProcessorContext(conf, "baidu.com");
            context.setPluginManager(new SimplePluginManager());
            context.init();

            /*            *//**
                              * 本用例没有使用登陆产生cookie，而是直接写入以下cookie，失效后请更换
                              */
            /*
            ProcessorContextUtil
             .setCookieString(
                 context,
                 "BIGipServerPOOL_WSYYT2_4GqiantaiAPP=755700746.17695.0000; e3=zwwfVwPPCGMTwNq1d8VwyVtqDH21VTKn42lkqJH3QTJRbJBFs1dM!2141628305!-148112031;");
            */
            /**
             * personal information search
             */
            long start = System.currentTimeMillis();
            CrawlRequest request = CrawlRequest.build().setProcessorContext(context)
                .setUrl(new LinkNode("https://www.baidu.com/more"))
                .setSearchTemplateId("datetimeoperation-sample-search-template").contextInit();
            CrawlResponse resp = Crawler.crawl(request);
            logger.info("datetime operation sample search response status - " + ResponseUtil.getResponseStatus(resp));
            Assert.assertTrue((ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS || ResponseUtil
                .getResponseStatus(resp) == Status.NO_SEARCH_RESULT));
            logger.info("datetime operation sample search ResponseObjectList - "
                        + ResponseUtil.getResponseObjectList(resp));
            if (ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS) {
                Assert.assertNotNull(ResponseUtil.getResponseObjectList(resp));
            }
            logger
                .info("datetime operation sample search search took - " + (System.currentTimeMillis() - start) + "ms");

        } catch (ParseException e) {
            Assert.fail("not well format config!");
        }
    }
}
