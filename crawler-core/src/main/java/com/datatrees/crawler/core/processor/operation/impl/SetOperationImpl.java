/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.SetOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:34 PM
 */
public class SetOperationImpl extends Operation {

    private static final Logger log = LoggerFactory.getLogger(SetOperationImpl.class);

    /*
     * (non-Javadoc)
     */
    @Override
    public void process(Request request, Response response) throws Exception {
        SetOperation operation = (SetOperation) getOperation();
        String output = operation.getValue();
        if (log.isDebugEnabled()) {
            log.debug("operation set value " + output);
        }
        if (output != null && output.equals("${empty}")) {
            response.setOutPut("");
        } else {
            response.setOutPut(output);
        }
    }

}
