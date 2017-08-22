/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.operation;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 4:19:27 PM
 */
public class BaseOperationTest {



    protected Request createDummyRequest(String content) {
        Request request = new Request();
        request.setInput(content);
        return request;
    }


    protected Response createDummyResponse(String content) {
        Response request = new Response();
        request.setOutPut(content);
        return request;
    }
}
