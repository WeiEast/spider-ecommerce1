/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.operation.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.CalculateOperation;
import com.datatrees.crawler.core.processor.common.CalculateUtil;
import com.datatrees.crawler.core.processor.operation.Operation;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月21日 上午10:29:59
 */
public class CalculateOperationImpl extends Operation {
    private static final Logger log = LoggerFactory.getLogger(CalculateOperationImpl.class);


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.operation.Operation#process(com.datatrees.common.pipeline
     * .Request, com.datatrees.common.pipeline.Response)
     */
    @Override
    public void process(Request request, Response response) throws Exception {
        CalculateOperation operation = (CalculateOperation) getOperation();
        String expression = operation.getValue();
        Object result = null;

        // regex support get value from context
        if (StringUtils.isNotEmpty(expression)) {
            result = CalculateUtil.sourceCalculate(request, response, expression, null);
        }
        if (log.isDebugEnabled()) {
            log.debug("calculate input:" + expression + " ,result:" + result);
        }
        if (result != null) {
            response.setOutPut(result.toString());
        } else {
            response.setOutPut(null);
        }
    }

}
