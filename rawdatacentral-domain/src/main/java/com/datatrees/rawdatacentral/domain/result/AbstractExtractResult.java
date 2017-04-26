/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.domain.result;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.rawdatacentral.domain.enums.ExtractCode;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月27日 下午2:55:43
 */
public abstract class AbstractExtractResult implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(AbstractExtractResult.class);

    /**
     *
     */
    private static final long serialVersionUID = 3890382241592488877L;
    private int id;
    private int taskId;
    private int websiteId;
    private String uniqueSign;
    private String uniqueMd5;
    private int status;
    private String remark;
    private String storagePath;
    private String resultType;
    private String url;
    private String pageExtractId;

    private long duration;

    private Map<String, Object> extraInfo;

    
    /**
     * @return the extraInfo
     */
    public Map<String, Object> getExtraInfo() {
        return extraInfo;
    }

    /**
     * @param extraInfo the extraInfo to set
     */
    public void setExtraInfo(Map<String, Object> extraInfo) {
        this.extraInfo = extraInfo;
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

    /**
     * @return the pageExtractId
     */
    public String getPageExtractId() {
        return pageExtractId;
    }

    /**
     * @param pageExtractId the pageExtractId to set
     */
    public void setPageExtractId(String pageExtractId) {
        this.pageExtractId = pageExtractId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
     * @return the uniqueSign
     */
    public String getUniqueSign() {
        return uniqueSign;
    }

    /**
     * @param uniqueSign the uniqueSign to set
     */
    public void setUniqueSign(String uniqueSign) {
        this.uniqueSign = uniqueSign;
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

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the storagePath
     */
    public String getStoragePath() {
        return storagePath;
    }

    /**
     * @param storagePath the storagePath to set
     */
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    /**
     * @return the resultType
     */
    public String getResultType() {
        return resultType;
    }

    /**
     * @param resultType the resultType to set
     */
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    /**
     * @return the uniqueMd5
     */
    public String getUniqueMd5() {
        return uniqueMd5;
    }

    /**
     * @param uniqueMd5 the uniqueMd5 to set
     */
    public void setUniqueMd5(String uniqueMd5) {
        this.uniqueMd5 = uniqueMd5;
    }

    public void setExtractCode(ExtractCode errorCode) {
        this.setExtractCode(errorCode, null);
    }

    public void setExtractCode(ExtractCode errorCode, String message) {
        if (status == 0 || errorCode.getCode() < status) {
            this.setStatus(errorCode.getCode());
            if (message != null) {
                this.setRemark(message);
            } else {
                this.setRemark(errorCode.getDesc());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ExtractResult [taskId=" + taskId + ", websiteId=" + websiteId + ", uniqueSign=" + uniqueSign + ", uniqueMd5="
                + uniqueMd5 + "]";
    }

}
