/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.worker.deduplicate;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.collector.chain.common.WebsiteType;
import com.datatrees.rawdatacentral.collector.worker.deduplicate.impl.DuplicateCheckerImpl;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.rawdatacentral.core.common.UnifiedSysTime;
import com.datatrees.rawdatacentral.core.model.Task;
import com.datatrees.rawdatacentral.core.service.ExtractorResultService;
import com.datatrees.rawdatacentral.core.service.TaskService;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月27日 下午12:56:28
 */
@Service
public class DuplicateCheckerFactory {
    private static final Logger log = LoggerFactory.getLogger(DuplicateCheckerFactory.class);

    private long duplicateCheckerWithinHours = PropertiesConfiguration.getInstance().getLong("duplicate.checker.within.hours", 48);
    private int websiteTaskCountThreshold = PropertiesConfiguration.getInstance().getInt("website.task.count.threshold", 2);

    @Resource
    private ExtractorResultService extractorResultService;

    @Resource
    private TaskService taskService;

    public DuplicateChecker duplicateCheckerBuild(Website website, int userId) {
        List<Task> tasks =
                taskService.selectWebisteTaskWithinPeriod(userId, website.getId(), new Date(UnifiedSysTime.INSTANCE.getSystemTime().getTime()
                        - duplicateCheckerWithinHours * 3600 * 1000));

        Set<String> existedKeySet = null;
        // task not empty means user retry to import data, and don't do duplicate check,
        // websiteTaskCountThreshold min=2;去除当前次task
        if (CollectionUtils.isEmpty(tasks) || tasks.size() < websiteTaskCountThreshold) {
            log.info("try to load duplicateChecker for userId:{}, website:{}", userId, website);
            WebsiteType type = WebsiteType.getWebsiteType(website.getWebsiteType());
            if (type != null) {
                switch (type) {
                    case MAIL:
                        existedKeySet = extractorResultService.getUserSuccessParsedMailKeySet(userId);
                        break;
                    case OPERATOR:
                        break;
                    case ECOMMERCE:
                        break;
                    case BANK:
                        existedKeySet = extractorResultService.getUserSuccessParsedEBankBillSet(userId);
                        break;
                    default:
                        break;
                }
            }
        } else {
            log.warn("no need to do duplicate check for websiteid:" + website.getId() + "," + "userid:" + userId + ",withinHours:"
                    + duplicateCheckerWithinHours + ",size:" + tasks.size() + ",threshold:" + websiteTaskCountThreshold);
        }

        DuplicateChecker checker = new DuplicateCheckerImpl(existedKeySet);
        return checker;
    }
}
