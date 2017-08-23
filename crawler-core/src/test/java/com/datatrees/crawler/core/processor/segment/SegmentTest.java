/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.segment;

import java.util.List;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.domain.config.segment.impl.XpathSegment;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.util.xml.exception.ParseException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 25, 2014 10:41:32 AM
 */
public class SegmentTest extends BaseConfigTest {

    private static final Logger                 log    = LoggerFactory.getLogger(SegmentTest.class);
    private static       SearchProcessorContext config = null;

    @BeforeClass
    public static void init() {
        String fileName = "config.xml";
        try {
            config = getProcessorContext(fileName, "sohu.com");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void testSegmentByXpath() {
        XpathSegment xpathSegment = new XpathSegment();
        xpathSegment.setXpath("/div");

        try {
            SegmentBase segmentBase = ProcessorFactory.getSegment(xpathSegment);
            Request request = new Request("xxx");
            Response response = Response.build();
            segmentBase.invoke(request, response);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) response.getAttribute(Constants.SEGMENTS_RESULTS);
            log.info("result size:\t" + resultList);
            for (Map<String, Object> map : resultList) {

                log.info("field info:\t" + map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSearchSegment() {

        String content = getContent("search.html");

        Page page = config.getPage("keyword-search-page");

        AbstractSegment segment = page.getSegmentList().get(1);

        Request req = new Request(content);
        Response resp = Response.build();

        try {
            SegmentBase segmentBase = ProcessorFactory.getSegment(segment);
            segmentBase.invoke(req, resp);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) resp.getAttribute(Constants.SEGMENTS_RESULTS);
            log.info("result size:\t" + resultList.size());
            for (Map<String, Object> map : resultList) {

                log.info("field info:\t" + map.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void testSegmentByRegex() {

    }

    @Ignore
    @Test
    public void testSegmentBySplit() {

    }

}
