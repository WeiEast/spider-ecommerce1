/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.share.service.collector.subtask.pool;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.datatrees.spider.share.service.collector.actor.Collector;
import com.datatrees.spider.share.service.collector.subtask.container.Container;
import com.datatrees.spider.share.service.collector.subtask.container.Mutex;
import com.datatrees.spider.share.service.domain.SubTaskCollectorMessage;
import com.datatrees.spider.share.service.domain.SubTask;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月21日 下午1:33:34
 */
@Service
public class MutexSupportSubTaskExecutor implements SubTaskExecutor {

    private static final Logger          logger = LoggerFactory.getLogger(MutexSupportSubTaskExecutor.class);

    private              ExecutorService pool   = Executors.newCachedThreadPool(new ThreadFactoryImpl("SubTaskExecutor_"));

    @Resource
    private              Collector       collector;

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
        return pool.submit(() -> {
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
            if (MapUtils.isNotEmpty(resultObject)) {
                return  resultObject;
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
