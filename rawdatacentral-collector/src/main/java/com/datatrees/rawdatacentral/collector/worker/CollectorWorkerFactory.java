/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.rawdatacentral.collector.worker;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.datatrees.common.actor.WrappedActorRef;
import com.datatrees.rawdatacentral.collector.actor.TaskMessage;
import com.datatrees.rawdatacentral.collector.search.CrawlExcutorHandler;
import com.datatrees.rawdatacentral.collector.worker.deduplicate.DuplicateChecker;
import com.datatrees.rawdatacentral.collector.worker.deduplicate.DuplicateCheckerFactory;
import com.datatrees.rawdatacentral.core.dao.RedisDao;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.core.subtask.SubTaskManager;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年1月18日 下午3:34:47
 */
@Service
public class CollectorWorkerFactory {

    @Resource
    private WrappedActorRef extractorWorkerRef;
    @Resource
    private ResultDataHandler resultDataHandler;
    @Resource
    private CrawlExcutorHandler crawlExcutorHandler;
    @Resource
    private DuplicateCheckerFactory duplicateCheckerFactory;
    @Resource
    private SubTaskManager subTaskManager;
    @Resource
    private RedisDao redisDao;



    public CollectorWorker getCollectorWorker(TaskMessage taskMessage) {
        CollectorMessage message = taskMessage.getCollectorMessage();
        // DuplicateChecker duplicateChecker = null;
        // if (message.isNeedDuplicate()) {
        // // init duplicateChecker
        // duplicateChecker =
        // duplicateCheckerFactory.duplicateCheckerBuild(taskMessage.getContext().getWebsite(),
        // message.getTaskId());
        // }
        // init collectorWorker
        CollectorWorker collectorWorker = new CollectorWorker().setCrawlExcutorHandler(crawlExcutorHandler).setResultDataHandler(resultDataHandler)
                .setExtractorActorRef(extractorWorkerRef).setSubTaskManager(subTaskManager).setRedisDao(redisDao);

        return collectorWorker;
    }
}
