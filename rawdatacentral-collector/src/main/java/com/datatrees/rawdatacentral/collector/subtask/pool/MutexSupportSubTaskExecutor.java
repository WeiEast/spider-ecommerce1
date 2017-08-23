/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.subtask.pool;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.collector.subtask.container.Container;
import com.datatrees.rawdatacentral.collector.subtask.container.Mutex;
import com.datatrees.rawdatacentral.core.model.message.impl.SubTaskCollectorMessage;
import com.datatrees.rawdatacentral.core.model.subtask.SubTask;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月21日 下午1:33:34
 */
@Service
public class MutexSupportSubTaskExecutor implements SubTaskExecutor {

    private static final Logger          logger = LoggerFactory.getLogger(MutexSupportSubTaskExecutor.class);
    private              ExecutorService pool   = Executors.newCachedThreadPool(new ThreadFactoryImpl("SubTaskExecutor_"));
    @Resource
    private Collector collector;

    /*
     * (non-Javadoc)
     * 
     * @see
     * SubTaskExecutor#submitSubTask(com.datatrees.
     * rawdatacentral.core.model.subtask.SubTask)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Future<Map> submit(Container container) {
        return pool.submit(new Callable<Map>() {
            @Override
            public Map call() throws Exception {
                if (container instanceof Mutex) {
                    SubTask subTask = container.popSubTask();
                    while (((Mutex) container).waiting() || subTask != null) {
                        if (subTask != null) {
                            Map resultMap = execute(subTask);
                            if (MapUtils.isNotEmpty(resultMap)) {
                                return resultMap;
                            }
                        } else {
                            Thread.sleep(500);
                            logger.warn("no sub task income sleep 500ms, try next round..");
                        }
                        subTask = container.popSubTask();
                    }
                } else {
                    return execute(container.popSubTask());
                }
                return null;
            }
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private SubTaskCollectorMessage initSubTaskCollectorMessage(SubTask task) {
        SubTaskCollectorMessage message = new SubTaskCollectorMessage();
        // set from parent tassk
        message.setCookie(task.getParentTask().getCookie());
        message.setEndURL(task.getParentTask().getCollectorMessage().getEndURL());
        message.setNeedDuplicate(task.getParentTask().getCollectorMessage().isNeedDuplicate());
        message.setLevel1Status(task.getParentTask().getCollectorMessage().isLevel1Status());
        message.setParentTaskID(task.getParentTask().getTaskId());//taskLogId
        message.setTaskId(task.getTaskId());
        message.setSubSeed(task.getSeed());
        // set from seed
        message.setSynced(BooleanUtils.isTrue(task.getSeed().isSync()));
        message.setLoginCheckIgnore(BooleanUtils.isTrue(task.getSeed().getLoginCheckIgnore()));
        message.setTemplateId(task.getSeed().getTemplateId());
        message.setWebsiteName(task.getSeed().getWebsiteName());
        Map property = new HashMap();
        property.putAll(task.getParentTask().getProperty());
        property.putAll(task.getSeed());
        message.setProperty(property);
        return message;
    }

    @SuppressWarnings("rawtypes")
    private Map execute(SubTask task) {
        try {
            logger.info("start to execute sub task taskId={}", task.getTaskId());
            Map resultObject = collector.processMessage(initSubTaskCollectorMessage(task));
            if (resultObject != null && MapUtils.isNotEmpty(resultObject)) {
                return (Map) resultObject;
            }
        } catch (Exception e) {
            logger.error("execute task error:" + e.getMessage(), e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see SubTaskExecutor#shutdown()
     */
    @Override
    public void shutdown() {
        pool.shutdown();
    }

    /*
     * (non-Javadoc)
     * 
     * @see SubTaskExecutor#getActiveCount()
     */
    @Override
    public int getActiveCount() {
        return ((ThreadPoolExecutor) pool).getActiveCount();
    }

}
