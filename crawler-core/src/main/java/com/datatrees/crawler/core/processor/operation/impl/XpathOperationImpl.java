/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import java.util.List;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.XpathOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:58:48 PM
 */
public class XpathOperationImpl extends Operation<XpathOperation> {

    @Override
    public void process(Request request, Response response) throws Exception {
        XpathOperation operation = getOperation();
        String xpath = operation.getXpath();

        xpath = StandardExpression.eval(xpath, request, response);

        String orginal = OperationHelper.getStringInput(request, response);
        String resultStirng = "";
        List<String> result = XPathUtil.getXpath(xpath, orginal);
        if (CollectionUtils.isNotEmpty(result)) {
            for (String temp : result) {
                resultStirng = resultStirng + temp;
            }
        } else {
            logger.warn("xpath extract empty content! - {}", xpath);
            resultStirng = "";
        }
        resultStirng = StringUtils.isEmpty(resultStirng) && BooleanUtils.isTrue(operation.getEmptyToNull()) ? null : resultStirng;
        logger.debug("xpath extracted result: {}", resultStirng);
        response.setOutPut(resultStirng);

    }

}
