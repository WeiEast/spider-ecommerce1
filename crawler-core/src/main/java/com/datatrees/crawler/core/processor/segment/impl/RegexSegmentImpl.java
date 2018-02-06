/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.segment.impl;

import java.util.LinkedList;
import java.util.List;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.util.StringUtils;
import com.datatrees.crawler.core.domain.config.segment.impl.RegexSegment;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.treefinance.toolkit.util.RegExp;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 25, 2014 10:03:15 AM
 */
public class RegexSegmentImpl extends SegmentBase<RegexSegment> {

    @Override
    public List<String> getSplit(Request request) {
        String content = RequestUtil.getContent(request);

        List<String> result = new LinkedList<String>();

        RegexSegment segment = getSegment();
        String regex = segment.getRegex();

        if (StringUtils.isNotEmpty(regex)) {
            List<String> regexResult = RegExp.findAll(content, regex, segment.getGroupIndex());
            result.addAll(regexResult);
        } else {
            result.add(content);
        }

        return result;
    }

}
