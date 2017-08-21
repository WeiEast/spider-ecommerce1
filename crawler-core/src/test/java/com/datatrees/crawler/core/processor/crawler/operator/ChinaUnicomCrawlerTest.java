/**
 * www.gf-dai.com.cn
 * Copyright (c) 2015 All Rights Reserved.
 */
package com.datatrees.crawler.core.processor.crawler.operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.ProtocolStatusCodes;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.ExtractorRepuest;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.extractor.Extractor;
import com.datatrees.crawler.core.processor.plugin.SimplePluginManager;
import com.datatrees.crawler.core.processor.search.Crawler;
import com.datatrees.crawler.core.util.xml.exception.ParseException;

/**
 * 
 * @author likun
 * @version $Id: TelUnicomCrawlerTest.java, v 0.1 Jul 13, 2015 2:10:09 PM likun Exp $
 */
public class ChinaUnicomCrawlerTest extends BaseConfigTest {

    private final static Logger logger = LoggerFactory.getLogger(ChinaUnicomCrawlerTest.class);

    @Test
    public void testLinkNodesDiscoverSearchTemplatesCrawlerAndExtract() throws ResultEmptyException {
        String conf = "operator/chinaunicom-search.xml";
        try {
            SearchProcessorContext context = getProcessorContext("operator/chinaunicom-search.xml", "10010.com");
            //context.setPluginManager(new SimplePluginManager());
            //context.setProxyManager(new SimpleProxyManager());

            ExtractorProcessorContext extractorContext = getExtractorProcessorContext(
                "operator/chinaunicom-extractor.xml", "10010.com");

            /**
             * 本用例没有使用登陆产生cookie，而是直接写入以下cookie，失效后请更换
             */
            ProcessorContextUtil
                .setCookieString(
                    context,
                    "BIGipServerPOOL_WSYYT2_4GqiantaiAPP=789255178.16671.0000; e3=JLmjVwNWr2w0G5TCThN8qfNBvHyyc4yl4TvBkvwQxhQHKCg1vfsH!446242911!807301151;");

            /**
             * calldetails linknodes discover search
             */

            long start = System.currentTimeMillis();
            CrawlRequest request = CrawlRequest.build().setProcessorContext(context)
                .setUrl(new LinkNode("http://iservice.10010.com/e3/query/call_dan.html"))
                .setSearchTemplateId("calldetails-linknodes-discover-search-template").contextInit();
            CrawlResponse resp = Crawler.crawl(request);
            logger.info("calldetails linknodes discover search response status - "
                        + ResponseUtil.getResponseStatus(resp));
            Assert.assertTrue((ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS || ResponseUtil
                .getResponseStatus(resp) == Status.NO_SEARCH_RESULT));
            logger
                .info("calldetails linknodes discover search link nodes - " + ResponseUtil.getResponseLinkNodes(resp));
            logger.info("calldetails linknodes discover search took - " + (System.currentTimeMillis() - start) + "ms");

            if (CollectionUtils.isNotEmpty(ResponseUtil.getResponseLinkNodes(resp))) {
                for (LinkNode linkNode : ResponseUtil.getResponseLinkNodes(resp)) {
                    logger.info(linkNode + "  propertys " + linkNode.getPropertys());
                    logger.info("Crawler url " + linkNode.getUrl());
                    CrawlRequest subRequest = CrawlRequest.build().setProcessorContext(context).setUrl(linkNode)
                        .setSearchTemplateId("calldetails-search-template").contextInit();
                    CrawlResponse subResp = Crawler.crawl(subRequest);

                    // check bloack
                    if (Status.BLOCKED == (int) ResponseUtil.getResponseStatus(subResp)) {
                        logger.error("Block !!! Exit...");
                        logger.error(ResponseUtil.getProtocolResponse(subResp).getContent().getContentAsString());
                        return;
                    } else if (Status.NO_SEARCH_RESULT == (int) ResponseUtil.getResponseStatus(subResp)) {
                        logger.warn("no result ...");
                    } else {
                        List<Object> objs = ResponseUtil.getResponseObjectList(subResp);
                        if (CollectionUtils.isEmpty(objs)) {
                            logger.warn("response - "
                                        + ResponseUtil.getProtocolResponse(subResp).getContent().getContentAsString());
                        } else {
                            // 保存 做处理 。。。
                            for (Object obj : objs) {
                                logger.info("calldetails search response obj " + obj);
                            }

                            /**
                              * extract data
                              */

                            ExtractorRepuest extractorRequest = ExtractorRepuest.build().setProcessorContext(
                                extractorContext);
                            extractorRequest.setInput((Map) objs.get(0));
                            Response extractorResponse = Extractor.extract(extractorRequest);

                            List<Object> extractorResultObjectList = ResponseUtil
                                .getResponseObjectList(extractorResponse);
                            logger.info("calldetails extractor response resultObjectList - {}",
                                extractorResultObjectList);
                        }
                    }
                }
            }

            /**
             * smsdetails linknodes discover search
             */
            start = System.currentTimeMillis();
            request = CrawlRequest.build().setProcessorContext(context)
                .setUrl(new LinkNode("http://iservice.10010.com/e3/query/call_sms.html"))
                .setSearchTemplateId("smsdetails-linknodes-discover-search-template").contextInit();
            resp = Crawler.crawl(request);
            logger.info("smsdetails linknodes discover search response status - "
                        + ResponseUtil.getResponseStatus(resp));
            Assert.assertTrue((ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS || ResponseUtil
                .getResponseStatus(resp) == Status.NO_SEARCH_RESULT));
            logger.info("smsdetails linknodes discover search link nodes - " + ResponseUtil.getResponseLinkNodes(resp));
            logger.info("smsdetails linknodes discover search took - " + (System.currentTimeMillis() - start) + "ms");

            if (CollectionUtils.isNotEmpty(ResponseUtil.getResponseLinkNodes(resp))) {
                for (LinkNode linkNode : ResponseUtil.getResponseLinkNodes(resp)) {
                    logger.info(linkNode + "  propertys " + linkNode.getPropertys());
                    logger.info("Crawler url " + linkNode.getUrl());
                    CrawlRequest subRequest = CrawlRequest.build().setProcessorContext(context).setUrl(linkNode)
                        .setSearchTemplateId("smsdetails-search-template").contextInit();
                    CrawlResponse subResp = Crawler.crawl(subRequest);

                    // check bloack
                    if (Status.BLOCKED == (int) ResponseUtil.getResponseStatus(subResp)) {
                        logger.error("Block !!! Exit...");
                        logger.error(ResponseUtil.getProtocolResponse(subResp).getContent().getContentAsString());
                        return;
                    } else if (Status.NO_SEARCH_RESULT == (int) ResponseUtil.getResponseStatus(subResp)) {
                        logger.warn("no result ...");
                    } else {
                        List<Object> objs = ResponseUtil.getResponseObjectList(subResp);
                        if (CollectionUtils.isEmpty(objs)) {
                            logger.warn("response - "
                                        + ResponseUtil.getProtocolResponse(subResp).getContent().getContentAsString());
                        } else {
                            // 保存 做处理 。。。
                            for (Object obj : objs) {
                                logger.info("  obj " + obj);
                            }

                            /**
                             * extract data
                             */
                            ExtractorRepuest extractorRequest = ExtractorRepuest.build().setProcessorContext(
                                extractorContext);
                            extractorRequest.setInput((Map) objs.get(0));
                            Response extractorResponse = Extractor.extract(extractorRequest);

                            List<Object> extractorResultObjectList = ResponseUtil
                                .getResponseObjectList(extractorResponse);
                            logger.info("smsdetails extractor response resultObjectList - {}",
                                extractorResultObjectList);
                        }
                    }
                }
            }

            /**
             * billdetails linknodes discover search
             */
            start = System.currentTimeMillis();
            request = CrawlRequest.build().setProcessorContext(context)
                .setUrl(new LinkNode("http://iservice.10010.com/e3/query/history_list.html"))
                .setSearchTemplateId("billdetails-linknodes-discover-search-template").contextInit();
            resp = Crawler.crawl(request);
            logger.info("billdetails linknodes discover search response status - "
                        + ResponseUtil.getResponseStatus(resp));
            Assert.assertTrue((ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS || ResponseUtil
                .getResponseStatus(resp) == Status.NO_SEARCH_RESULT));
            logger
                .info("billdetails linknodes discover search link nodes - " + ResponseUtil.getResponseLinkNodes(resp));
            logger.info("billdetails linknodes discover search took - " + (System.currentTimeMillis() - start) + "ms");

            if (CollectionUtils.isNotEmpty(ResponseUtil.getResponseLinkNodes(resp))) {
                for (LinkNode linkNode : ResponseUtil.getResponseLinkNodes(resp)) {
                    logger.info(linkNode + "  propertys " + linkNode.getPropertys());
                    logger.info("Crawler url " + linkNode.getUrl());
                    CrawlRequest subRequest = CrawlRequest.build().setProcessorContext(context).setUrl(linkNode)
                        .setSearchTemplateId("billdetail-search-template").contextInit();
                    CrawlResponse subResp = Crawler.crawl(subRequest);

                    // check bloack
                    if (Status.BLOCKED == (int) ResponseUtil.getResponseStatus(subResp)) {
                        logger.error("Block !!! Exit...");
                        logger.error(ResponseUtil.getProtocolResponse(subResp).getContent().getContentAsString());
                        return;
                    } else if (Status.NO_SEARCH_RESULT == (int) ResponseUtil.getResponseStatus(subResp)) {
                        logger.warn("no result ...");
                    } else {
                        List<Object> objs = ResponseUtil.getResponseObjectList(subResp);
                        if (CollectionUtils.isEmpty(objs)) {
                            logger.warn("response - "
                                        + ResponseUtil.getProtocolResponse(subResp).getContent().getContentAsString());
                        } else {
                            // 保存 做处理 。。。
                            for (Object obj : objs) {
                                logger.info("  obj " + obj);
                            }

                            /**
                             * extract data
                             */
                            ExtractorRepuest extractorRequest = ExtractorRepuest.build().setProcessorContext(
                                extractorContext);
                            extractorRequest.setInput((Map) objs.get(0));
                            Response extractorResponse = Extractor.extract(extractorRequest);

                            List<Object> extractorResultObjectList = ResponseUtil
                                .getResponseObjectList(extractorResponse);
                            logger.info("billdetails extractor response resultObjectList - {}",
                                extractorResultObjectList);
                        }
                    }
                }
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            Assert.fail("not well format config!");
        }
    }

    @Ignore
    @Test
    public void testSeparateSearchTemplates() throws ResultEmptyException {
        String conf = "operator/chinaunicom-search.xml";
        try {
            SearchProcessorContext context = getProcessorContext(conf, "10010.com");
            context.setPluginManager(new SimplePluginManager());

            /**
             * 本用例没有使用登陆产生cookie，而是直接写入以下cookie，失效后请更换
             */
            ProcessorContextUtil
                .setCookieString(
                    context,
                    "BIGipServerPOOL_WSYYT2_4GqiantaiAPP=755700746.17695.0000; e3=zwwfVwPPCGMTwNq1d8VwyVtqDH21VTKn42lkqJH3QTJRbJBFs1dM!2141628305!-148112031;");

            /**
             * personal information search
             */
            long start = System.currentTimeMillis();
            CrawlRequest request = CrawlRequest.build().setProcessorContext(context)
                .setUrl(new LinkNode("http://iservice.10010.com/e3/static/query/accountBalance/search\""))
                .setSearchTemplateId("personalinformation-search-template").contextInit();
            CrawlResponse resp = Crawler.crawl(request);
            logger.info("personal information search response status - " + ResponseUtil.getResponseStatus(resp));
            Assert.assertTrue((ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS || ResponseUtil
                .getResponseStatus(resp) == Status.NO_SEARCH_RESULT));
            logger.info("personal information search ResponseObjectList - " + ResponseUtil.getResponseObjectList(resp));
            if (ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS) {
                Assert.assertNotNull(ResponseUtil.getResponseObjectList(resp));
            }
            logger.info("personal information search search took - " + (System.currentTimeMillis() - start) + "ms");

            /**
             * current fee search
             */
            start = System.currentTimeMillis();
            request = CrawlRequest.build().setProcessorContext(context)
                .setUrl(new LinkNode("http://iservice.10010.com/e3/static/query/currentFee\""))
                .setSearchTemplateId("currentfee-search-template").contextInit();
            resp = Crawler.crawl(request);
            logger.info("curent fee search response status - " + ResponseUtil.getResponseStatus(resp));
            Assert.assertTrue((ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS || ResponseUtil
                .getResponseStatus(resp) == Status.NO_SEARCH_RESULT));
            logger.info("curent fee search ResponseObjectList - " + ResponseUtil.getResponseObjectList(resp));
            if (ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS) {
                Assert.assertNotNull(ResponseUtil.getResponseObjectList(resp));
            }
            logger.info("current fee search took - " + (System.currentTimeMillis() - start) + "ms");

            /**
             * call detail search
             */
            int requestedMonths = 3;
            int monthIndex = 0;
            StringBuilder urlBuilder = null;
            while (requestedMonths-- > 0) {
                start = System.currentTimeMillis();
                urlBuilder = new StringBuilder();
                urlBuilder
                    .append("http://iservice.10010.com/e3/static/query/callDetail")
                    .append("\"")
                    .append("pageNo=")
                    .append(1)
                    .append("&pageSize=")
                    .append(Integer.MAX_VALUE)
                    .append("&beginDate=")
                    .append(
                        new LocalDate().minusMonths(monthIndex).dayOfMonth().withMinimumValue().toString("yyyy-MM-dd"))
                    .append("&endDate=")
                    .append(
                        new LocalDate().minusMonths(monthIndex).dayOfMonth().withMaximumValue().toString("yyyy-MM-dd"));
                request = CrawlRequest.build().setProcessorContext(context).setUrl(new LinkNode(urlBuilder.toString()))
                    .setSearchTemplateId("calldetails-search-template").contextInit();
                resp = Crawler.crawl(request);
                logger.info("call details response status - " + ResponseUtil.getResponseStatus(resp));
                Assert.assertTrue((ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS || ResponseUtil
                    .getResponseStatus(resp) == Status.NO_SEARCH_RESULT));
                logger.info("call details ResponseObjectList - " + ResponseUtil.getResponseObjectList(resp));
                if (ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS) {
                    Assert.assertNotNull(ResponseUtil.getResponseObjectList(resp));
                }
                logger.info("call details search took - " + (System.currentTimeMillis() - start) + "ms");
                monthIndex++;
            }

            /**
             * sms detail search
             */
            requestedMonths = 3;
            monthIndex = 0;
            while (requestedMonths-- > 0) {
                start = System.currentTimeMillis();
                urlBuilder = new StringBuilder();
                urlBuilder.append("http://iservice.10010.com/e3/static/query/sms").append("\"").append("pageNo=")
                    .append(1).append("&pageSize=").append(Integer.MAX_VALUE);
                LocalDate startDate = new LocalDate().minusMonths(monthIndex).dayOfMonth().withMinimumValue();
                LocalDate endDate = new LocalDate().minusMonths(monthIndex).dayOfMonth().withMaximumValue();
                if (endDate.isAfter(new LocalDate())) {
                    endDate = new LocalDate();
                }
                urlBuilder.append("&begindate=").append(startDate.toString("yyyyMMdd")).append("&enddate=")
                    .append(endDate.toString("yyyyMMdd"));
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
                headersMap.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                request = CrawlRequest.build().setProcessorContext(context)
                    .setUrl(new LinkNode(urlBuilder.toString()).addHeaders(headersMap))
                    .setSearchTemplateId("smsdetails-search-template").contextInit();
                resp = Crawler.crawl(request);
                logger.info("sms details response status - " + ResponseUtil.getResponseStatus(resp));
                Assert.assertTrue((ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS || ResponseUtil
                    .getResponseStatus(resp) == Status.NO_SEARCH_RESULT));
                logger.info("sms details ResponseObjectList - " + ResponseUtil.getResponseObjectList(resp));
                if (ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS) {
                    Assert.assertNotNull(ResponseUtil.getResponseObjectList(resp));
                }
                logger.info("sms details search took - " + (System.currentTimeMillis() - start) + "ms");
                monthIndex++;
            }

            /**
             * bill detail search
             */
            requestedMonths = 3;
            monthIndex = 0;
            while (requestedMonths-- > 0) {
                start = System.currentTimeMillis();
                urlBuilder = new StringBuilder();
                urlBuilder.append("http://iservice.10010.com/e3/static/query/queryHistoryBill").append("\"")
                    //.append("querytype=0001").append("&querycode=0001")
                    .append("billdate=")
                    .append(new LocalDate().minusMonths(monthIndex).dayOfMonth().withMinimumValue().toString("yyyyMM"));
                request = CrawlRequest.build().setProcessorContext(context).setUrl(new LinkNode(urlBuilder.toString()))
                    .setSearchTemplateId("billdetail-search-template").contextInit();
                resp = Crawler.crawl(request);
                logger.info("bill details response status - " + ResponseUtil.getResponseStatus(resp));
                Assert.assertTrue((ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS || ResponseUtil
                    .getResponseStatus(resp) == Status.NO_SEARCH_RESULT));
                logger.info("bill details ResponseObjectList - " + ResponseUtil.getResponseObjectList(resp));
                if (ResponseUtil.getResponseStatus(resp) == ProtocolStatusCodes.SUCCESS) {
                    Assert.assertNotNull(ResponseUtil.getResponseObjectList(resp));
                }
                logger.info("bill details search took - " + (System.currentTimeMillis() - start) + "ms");
                monthIndex++;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            Assert.fail("not well format config!");
        }
    }
}