/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor;

import javax.annotation.Nonnull;
import java.util.*;

import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.extractor.source.PageSourceImpl;
import com.datatrees.crawler.core.processor.segment.SegmentBase;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.function.SpiderResponseFactory;
import com.treefinance.crawler.framework.context.pipeline.ProcessorInvokerAdapter;
import com.treefinance.toolkit.util.Preconditions;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午9:14:20
 */
public class PageExtractorImpl extends ProcessorInvokerAdapter {

    private final PageExtractor pageExtractor;

    public PageExtractorImpl(@Nonnull PageExtractor pageExtractor) {
        this.pageExtractor = Objects.requireNonNull(pageExtractor);
    }

    @Override
    public void process(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        Preconditions.notNull("input", request.getInput());
        List<AbstractSegment> segments = pageExtractor.getSegmentList();
        logger.info("segments size[{}] in page-extractor: {}", segments.size(), pageExtractor.getId());
        if (CollectionUtils.isEmpty(segments)) {
            logger.warn("Empty segment processor and skip page extractor. page-extractor: {}", pageExtractor.getId());
            return;
        }

        PageSourceImpl pageSourceImpl = new PageSourceImpl(pageExtractor.getPageSourceList());
        pageSourceImpl.invoke(request, response);

        this.extractObjectsWithSegments(segments, request, response);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void segResultConvert(Map segmentResultMap, Object segResult, AbstractSegment abstractSegment) {
        if (segResult == null) {
            return;
        }

        Object oldValue = segmentResultMap.putIfAbsent(abstractSegment.getName(), segResult);

        if (oldValue instanceof Collection) {
            if (segResult instanceof Collection) {
                ((Collection) oldValue).addAll((Collection) segResult);
            } else {
                ((Collection) oldValue).add(segResult);
            }
        } else if (oldValue != null) {
            List newValue = new ArrayList();
            newValue.add(oldValue);

            if (segResult instanceof Collection) {
                newValue.addAll((Collection) segResult);
            } else {
                newValue.add(segResult);
            }
            segmentResultMap.put(abstractSegment.getName(), newValue);
        }
    }

    @SuppressWarnings("rawtypes")
    private void extractObjectsWithSegments(List<AbstractSegment> segments, SpiderRequest req, SpiderResponse resp) throws ResultEmptyException {
        logger.info("extractObjectsWithSegments: segment size.." + segments.size());
        Map segmentResultMap = new HashMap();
        for (AbstractSegment abstractSegment : segments) {
            try {
                SpiderResponse segResponse = SpiderResponseFactory.make();
                SegmentBase segmentBase = ProcessorFactory.getSegment(abstractSegment);

                segmentBase.invoke(req, segResponse);

                Object segResult = ResponseUtil.getSegmentsResults(segResponse);

                this.segResultConvert(segmentResultMap, segResult, abstractSegment);

            } catch (ResultEmptyException e) {
                throw e;
            } catch (Exception e) {
                logger.error("invoke segment processor error!", e);
            }
        }

        ResponseUtil.setResponsePageExtractResultMap(resp, segmentResultMap);
    }

}
