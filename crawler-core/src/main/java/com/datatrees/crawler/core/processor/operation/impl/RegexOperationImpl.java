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
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.domain.config.operation.impl.RegexOperation;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorWarpper;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:57:53 PM
 */
public class RegexOperationImpl extends Operation {

    private static final Logger log = LoggerFactory.getLogger(RegexOperationImpl.class);

    /*
     * (non-Javadoc)
     */
    @Override
    public void process(Request request, Response response) throws Exception {
        RegexOperation operation = (RegexOperation) getOperation();
        String regex = operation.getRegex();
        String orginal = getInput(request, response);

        //regex support get value from context
        if (StringUtils.isNotEmpty(regex)) {
            Set<String> replaceList = ReplaceUtils.getReplaceList(regex);
            Map<String, FieldExtractorWarpper> fieldMap = ResponseUtil.getResponseFieldResult(response);
            regex = ReplaceUtils.replaceMap(replaceList, FieldExtractorWarpperUtil.fieldWrapperMapToField(fieldMap), RequestUtil.getSourceMap(request), regex);
        }
        Object result = null;
        if (operation.getGroupIndex() == null || operation.getGroupIndex() < 0) {
            result = PatternUtils.matcher(regex, orginal);
            if (!((Matcher) result).find()) {
                result = null;
            }
        } else {
            int index = operation.getGroupIndex();
            if (log.isDebugEnabled()) {
                log.debug("RegexOperation input: " + String.format("regex: %s, index: %d", regex, index));
            }
            result = PatternUtils.groupDefaultNull(orginal, regex, index);
            if (log.isDebugEnabled()) {
                log.debug("RegexOperation content: " + String.format("orginal: %s , dest: %s", orginal, result));
            }
        }
        response.setOutPut(result);
    }
}
