/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor;

import javax.annotation.Nonnull;
import java.util.List;

import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.bean.ExtractRequest;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.treefinance.crawler.exception.UnexpectedException;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.function.SpiderResponseFactory;
import com.treefinance.toolkit.util.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 8:48:55 PM
 */
public class Extractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Extractor.class);

    /**
     * crawler request main route step 1: request via httpclient step 2: parse page content
     * @param request
     * @return
     */
    public static SpiderResponse extract(@Nonnull ExtractRequest request) {
        Preconditions.notNull("request", request);
        Preconditions.notNull("input", request.getInput());

        SpiderResponse response = SpiderResponseFactory.make();

        try {
            ExtractorProcessorContext context = (ExtractorProcessorContext) request.getProcessorContext();
            ExtractorSelectorHandler selectorHandler = new ExtractorSelectorHandler(context.getExtractorSelectors(), context.getPageExtractorMap());
            List<PageExtractor> pageExtractors = selectorHandler.select(request);

            if (CollectionUtils.isEmpty(pageExtractors)) {
                throw new UnexpectedException("Empty page extractors!");
            }

            for (int i = 0, length = pageExtractors.size(); i < length; i++) {
                PageExtractor pageExtractor = pageExtractors.get(i);
                try {
                    PageExtractorImpl pageExtractorImpl = new PageExtractorImpl(pageExtractor);
                    pageExtractorImpl.invoke(request.copy(), response);
                } catch (Exception e) {
                    if (i >= length - 1) {
                        throw e;
                    }
                    LOGGER.warn("Error invoking page extractor[{}], error: {}", pageExtractor.getId(), e.getMessage());
                    continue;
                }

                if (MapUtils.isNotEmpty(ResponseUtil.getResponsePageExtractResultMap(response))) {
                    ResponseUtil.setPageExtractor(response, pageExtractor);
                    break;
                }
            }
        } catch (Exception e) {
            if (e instanceof ResultEmptyException) {
                LOGGER.warn("extract request error." + e.getMessage());
            } else {
                LOGGER.error("extract request error.", e);
            }
            response.setAttribute(Constants.CRAWLER_EXCEPTION, e);
        }
        return response;
    }

}
