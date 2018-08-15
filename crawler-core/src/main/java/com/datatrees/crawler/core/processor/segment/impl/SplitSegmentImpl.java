/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.segment.impl;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.segment.impl.SplitSegment;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 25, 2014 10:03:56 AM
 */
public class SplitSegmentImpl extends SegmentBase<SplitSegment> {

    public SplitSegmentImpl(@Nonnull SplitSegment segment) {
        super(segment);
    }

    @Override
    protected List<String> splitInputContent(String content, SplitSegment segment, SpiderRequest request, SpiderResponse response) {
        if (StringUtils.isNotEmpty(content)) {
            String split = StringUtils.defaultString(segment.getSplitString());

            logger.debug("Splitter separate: {}", split);

            if (!split.isEmpty()) {
                String[] regexResult = content.split(split);
                Matcher m = RegExp.getMatcher(split, content);
                int count = 0;
                while (count < regexResult.length) {
                    if (BooleanUtils.isTrue(segment.getAppend())) {
                        if (m.find()) {
                            regexResult[count] = regexResult[count] + m.group();
                        }
                    } else {
                        if (count > 0 && m.find()) {
                            regexResult[count] = m.group() + regexResult[count];
                        }
                    }
                    count++;
                }
                return Arrays.asList(regexResult);
            }
        }

        return Collections.singletonList(content);
    }
}
