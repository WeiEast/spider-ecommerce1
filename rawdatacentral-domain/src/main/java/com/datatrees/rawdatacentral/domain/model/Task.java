/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.domain.model;

import com.datatrees.rawdatacentral.domain.enums.ErrorCode;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月27日 下午7:19:25
 */
public class Task {
    private int           id;
    private int           websiteId;
    private long          taskId;
    private AtomicInteger openUrlCount       = new AtomicInteger(0);
    private AtomicInteger openPageCount      = new AtomicInteger(0);
    private AtomicInteger requestFailedCount = new AtomicInteger(0);
    private AtomicInteger retryCount         = new AtomicInteger(0);
    private AtomicInteger filteredCount      = new AtomicInteger(0);
    private AtomicLong    networkTraffic     = new AtomicLong(0);

    private int           status;
    private String        remark;
    private long          duration;
    private int           extractedCount;
    private int           extractSucceedCount;
    private int           extractFailedCount;
    private int           storeFailedCount;
    private int           notExtractCount;

    private Date          startedAt;
    private Date          finishedAt;

    private String        resultMessage;
    private String        nodeName;

    private boolean       isDuplicateRemoved;

    /**
     * 是否子任务
     */
    private boolean       isSubTask          = false;

    /**
     * 配置站点名称
     */
    private String        websiteName;

    /**
     * @return the openUrlCount
     */
    public AtomicInteger getOpenUrlCount() {
        return openUrlCount;
    }

    /**
     * @param openUrlCount the openUrlCount to set
     */
    public void setOpenUrlCount(AtomicInteger openUrlCount) {
        this.openUrlCount = openUrlCount;
    }

    /**
     * @return the openPageCount
     */
    public AtomicInteger getOpenPageCount() {
        return openPageCount;
    }

    /**
     * @param openPageCount the openPageCount to set
     */
    public void setOpenPageCount(AtomicInteger openPageCount) {
        this.openPageCount = openPageCount;
    }

    /**
     * @return the requestFailedCount
     */
    public AtomicInteger getRequestFailedCount() {
        return requestFailedCount;
    }

    /**
     * @param requestFailedCount the requestFailedCount to set
     */
    public void setRequestFailedCount(AtomicInteger requestFailedCount) {
        this.requestFailedCount = requestFailedCount;
    }

    /**
     * @return the retryCount
     */
    public AtomicInteger getRetryCount() {
        return retryCount;
    }

    /**
     * @param retryCount the retryCount to set
     */
    public void setRetryCount(AtomicInteger retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * @return the filteredCount
     */
    public AtomicInteger getFilteredCount() {
        return filteredCount;
    }

    /**
     * @return the extractedCount
     */
    public int getExtractedCount() {
        return extractedCount;
    }

    /**
     * @param extractedCount the extractedCount to set
     */
    public void setExtractedCount(int extractedCount) {
        this.extractedCount = extractedCount;
    }

    /**
     * @return the extractSucceedCount
     */
    public int getExtractSucceedCount() {
        return extractSucceedCount;
    }

    /**
     * @param extractSucceedCount the extractSucceedCount to set
     */
    public void setExtractSucceedCount(int extractSucceedCount) {
        this.extractSucceedCount = extractSucceedCount;
    }

    /**
     * @return the extractFailedCount
     */
    public int getExtractFailedCount() {
        return extractFailedCount;
    }

    /**
     * @param extractFailedCount the extractFailedCount to set
     */
    public void setExtractFailedCount(int extractFailedCount) {
        this.extractFailedCount = extractFailedCount;
    }

    /**
     * @return the storeFailedCount
     */
    public int getStoreFailedCount() {
        return storeFailedCount;
    }

    /**
     * @param storeFailedCount the storeFailedCount to set
     */
    public void setStoreFailedCount(int storeFailedCount) {
        this.storeFailedCount = storeFailedCount;
    }

    /**
     * @return the notExtractCount
     */
    public int getNotExtractCount() {
        return notExtractCount;
    }

    /**
     * @param notExtractCount the notExtractCount to set
     */
    public void setNotExtractCount(int notExtractCount) {
        this.notExtractCount = notExtractCount;
    }

    /**
     * @param filteredCount the filteredCount to set
     */
    public void setFilteredCount(AtomicInteger filteredCount) {
        this.filteredCount = filteredCount;
    }

    /**
     * @return the networkTraffic
     */
    public AtomicLong getNetworkTraffic() {
        return networkTraffic;
    }

    /**
     * @param networkTraffic the networkTraffic to set
     */
    public void setNetworkTraffic(AtomicLong networkTraffic) {
        this.networkTraffic = networkTraffic;
    }

    /**
     * @return the isDuplicateRemoved
     */
    public boolean isDuplicateRemoved() {
        return isDuplicateRemoved;
    }

    /**
     * @param isDuplicateRemoved the isDuplicateRemoved to set
     */
    public void setDuplicateRemoved(boolean isDuplicateRemoved) {
        this.isDuplicateRemoved = isDuplicateRemoved;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName the nodeName to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the resultMessage
     */
    public String getResultMessage() {
        return resultMessage;
    }

    /**
     * @param resultMessage the resultMessage to set
     */
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    /**
     * @return the startedAt
     */
    public Date getStartedAt() {
        return startedAt;
    }

    /**
     * @param startedAt the startedAt to set
     */
    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    /**
     * @return the finishedAt
     */
    public Date getFinishedAt() {
        return finishedAt;
    }

    /**
     * @param finishedAt the finishedAt to set
     */
    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
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
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.setErrorCode(errorCode, null);
    }

    public void setErrorCode(ErrorCode errorCode, String message) {
        synchronized (this) {
            if (status == 0 || errorCode.getErrorCode() < status) {
                this.setStatus(errorCode.getErrorCode());
                if (message != null) {
                    this.setRemark(message);
                } else {
                    this.setRemark(errorCode.getErrorMessage());
                }
            }
        }
    }

    /**
     * @return the taskId
     */
    public long getTaskId() {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public boolean isSubTask() {
        return isSubTask;
    }

    public void setSubTask(boolean subTask) {
        isSubTask = subTask;
    }
}