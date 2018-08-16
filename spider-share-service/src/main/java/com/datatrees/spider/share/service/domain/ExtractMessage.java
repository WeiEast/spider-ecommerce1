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

package com.datatrees.spider.share.service.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.spider.share.domain.ExtractCode;
import com.datatrees.spider.share.domain.ResultType;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午11:12:56
 */
public class ExtractMessage {

    private int                                          taskLogId;//TaskLog Id

    private Long                                         taskId;

    private int                                          websiteId;// search websiteid

    private com.datatrees.spider.share.domain.ResultType ResultType;

    private int                                          typeId;// maybe bankid，operatorid，ecommerceid

    private Object                                       messageObject;

    private ExtractCode                                  extractCode;

    private Map<String, String>                          submitkeyResult = new HashMap<String, String>();

    private ParentTask                                   task;

    private List<ExtractMessage>                         subExtractMessageList;

    private Integer                                      messageIndex;

    private String                                       websiteName;

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

    public void addSubmitKey(String name, String value) {
        submitkeyResult.put(name, value);
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

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
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
