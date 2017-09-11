/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor.selector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.domain.config.extractor.ExtratorSelector;
import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.processor.common.Processor;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.extractor.util.SourceFieldUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午3:59:14
 */
public class ExtratorSelectorImpl extends Processor {

    private List<ExtratorSelector> extratorSelectorList;

    /**
     * @param extratorSelectorList
     */
    public ExtratorSelectorImpl(List<ExtratorSelector> extratorSelectorList) {
        super();
        this.extratorSelectorList = extratorSelectorList;
    }

    private boolean selectorCheck(ExtratorSelector selector, Request request, Set<String> blackPageExtractorIdSet) {
        Object input = request.getInput();
        Preconditions.checkNotNull(input, "input should not be null!");
        String value = RequestUtil.getAttribute(request, selector.getField());
        if (value == null) {
            value = SourceFieldUtil.getInputFieldString(input, selector.getField());
            RequestUtil.setAttribute(request, selector.getField(), value);
        }
        if (value != null) {
            if (StringUtils.isNotBlank(selector.getDisContainRegex()) && PatternUtils.match(selector.getDisContainRegex(), value)) {
                blackPageExtractorIdSet.add(selector.getPageExtractor().getId());
                return false;
            }
            if (StringUtils.isNotBlank(selector.getContainRegex()) && PatternUtils.match(selector.getContainRegex(), value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        Preconditions.checkNotNull(extratorSelectorList, "field extractor should not be null");
        List<PageExtractor> matchedPageExtractorList = new ArrayList<PageExtractor>();
        Set<String> blackPageExtractorIdSet = new HashSet<String>();
        for (ExtratorSelector selector : extratorSelectorList) {
            if (this.selectorCheck(selector, request, blackPageExtractorIdSet)) {
                matchedPageExtractorList.add(selector.getPageExtractor());
            }
        }
        ResponseUtil.setMatchedPageExtractorList(response, matchedPageExtractorList);
        ResponseUtil.setBlackPageExtractorIdSet(response, blackPageExtractorIdSet);
    }
}
