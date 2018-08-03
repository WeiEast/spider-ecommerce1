/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.spider.share.service.collector.worker;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.collector.actor.TaskMessage;
import com.datatrees.rawdatacentral.collector.search.CrawlExecutor;
import com.datatrees.rawdatacentral.collector.worker.filter.BusinessTypeFilter;
import com.datatrees.spider.share.service.collector.actor.TaskMessage;
import com.datatrees.spider.share.service.collector.search.CrawlExecutor;
import com.datatrees.spider.share.service.dao.RedisDao;
import com.datatrees.spider.share.service.extra.SubTaskManager;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2016年1月18日 下午3:34:47
 */
@Service
public class CollectorWorkerFactory {

    @Resource
    private ResultDataHandler  resultDataHandler;

    @Resource
    private CrawlExecutor      crawlExecutor;

    @Resource
    private SubTaskManager     subTaskManager;

    @Resource
    private RedisDao           redisDao;

    @Resource
    private BusinessTypeFilter businessTypeFilter;

    public CollectorWorker getCollectorWorker(TaskMessage taskMessage) {

        return new CollectorWorker().setCrawlExecutor(crawlExecutor).setResultDataHandler(resultDataHandler).setSubTaskManager(subTaskManager)
                .setRedisDao(redisDao).setBusinessTypeFilter(businessTypeFilter);
    }
}
