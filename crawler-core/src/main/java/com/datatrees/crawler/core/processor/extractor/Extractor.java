/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor;

import java.util.*;
import java.util.Map.Entry;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.page.impl.PageExtractor;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.ExtractorRepuest;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.extractor.selector.ExtractorSelectorImpl;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 8:48:55 PM
 */
public class Extractor {

    private static final Logger log = LoggerFactory.getLogger(Extractor.class);

    /**
     * crawler request main route step 1: request via httpclient step 2: parse page content
     * @param request
     * @return
     */
    public static Response extract(ExtractorRepuest request) {
        Response response = Response.build();
        Object input = request.getInput();

        try {
            Preconditions.checkNotNull(input, "input should not be null!");
            ExtractorProcessorContext context = (ExtractorProcessorContext) RequestUtil.getProcessorContext(request);
            ExtractorSelectorImpl processer = new ExtractorSelectorImpl(context.getWebsite().getExtractorConfig().getExtractorSelectors());
            processer.invoke(request, response);
            Collection<PageExtractor> list = ResponseUtil.getMatchedPageExtractorList(response);
            Set<String> blackSet = ResponseUtil.getBlackPageExtractorIdSet(response);

            Map<String, PageExtractor> totalPageExtractors = new HashMap<String, PageExtractor>();
            for (PageExtractor matchPageExtractor : list) {
                totalPageExtractors.put(matchPageExtractor.getId(), matchPageExtractor);
            }
            Iterator<Entry<String, PageExtractor>> it = context.getPageExtractorMap().entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, PageExtractor> entry = it.next();
                if (BooleanUtils.isTrue(entry.getValue().getDisAlternative()) && !totalPageExtractors.containsKey(entry.getKey())) {
                    log.debug("remove dis alternative PageExtractor:" + entry.getValue());
                } else if (blackSet != null && blackSet.contains(entry.getKey())) {
                    log.debug("remove balck PageExtractor:" + entry.getValue());
                } else {
                    totalPageExtractors.put(entry.getKey(), entry.getValue());
                }
            }

            if (CollectionUtils.isEmpty(list)) {
                log.warn("empty pageExtractor list after selector,try test with all pageExtractors...");
                list = new ArrayList<PageExtractor>(totalPageExtractors.values());
            }
            while (!doPageExtractor(request, response, list, totalPageExtractors)) {
                if (CollectionUtils.isEmpty(totalPageExtractors.values())) {
                    break;
                }
                list = new ArrayList<PageExtractor>(totalPageExtractors.values());
            }

        } catch (Exception e) {
            if (e instanceof ResultEmptyException) {
                log.warn("extract request error." + e.getMessage());
            } else {
                log.error("extract request error.", e);
            }
            response.setAttribute(Constants.CRAWLER_EXCEPTION, e);
        }
        return response;
    }

    private static boolean doPageExtractor(Request request, Response response, Collection<PageExtractor> list,
            Map<String, PageExtractor> totalPageExtractors) throws Exception {
        for (PageExtractor pageExtractor : list) {
            log.info("use " + pageExtractor + " to extract page...");
            totalPageExtractors.remove(pageExtractor.getId());
            try {
                PageExtractorImpl pageExtractorImpl = new PageExtractorImpl(pageExtractor);
                pageExtractorImpl.invoke(Request.clone(request), response);
            } catch (Exception e) {
                log.warn("extract request error " + e.getMessage());
                if (MapUtils.isEmpty(totalPageExtractors)) {
                    throw e;
                } else {
                    continue;
                }
            }
            if (MapUtils.isNotEmpty(ResponseUtil.getResponsePageExtractResultMap(response))) {
                ResponseUtil.setPageExtractor(response, pageExtractor);
                return true;
            }
        }
        return false;
    }

    /**
     * add default conf if not exists
     * @param request
     */
    private static void checkConf(CrawlRequest request) {
        Configuration conf = request.getConf();
        if (conf == null) {
            conf = PropertiesConfiguration.getInstance();
            RequestUtil.setConf(request, conf);
        }
    }

}
