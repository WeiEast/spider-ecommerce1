/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.worker;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import akka.dispatch.Future;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.datatrees.common.actor.WrappedActorRef;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.rawdatacentral.collector.actor.TaskMessage;
import com.datatrees.rawdatacentral.collector.common.CollectorConstants;
import com.datatrees.rawdatacentral.core.common.NormalizerFactory;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.core.model.Task;
import com.datatrees.rawdatacentral.core.model.subtask.ParentTask;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午10:45:33
 */
@Service
public class ResultDataHandler {
    private static final Logger log = LoggerFactory.getLogger(ResultDataHandler.class);

    @Resource
    private NormalizerFactory collectNormalizerFactory;

    private ParentTask getParentTask(TaskMessage taskMessage) {
        ParentTask parentTask = new ParentTask();
        SearchProcessorContext processorContext = taskMessage.getContext();
        Task task = taskMessage.getTask();

        parentTask.setCollectorMessage(taskMessage.getCollectorMessage());
        parentTask.setCookie(ProcessorContextUtil.getCookieString(processorContext));
        parentTask.setProperty(processorContext.getProcessorResult());
        parentTask.setTaskId(task.getId());
        parentTask.setWebsiteName(processorContext.getWebsiteName());
        parentTask.setProcessorContext(processorContext);
        return parentTask;
    }


    public List<Future<Object>> resultListHandler(List<Object> objs, TaskMessage taskMessage, WrappedActorRef extractorActorRef) {
        List<Future<Object>> futureList = new ArrayList<Future<Object>>();
        Task task = taskMessage.getTask();
        ParentTask parentTask = this.getParentTask(taskMessage);
        for (Object obj : objs) {
            ExtractMessage message = new ExtractMessage();
            message.setMessageObject(obj);
            message.setTaskId(task.getId());
            message.setWebsiteId(task.getWebsiteId());
            message.setTask(parentTask);
            try {
                boolean result = collectNormalizerFactory.normalize(message);
                if (result) {
                    Future<Object> future =
                            Patterns.ask(extractorActorRef.getActorRef(), message, new Timeout(CollectorConstants.EXTRACT_ACTOR_TIMEOUT));
                    futureList.add(future);
                } else {
                    log.warn("message normalize failed, message:" + message + ", obj:" + obj);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return futureList;
    }
}
