/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.core.model.subtask.ParentTask;
import com.datatrees.rawdatacentral.domain.enums.ExtractCode;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午11:12:56
 */
public class ExtractMessage {

    private int         taskLogId;//TaskLog Id
    private Long        taskId;
    private int         websiteId;// search websiteid
    private ResultType  ResultType;
    private int         typeId;// maybe bankid，operatorid，ecommerceid
    private Object      messageObject;
    private ExtractCode extractCode;
    private Map<String, String> submitkeyResult = new HashMap<String, String>();
    private ParentTask           task;
    private List<ExtractMessage> subExtractMessageList;
    private Integer              messageIndex;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the messageIndex
     */
    public Integer getMessageIndex() {
        return messageIndex;
    }

    /**
     * @param messageIndex the messageIndex to set
     */
    public void setMessageIndex(Integer messageIndex) {
        this.messageIndex = messageIndex;
    }

    public int getTaskLogId() {
        return taskLogId;
    }

    public void setTaskLogId(int taskLogId) {
        this.taskLogId = taskLogId;
    }

    /**
     * @return the websiteId
     */
    public int getWebsiteId() {
        return websiteId;
    }

    /**
     * @param websiteId the websiteId to set
     */
    public void setWebsiteId(int websiteId) {
        this.websiteId = websiteId;
    }

    /**
     * @return the resultType
     */
    public ResultType getResultType() {
        return ResultType;
    }

    /**
     * @param resultType the resultType to set
     */
    public void setResultType(ResultType resultType) {
        ResultType = resultType;
    }

    /**
     * @return the typeId
     */
    public int getTypeId() {
        return typeId;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    /**
     * @return the messageObject
     */
    public Object getMessageObject() {
        return messageObject;
    }

    /**
     * @param messageObject the messageObject to set
     */
    public void setMessageObject(Object messageObject) {
        this.messageObject = messageObject;
    }

    /**
     * @return the extractCode
     */
    public ExtractCode getExtractCode() {
        return extractCode;
    }

    /**
     * @param extractCode the extractCode to set
     */
    public void setExtractCode(ExtractCode extractCode) {
        this.extractCode = extractCode;
    }

    /**
     * @return the submitkeyResult
     */
    public Map<String, String> getSubmitkeyResult() {
        return submitkeyResult;
    }

    /**
     * @param submitkeyResult the submitkeyResult to set
     */
    public void setSubmitkeyResult(Map<String, String> submitkeyResult) {
        this.submitkeyResult = submitkeyResult;
    }

    /**
     * @return the task
     */
    public ParentTask getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(ParentTask task) {
        this.task = task;
    }

    /**
     * @return the subExtractMessageList
     */
    public List<ExtractMessage> getSubExtractMessageList() {
        return subExtractMessageList;
    }

    /**
     * @param subExtractMessageList the subExtractMessageList to set
     */
    public void setSubExtractMessageList(List<ExtractMessage> subExtractMessageList) {
        this.subExtractMessageList = subExtractMessageList;
    }

    public void addSubExtractMessage(ExtractMessage subExtractMessage) {
        if (subExtractMessageList == null) {
            synchronized (this) {
                if (subExtractMessageList == null) {
                    subExtractMessageList = new ArrayList<ExtractMessage>();
                }
            }
        }
        subExtractMessageList.add(subExtractMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ExtractMessage [taskLogId=" + taskLogId + ",taskId=" + taskId + ", ResultType=" + ResultType + ", typeId=" + typeId + "]";
    }

}
