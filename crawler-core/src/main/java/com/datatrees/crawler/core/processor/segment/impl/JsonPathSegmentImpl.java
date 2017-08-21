package com.datatrees.crawler.core.processor.segment.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.domain.config.segment.impl.JsonPathSegment;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.datatrees.crawler.core.util.json.JsonPathUtil;

/**
 * @author Jerry
 * @datetime 2015-07-17 19:45
 */
public class JsonPathSegmentImpl extends SegmentBase<JsonPathSegment> {

    private static final Logger log = LoggerFactory.getLogger(JsonPathSegmentImpl.class);

    @Override
    protected List<String> getSplit(Request request) {
        String content = RequestUtil.getContent(request);

        List<String> result = new LinkedList<>();

        JsonPathSegment segment =  getSegment();
        String jsonPath = segment.getJsonpath();

        if(StringUtils.isNotBlank(jsonPath)){
            List<String> segments = JsonPathUtil.readAsList(content, jsonPath);
            log.info("segment count@" + segments.size() + " by using jsonPath.." + jsonPath);
            if (CollectionUtils.isNotEmpty(segments)) {
                result.addAll(segments);
            }
        }else {
            result.add(content);
        }

        return result;
    }
}
