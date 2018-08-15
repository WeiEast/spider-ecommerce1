/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.crawler.framework.process.extract;

import javax.annotation.Nonnull;
import java.util.*;

import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.treefinance.crawler.framework.process.extract.PageSourceImpl;
import com.treefinance.crawler.framework.process.segment.SegmentBase;
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
