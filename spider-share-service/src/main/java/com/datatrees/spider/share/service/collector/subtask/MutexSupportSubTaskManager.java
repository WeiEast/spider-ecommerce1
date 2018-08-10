/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.service.collector.subtask;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.spider.share.service.collector.subtask.container.Container;
import com.datatrees.spider.share.service.collector.subtask.container.Mutex;
import com.datatrees.spider.share.service.collector.subtask.container.impl.MutexSubTaskContainer;
import com.datatrees.spider.share.service.collector.subtask.container.impl.SimpleSubTaskContainer;
import com.datatrees.spider.share.service.collector.subtask.pool.SubTaskExecutor;
import com.datatrees.spider.share.service.domain.SubSeed;
import com.datatrees.spider.share.service.domain.SubTask;
import com.datatrees.spider.share.service.extra.SubTaskManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月21日 上午11:03:55
 */
@Service
public class MutexSupportSubTaskManager implements SubTaskManager {

    private static final Logger                             logger                  = LoggerFactory.getLogger(MutexSupportSubTaskManager.class);

    // asyncSubTask has no mutex
    private              LinkedBlockingQueue<SubTask>       asyncSubTaskManagerList = new LinkedBlockingQueue<SubTask>();

    private              Map<Integer, Queue<SubTaskFuture>> syncSubTaskFutureMap    = new ConcurrentHashMap<Integer, Queue<SubTaskFuture>>();

    private              Map<String, SubTaskFuture>         mutexSubTaskFutureMap   = new ConcurrentHashMap<String, SubTaskFuture>();

    private              int                                maxSubTaskWaitSecond    = PropertiesConfiguration.getInstance()
            .getInt("max.subTask.wait.second", 60 * 2);

    private              Map<String, SubTask>               syncMutexSubTaskMap     = new ConcurrentHashMap<String, SubTask>();

    @Resource
    private              SubTaskExecutor                    taskExecutor;

    public MutexSupportSubTaskManager() {
        super();
    }

    @PostConstruct
    public void init() {
        new AsyncSubTaskScheduleThread(taskExecutor).start();
    }

