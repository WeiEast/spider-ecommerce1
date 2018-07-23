/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model.message.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.datatrees.rawdatacentral.core.model.message.MessageInfo;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午4:14:22
 */
public class CollectorMessage extends MessageInfo {

    private transient String              cookie;

    private           String              websiteName;

    private           long                taskId;

    private           String              accountNo;

    private           String              serialNum;

    private           String              endURL;

    private           boolean             needDuplicate;

    private           boolean             level1Status;                            // 标识本网站是否需要发送一级状态

    private           boolean             loginCheckIgnore;

    private           Map<String, Object> property = new HashMap<String, Object>();

    private           Map<String, Object> sendBack = new HashMap<String, Object>();

    private           boolean             finish;

    /** 分组代码 */
    private           String              groupCode;

    /** 分组名称 */
    private           String              groupName;

    /**
     * 总共运行次数
     */
    private           long                totalRun = 0;

    public long getTotalRun() {
        return totalRun;
    }

    public void setTotalRun(long totalRun) {
        this.totalRun = totalRun;
    }

    public Set<String> getResultTagSet() {
        return null;
    }

    /**
     * @return the loginCheckIgnore
     */
    public boolean isLoginCheckIgnore() {
        return loginCheckIgnore;
    }

    /**
     * @param loginCheckIgnore the loginCheckIgnore to set
     */
    public void setLoginCheckIgnore(boolean loginCheckIgnore) {
        this.loginCheckIgnore = loginCheckIgnore;
    }

    /**
     * @return the finish
     */
    public boolean isFinish() {
        return finish;
    }

    /**
     * @param finish the finish to set
     */
    public void setFinish(boolean finish) {
        this.finish = finish;
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
     * @return the level1Status
     */
    public boolean isLevel1Status() {
        return level1Status;
    }

    /**
     * @param level1Status the level1Status to set
     */
    public void setLevel1Status(boolean level1Status) {
        this.level1Status = level1Status;
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
     * @return the serialNum
     */
    public String getSerialNum() {
        return serialNum;
    }

    /**
     * @param serialNum the serialNum to set
     */
    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    /**
     * @return the endURL
     */
    public String getEndURL() {
        return endURL;
    }

    /**
     * @param endURL the endURL to set
     */
    public void setEndURL(String endURL) {
        this.endURL = endURL;
    }

    /**
     * @return the needDuplicate
     */
    public boolean isNeedDuplicate() {
        return needDuplicate;
    }

    /**
     * @param needDuplicate the needDuplicate to set
     */
    public void setNeedDuplicate(boolean needDuplicate) {
        this.needDuplicate = needDuplicate;
    }

    /**
     * @return the sendBack
     */
    public Map<String, Object> getSendBack() {
        return sendBack;
    }

    public void setSendBack(Map<String, Object> sendBack) {
        this.sendBack = sendBack;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "CollectorMessage [ websiteName=" + websiteName + ", taskId=" + taskId + ", serialNum=" + serialNum + ", endURL=" + endURL +
                ", cookie=" + cookie + "]";
    }

    /**
     * @return the accountNo
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * @param accountNo the accountNo to set
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

}
