package com.datatrees.crawler.core.processor.segment.impl;

import java.util.LinkedList;
import java.util.List;

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.domain.config.segment.impl.JsonPathSegment;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.datatrees.crawler.core.util.json.JsonPathUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @datetime 2015-07-17 19:45
 */
public class JsonPathSegmentImpl extends SegmentBase<JsonPathSegment> {

    @Override
    protected List<String> getSplit(Request request) {
        String content = RequestUtil.getContent(request);

        List<String> result = new LinkedList<>();

        JsonPathSegment segment = getSegment();
        String jsonPath = segment.getJsonpath();

        if (StringUtils.isNotBlank(jsonPath)) {
            List<String> segments = JsonPathUtil.readAsList(content, jsonPath);
            logger.info("segment count@{} by using jsonPath: {}", segments.size(), jsonPath);
            if (CollectionUtils.isNotEmpty(segments)) {
                result.addAll(segments);
            }
        } else {
            result.add(content);
        }

        return result;
    }
}