    /*
     * (non-Javadoc)
     *
     * @see SubTaskManager#getSyncedSubTaskResults(int)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List<Map> getSyncedSubTaskResults(int taskid) {
        List<Map> resultsList = new ArrayList<Map>();
        Queue<SubTaskFuture> syncSubTaskLists = syncSubTaskFutureMap.remove(taskid);
        if (CollectionUtils.isNotEmpty(syncSubTaskLists)) {
            for (SubTaskFuture future : syncSubTaskLists) {
                try {
                    Container container = future.container;
                    if (container instanceof Mutex) {
                        ((Mutex) container).stopWaiting();
                        mutexSubTaskFutureMap.remove(future.mutexKey);
                    }
                    Map result = null;
                    try {
                        result = future.future.get(maxSubTaskWaitSecond, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        logger.warn("get sub task " + future.mutexKey + " future error:" + e.getMessage());
                        result = new HashMap();
                        if (e instanceof TimeoutException) {
                            result.put("exception", "sub task execute timeout within " + maxSubTaskWaitSecond + " seconds.");
                        }
                    }
                    if (result == null) {
                        result = new HashMap();
                        result.put("exception", "sub task result empty.");
                    }
                    result.put("subTaskKey", future.mutexKey);
                    resultsList.add(result);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return resultsList;
    }

    private Queue<SubTaskFuture> getSubTaskFutureQueue(SubTask task) {
        Queue<SubTaskFuture> subTaskFutureList = syncSubTaskFutureMap.get(task.getParentTask().getTaskId());
        if (subTaskFutureList == null) {
            synchronized (syncSubTaskFutureMap) {
                subTaskFutureList = syncSubTaskFutureMap.get(task.getParentTask().getTaskId());
                if (subTaskFutureList == null) {
                    subTaskFutureList = new LinkedBlockingQueue<MutexSupportSubTaskManager.SubTaskFuture>();
                    syncSubTaskFutureMap.put(task.getParentTask().getTaskId(), subTaskFutureList);
                }
            }
        }
        return subTaskFutureList;
    }

    private void submitMutexSubTask(SubTask task, Queue<SubTaskFuture> queue) {
        SubSeed seed = task.getSeed();
        String mutexKey = task.getParentTask().getTaskId() + "_" + seed.getUniqueSuffix();
        logger.info("submit mutex sync subtask " + mutexKey + "queue:" + queue.hashCode() + " ,task:" + task);
        SubTaskFuture mutexSubTaskFuture = mutexSubTaskFutureMap.get(mutexKey);
        if (mutexSubTaskFuture == null) {
            synchronized (mutexSubTaskFutureMap) {
                mutexSubTaskFuture = mutexSubTaskFutureMap.get(mutexKey);
                if (mutexSubTaskFuture == null) {
                    Container container = new MutexSubTaskContainer();
                    mutexSubTaskFuture = new SubTaskFuture(mutexKey, container, taskExecutor.submit(container));
                    queue.offer(mutexSubTaskFuture);// add to task list
                    mutexSubTaskFutureMap.put(mutexKey, mutexSubTaskFuture);
                }
            }
        }
        mutexSubTaskFuture.container.addSubTask(task);
    }

    @Override
    public void submitSubTask(SubTask task) {
        SubSeed seed = task.getSeed();
        task.setSubmitAt(System.currentTimeMillis());
        if (BooleanUtils.isTrue(seed.isSync())) {
            // auto submit to pool
            Queue<SubTaskFuture> queue = this.getSubTaskFutureQueue(task);
            if (BooleanUtils.isTrue(seed.isMutex())) {
                this.submitMutexSubTask(task, queue);
            } else {
                Container container = new SimpleSubTaskContainer();
                container.addSubTask(task);
                String mutexKey = task.getParentTask().getTaskId() + "_" + seed.getUniqueSuffix();
                logger.info("submit normal sync subtask " + mutexKey + "queue:" + queue.hashCode() + " ,task:" + task);
                queue.offer(new SubTaskFuture(mutexKey, container, taskExecutor.submit(container)));
            }
        } else {
            if (BooleanUtils.isTrue(seed.isMutex())) {
                String mutexKey = task.getParentTask().getTaskId() + "_" + seed.getUniqueSuffix();
                if (syncMutexSubTaskMap.containsKey(mutexKey)) {
                    logger.info("already contains  async & mutex task for key:" + mutexKey);
                } else {
                    // add to async queue
                    boolean result = asyncSubTaskManagerList.offer(task);
                    syncMutexSubTaskMap.put(mutexKey, task);
                    logger.info("submit async & mutex" + task + " result:" + result);
                }
            } else {
                // add to async queue
                boolean result = asyncSubTaskManagerList.offer(task);
                logger.info("submit async " + task + " result:" + result);
            }
        }
    }

    static class SubTaskFuture {

        String      mutexKey;

        Container   container;

        Future<Map> future;

        /**
         * @param mutexKey
         * @param container
         * @param future
         */
        public SubTaskFuture(String mutexKey, Container container, Future<Map> future) {
            super();
            this.mutexKey = mutexKey;
            this.container = container;
            this.future = future;
        }
    }

    private class AsyncSubTaskScheduleThread extends Thread {

        private final String          waitingOnParentTask     = "parentTask";

        private       boolean         shutdown                = false;

        private       long            scheduleInterval        = PropertiesConfiguration.getInstance()
                .getLong("subtask.async.schedule.interval", 3000);

        private       int             subTaskCorePoolSize     = PropertiesConfiguration.getInstance().getInt("subtask.core.pool.size", 50);

        private       long            maxSubtaskWaitingMillis = PropertiesConfiguration.getInstance().getInt("max.subtask.waiting.minutes", 5) * 60 *
                1000L;

        private       SubTaskExecutor subTaskExecutor;

        /**
         * @param subTaskExecutor
         */
        public AsyncSubTaskScheduleThread(SubTaskExecutor subTaskExecutor) {
            super(AsyncSubTaskScheduleThread.class.getSimpleName());
            this.subTaskExecutor = subTaskExecutor;
        }

        public void shutdown() {
            this.shutdown = true;
        }

        private boolean subTaskWaitingOnCondition(SubTask subTask) {
            String waiting = subTask.getSeed().getWaiting();
            if (waiting.equals(waitingOnParentTask)) {
                if (subTask.getSubmitAt() + maxSubtaskWaitingMillis > System.currentTimeMillis()) {
                    if (!subTask.getParentTask().getCollectorMessage().isFinish()) {
                        logger.debug(subTask + " should still waiting for ParentTask finish");
                        return false;
                    }
                } else {
                    logger.warn(subTask + " waiting for ParentTask timeout in " + maxSubtaskWaitingMillis / 1000 + "s");
                }
            } else {
                try {
                    long waitingMillis = Long.parseLong(waiting);
                    if (waitingMillis + subTask.getSubmitAt() > System.currentTimeMillis()) {
                        logger.debug(subTask + " should witing for " + waitingMillis / 1000 + " s");
                        return false;
                    }
                } catch (Exception e) {
                    logger.info(e.getMessage(), e);
                }
            }
            return true;
        }

        /**
         * Start execution.
         */
        public void run() {
            logger.info("async subTask scheduler thread started ...");
            while (!shutdown && subTaskExecutor != null) {
                try {
                    int activeCount = subTaskExecutor.getActiveCount();
                    if (activeCount < subTaskCorePoolSize) {
                        int submitCount = 0;
                        Iterator<SubTask> iterator = asyncSubTaskManagerList.iterator();
                        while (iterator.hasNext() && (submitCount + activeCount) < subTaskCorePoolSize) {
                            SubTask subTask = iterator.next();
                            if (StringUtils.isNotBlank(subTask.getSeed().getWaiting()) && !this.subTaskWaitingOnCondition(subTask)) {
                                continue;
                            }
                            if (BooleanUtils.isTrue(subTask.getSeed().isMutex())) {
                                String mutexKey = subTask.getParentTask().getTaskId() + "_" + subTask.getSeed().getUniqueSuffix();
                                syncMutexSubTaskMap.remove(mutexKey);
                                logger.info("submit async & mutex task " + subTask + " ,for mutexKey:" + mutexKey);
                            } else {
                                logger.info("submit async task" + subTask);
                            }
                            Container container = new SimpleSubTaskContainer();
                            container.addSubTask(subTask);
                            subTaskExecutor.submit(container);
                            iterator.remove();// remove subtask from queue
                            submitCount++;
                        }
                    }
                    //logger.info("active thread count " + activeCount + ", asyncSubTask need wait sleep " + scheduleInterval + "毫秒");
                    Thread.sleep(scheduleInterval);
                } catch (Exception e) {
                    logger.warn("AsyncSubTaskScheduleThread interrupted", e);
                }
            }
        }
    }
}
