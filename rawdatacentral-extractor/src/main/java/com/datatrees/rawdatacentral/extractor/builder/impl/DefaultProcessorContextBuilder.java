/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.extractor.builder.impl;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.core.model.ResultType;
import com.datatrees.rawdatacentral.core.service.WebsiteService;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月30日 下午8:32:23
 */
@Component
public class DefaultProcessorContextBuilder {
    private final static Logger logger = LoggerFactory.getLogger(DefaultProcessorContextBuilder.class);
    private String extractorUseDefaultWebsiteIds = PropertiesConfiguration.getInstance().get("extractor.use.default.websiteIds", "162");
    private Set<String> extractorUseDefaultWebsiteIdsSet = new HashSet<String>();
    {
        for (String str : extractorUseDefaultWebsiteIds.split(",")) {
            extractorUseDefaultWebsiteIdsSet.add(str);
        }
    }
    @Resource
    private WebsiteService websiteService;

    public ExtractorProcessorContext buildExtractorProcessorContext(ExtractMessage extractMessage) {
        ResultType resultType = extractMessage.getResultType();
        ExtractorProcessorContext context = null;
        try {
            switch (resultType) {
                case MAILBILL:
                    String websiteIdStr = Integer.valueOf(extractMessage.getWebsiteId()).toString();
                    if(extractorUseDefaultWebsiteIdsSet.contains(websiteIdStr))
                        context = websiteService.getExtractorProcessorContext(extractMessage.getWebsiteId());
                    else
                        context = websiteService.getExtractorProcessorContextWithBankId(extractMessage.getTypeId());
                    break;
                default:
                    // use the same website config to extract
                    context = websiteService.getExtractorProcessorContext(extractMessage.getWebsiteId());
                    break;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return context;
    }
}
