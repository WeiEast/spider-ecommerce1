/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor.selector;

import javax.annotation.Nonnull;
import java.util.*;

import com.datatrees.common.pipeline.ProcessorInvokerAdapter;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.ExtractorSelector;
import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.google.common.base.Preconditions;
import com.treefinance.crawler.framework.util.SourceFieldUtils;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午3:59:14
 */
public class ExtractorSelectorImpl extends ProcessorInvokerAdapter {

    private final List<ExtractorSelector> extractorSelectors;

    public ExtractorSelectorImpl(@Nonnull List<ExtractorSelector> extractorSelectors) {
        this.extractorSelectors = Objects.requireNonNull(extractorSelectors);
    }

    @Override
    public void process(@Nonnull Request request, @Nonnull Response response) throws Exception {
        Object input = request.getInput();
        Preconditions.checkNotNull(input, "input should not be null!");

        List<PageExtractor> matchedPageExtractors = new ArrayList<>();
        Set<String> blackPageExtractorIds = new HashSet<>();

        for (ExtractorSelector selector : extractorSelectors) {
            String value = RequestUtil.getAttribute(request, selector.getField());
            if (value == null) {
                value = SourceFieldUtils.getFieldValueAsString(input, selector.getField(), null);
                RequestUtil.setAttribute(request, selector.getField(), value);
            }

            logger.debug("Filter extractor selector[{}] >>> {}", selector.getPageExtractor().getId(), value);

            if (value == null) continue;

            if (match(value, selector.getDisContainRegex())) {
                blackPageExtractorIds.add(selector.getPageExtractor().getId());
            } else if (match(value, selector.getContainRegex())) {
                logger.debug("Filter extractor selector[{}] >>> matched", selector.getPageExtractor().getId());

                matchedPageExtractors.add(selector.getPageExtractor());
            }
        }

        ResponseUtil.setMatchedPageExtractorList(response, matchedPageExtractors);
        ResponseUtil.setBlackPageExtractorIdSet(response, blackPageExtractorIds);
    }

    private boolean match(String value, String regex) {
        return StringUtils.isNotBlank(regex) && RegExp.find(value, regex);
    }
}
