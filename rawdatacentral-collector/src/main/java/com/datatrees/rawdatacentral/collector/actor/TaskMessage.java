/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.actor;

import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.model.Task;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午5:47:32
 */
public class TaskMessage {

    private Task                   task;
    private SearchProcessorContext context;
    private Boolean                messageSend;
    private int                    parentTaskID;
    private CollectorMessage       collectorMessage;
    private String                 templateId;
    private String                 uniqueSuffix;
    private Boolean                statusSend;

    /**
     * @param task
     * @param context
     */
    public TaskMessage(Task task, SearchProcessorContext context) {
        super();
        this.task = task;
        this.context = context;
        this.messageSend = true;
        this.statusSend = true;
    }

    /**
     * @return the task
     */
    public Task getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(Task task) {
        this.task = task;
    }

    /**
     * @return the context
     */
    public SearchProcessorContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(SearchProcessorContext context) {
        this.context = context;
    }

    /**
     * @return the websiteName
     */
    public String getWebsiteName() {
        return collectorMessage.getWebsiteName();
    }

    /**
     * @return the templateId
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId the templateId to set
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * @return the messageSend
     */
    public Boolean getMessageSend() {
        return messageSend;
    }

    /**
     * @param messageSend the messageSend to set
     */
    public void setMessageSend(Boolean messageSend) {
        this.messageSend = messageSend;
    }

    /**
     * @return the parentTaskID
     */
    public int getParentTaskID() {
        return parentTaskID;
    }

    /**
     * @param parentTaskID the parentTaskID to set
     */
    public void setParentTaskID(int parentTaskID) {
        this.parentTaskID = parentTaskID;
    }

    /**
     * @return the collectorMessage
     */
    public CollectorMessage getCollectorMessage() {
        return collectorMessage;
    }

    /**
     * @param collectorMessage the collectorMessage to set
     */
    public void setCollectorMessage(CollectorMessage collectorMessage) {
        this.collectorMessage = collectorMessage;
    }

    /**
     * @return the uniqueSuffix
     */
    public String getUniqueSuffix() {
        return uniqueSuffix;
    }

    /**
     * @param uniqueSuffix the uniqueSuffix to set
     */
    public void setUniqueSuffix(String uniqueSuffix) {
        this.uniqueSuffix = uniqueSuffix;
    }

    /**
     * @return the statusSend
     */
    public Boolean getStatusSend() {
        return statusSend;
    }

    /**
     * @param statusSend the statusSend to set
     */
    public void setStatusSend(Boolean statusSend) {
        this.statusSend = statusSend;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TaskMessage [websiteName=" + getWebsiteName() + ", templateId=" + templateId + ", messageSend=" + messageSend + ", parentTaskID=" + parentTaskID + "]";
    }

}
