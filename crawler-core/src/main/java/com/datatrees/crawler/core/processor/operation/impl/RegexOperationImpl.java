/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.RegexOperation;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorWarpper;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:57:53 PM
 */
public class RegexOperationImpl extends Operation<RegexOperation> {

    @Override
    public void process(Request request, Response response) throws Exception {
        RegexOperation operation = getOperation();
        String regex = operation.getRegex();
        String orginal = OperationHelper.getStringInput(request, response);

        //regex support get value from context
        if (StringUtils.isNotEmpty(regex)) {
            Set<String> replaceList = ReplaceUtils.getReplaceList(regex);
            Map<String, FieldExtractorWarpper> fieldMap = ResponseUtil.getResponseFieldResult(response);
            regex = ReplaceUtils.replaceMap(replaceList, FieldExtractorWarpperUtil.fieldWrapperMapToField(fieldMap), RequestUtil.getSourceMap(request), regex);
        }
        Object result = null;
        if (operation.getGroupIndex() == null || operation.getGroupIndex() < 0) {
            result = RegExp.getMatcher(regex, orginal);
            if (!((Matcher) result).find()) {
                result = null;
            }
        } else {
            int index = operation.getGroupIndex();
            logger.debug("regex: {}, index: {}", regex, index);

            result = RegExp.group(orginal, regex, index, null);

            logger.debug("original: {}, dest: {}", orginal, result);
        }
        response.setOutPut(result);
    }
}
