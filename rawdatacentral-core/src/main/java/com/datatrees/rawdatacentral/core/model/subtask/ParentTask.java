/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model.subtask;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.spider.share.domain.CollectorMessage;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月20日 上午12:36:15
 */
public class ParentTask {

    private int                    taskId;

    private String                 cookie;

    private String                 websiteName;

    private CollectorMessage       collectorMessage;

    private Map<String, Object>    property = new HashMap<String, Object>();

    private SearchProcessorContext processorContext;

    /**
     * @return the processorContext
     */
    public SearchProcessorContext getProcessorContext() {
        return processorContext;
    }

    /**
     * @param processorContext the processorContext to set
     */
    public void setProcessorContext(SearchProcessorContext processorContext) {
        this.processorContext = processorContext;
    }

    /**
     * @return the taskId
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the cookie
     */
    public String getCookie() {
        return cookie;
    }

    /**
     * @param cookie the cookie to set
     */
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    /**
     * @return the websiteName
     */
    public String getWebsiteName() {
        return websiteName;
    }

    /**
     * @param websiteName the websiteName to set
     */
    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    /**
     * @return the property
     */
    public Map<String, Object> getProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(Map<String, Object> property) {
        this.property = property;
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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ParentTask [taskId=" + taskId + ", websiteName=" + websiteName + ", property=" + property + "]";
    }

}
