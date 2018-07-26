/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.extractor.builder.impl;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.rawdatacentral.core.model.subtask.ParentTask;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月30日 下午8:32:23
 */
@Component
public class DefaultProcessorContextBuilder {

    private final static Logger               logger = LoggerFactory.getLogger(DefaultProcessorContextBuilder.class);

    private final        List<String>         extractorUseDefaultWebsiteIds;

    @Resource
    private              WebsiteConfigService websiteConfigService;

    public DefaultProcessorContextBuilder() {
        String extractorUseDefaultWebsiteIds = PropertiesConfiguration.getInstance().get("extractor.use.default.websiteIds", "162");
        if (StringUtils.isNotEmpty(extractorUseDefaultWebsiteIds)) {
            this.extractorUseDefaultWebsiteIds = Arrays.stream(extractorUseDefaultWebsiteIds.split(",")).map(String::trim).filter(s -> !s.isEmpty())
                    .distinct().collect(Collectors.toList());
        } else {
            this.extractorUseDefaultWebsiteIds = Collections.emptyList();
        }
    }

    public ExtractorProcessorContext buildExtractorProcessorContext(ExtractMessage extractMessage) {
        ResultType resultType = extractMessage.getResultType();
        ExtractorProcessorContext context;

        switch (resultType) {
            case MAILBILL:
                String websiteIdStr = Integer.valueOf(extractMessage.getWebsiteId()).toString();
                if (extractorUseDefaultWebsiteIds.contains(websiteIdStr)) {
                    context = websiteConfigService.getExtractorProcessorContext(extractMessage.getTaskId(), extractMessage.getWebsiteName());
                } else {
                    context = websiteConfigService.getExtractorProcessorContextWithBankId(extractMessage.getTypeId(), extractMessage.getTaskId());
                }
                break;
            default:
                // use the same website config to extract
                context = websiteConfigService.getExtractorProcessorContext(extractMessage.getTaskId(), extractMessage.getWebsiteName());
                break;
        }

        ParentTask task = extractMessage.getTask();
        if (context == null) {
            return null;
        }

        ProcessorContextUtil.setCookieString(context, task.getCookie());
        if (logger.isDebugEnabled()) {
            logger.debug("Add cookies into extract context: {}", task.getCookie());
        }

        return context;
    }
}
