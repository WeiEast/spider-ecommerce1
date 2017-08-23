/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor;

import java.util.List;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.extractor.ResultType;
import com.datatrees.crawler.core.domain.config.operation.OperationType;
import com.datatrees.crawler.core.domain.config.operation.impl.RegexOperation;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.util.xml.exception.ParseException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 24, 2014 7:30:17 PM
 */
public class FieldExtractorTest extends BaseConfigTest {

    private static SearchProcessorContext config = null;

    @BeforeClass
    public static void init() {
        String fileName = "config.xml";
        try {
            config = getProcessorContext(fileName, "sohu.com");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSearchFieldExtractor() {

        String content = getContent("search.html");

        Page page = config.getPage("keyword-search-page");

        AbstractSegment segment = page.getSegmentList().get(1);

        List<FieldExtractor> fieldExtractors = segment.getFieldExtractorList();

        FieldExtractorImpl fieldExtractorImpl = new FieldExtractorImpl();
        FieldExtractor extractor = fieldExtractors.get(0);
        String fieldName = extractor.getField();
        String fieldId = extractor.getId();
        fieldExtractorImpl.setFieldExtractor(extractor);
        Request req = new Request(content);
        Response resp = Response.build();
        try {
            // run
            fieldExtractorImpl.invoke(req, resp);

            // get result
            Map<String, FieldExtractorWarpper> resultMap = (Map<String, FieldExtractorWarpper>) resp.getAttribute(Constants.FIELDS_RESULT_MAP);
            FieldExtractorWarpper fWarpper = resultMap.get(fieldId);
            // print
            System.out.println(fWarpper.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Ignore
    @Test
    public void testFieldExtractor() {
        String content = "var videoId = '168269374';";

        // construct op
        RegexOperation op = new RegexOperation();
        op.setType(OperationType.REGEX.getValue());
        op.setGroupIndex(1);
        op.setRegex("videoId = '(\\d+)'");

        // construct field extractor
        FieldExtractor fExtractor = new FieldExtractor();
        fExtractor.setOperationList(op);
        fExtractor.setId("tttt");
        fExtractor.setField("TTTT");
        fExtractor.setResultType(ResultType.String.getValue());

        // construct request
        Request req = new Request(content);
        Response resp = new Response();
        FieldExtractorImpl fieldExtractorImpl = new FieldExtractorImpl();
        fieldExtractorImpl.setFieldExtractor(fExtractor);

        try {
            // run
            fieldExtractorImpl.invoke(req, resp);

            // get result
            Map<String, FieldExtractorWarpper> resultMap = (Map<String, FieldExtractorWarpper>) resp.getAttribute(Constants.FIELDS_RESULT_MAP);
            String resultId = "tttt";
            FieldExtractorWarpper fWarpper = resultMap.get(resultId);
            // print
            System.out.println(fWarpper.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
