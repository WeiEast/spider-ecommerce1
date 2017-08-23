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
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.domain.config.segment.impl.XpathSegment;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 25, 2014 10:03:41 AM
 */
public class XpathSegmentImpl extends SegmentBase<XpathSegment> {

    private static final Logger log = LoggerFactory.getLogger(XpathSegmentImpl.class);

    @Override
    public List<String> getSplit(Request request) {
        String content = RequestUtil.getContent(request);

        List<String> result = new LinkedList<>();

        XpathSegment segment = getSegment();
        String xpath = segment.getXpath();

        if (StringUtils.isNotBlank(xpath)) {
            Map<String, Object> sourceMap = RequestUtil.getSourceMap(request);
            xpath = ReplaceUtils.replaceMap(sourceMap, xpath);

            List<String> segments = XPathUtil.getXpath(xpath, content);
            log.info("segment count@" + segments.size() + " by using xpath.." + xpath);
            if (CollectionUtils.isNotEmpty(segments)) {
                result.addAll(segments);
            }
        } else {
            result.add(content);
        }

        return result;
    }

}
