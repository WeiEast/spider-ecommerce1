/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.process.segment.impl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.segment.impl.RegexSegment;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.process.segment.SegmentBase;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 25, 2014 10:03:15 AM
 */
public class RegexSegmentImpl extends SegmentBase<RegexSegment> {

    public RegexSegmentImpl(@Nonnull RegexSegment segment) {
        super(segment);
    }

    @Override
    public List<String> splitInputContent(String content, RegexSegment segment, SpiderRequest request, SpiderResponse response) {
        if(StringUtils.isEmpty(content)){
            return Collections.emptyList();
        }

        String regex = StringUtils.defaultString(segment.getRegex());

        logger.debug("RegExp pattern: {}, group: {}", regex, segment.getGroupIndex());

        if (!regex.isEmpty()) {
            return RegExp.findAll(content, regex, segment.getGroupIndex());
        }

        return Collections.singletonList(content);
    }

}
