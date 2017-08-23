/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.OperationType;
import com.datatrees.crawler.core.domain.config.operation.impl.*;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.operation.BaseOperationTest;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 4:04:22 PM
 */
public class OperationTest extends BaseOperationTest {

    @Test
    public void testTemplateOperation() throws Exception {

        String template = "http://stream${stream_id}.qqmusic.qq.com/${music_id}.mp3";
        Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put("music_id", "TTT");
        TemplateOperation op = new TemplateOperation();
        op.setType(OperationType.TEMPLATE.getValue());
        op.setTemplate(template);
        Operation operation = ProcessorFactory.getOperation(op);

        Request req = createDummyRequest("xxxx");
        Response resp = createDummyResponse(null);
        req.setAttribute(Constants.FIELDS_RESULT_MAP, fieldMap);

        try {
            operation.invoke(req, resp);

            String result = "http://stream${stream_id}.qqmusic.qq.com/TTT.mp3";
            assertEquals(result, resp.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception");
        }
    }

    @Ignore
    @Test
    public void testRegexOperation() throws Exception {
        String content = "<a class=\"fl\" href=\"/search?q=guava+collection+empty&newwindow=1&safe=strict&biw=1375&bih=386&ei=Ex8DU5DFIefNiAeqroDADg&start=40&sa=N\">";
        String regex = "start=(\\d+)";
        RegexOperation regexOp = new RegexOperation();
        regexOp.setType(OperationType.REGEX.getValue());
        regexOp.setGroupIndex(1);
        regexOp.setRegex(regex);
        Operation operation = ProcessorFactory.getOperation(regexOp);

        Request req = createDummyRequest(content);
        Response resp = createDummyResponse(null);
        try {
            operation.invoke(req, resp);

            String result = "40";
            assertEquals(result, resp.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception");
        }
    }

    @Ignore
    @Test
    public void testReplaceOperation() throws Exception {
        ReplaceOperation op = new ReplaceOperation();
        op.setType(OperationType.REPLACE.getValue());
        op.setFrom("ttt");
        op.setTo("dd");
        Operation operation = ProcessorFactory.getOperation(op);

        Request req = createDummyRequest("testtttaaa");
        Response resp = createDummyResponse(null);

        try {
            operation.invoke(req, resp);

            String result = "tesddtaaa";
            assertEquals(result, resp.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception");
        }

    }

    @Test
    public void testAppendOperation() throws Exception {
        AppendOperation op = new AppendOperation();
        op.setType(OperationType.APPEND.getValue());
        op.setIndex(1);
        op.setValue("yj");
        Operation operation = ProcessorFactory.getOperation(op);

        Request req = createDummyRequest("");
        Response resp = createDummyResponse(null);

        try {
            operation.invoke(req, resp);
            System.out.println(resp.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception");
        }

    }

    @Test
    public void testTripleOperation() throws Exception {
        TripleOperation op = new TripleOperation();
        op.setType(OperationType.TRIPLE.getValue());
        op.setValue(" =?b:c");
        Operation operation = ProcessorFactory.getOperation(op);

        Request req = createDummyRequest("");
        Response resp = createDummyResponse(null);

        try {
            operation.invoke(req, resp);
            System.out.println(resp.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception");
        }

    }
}
