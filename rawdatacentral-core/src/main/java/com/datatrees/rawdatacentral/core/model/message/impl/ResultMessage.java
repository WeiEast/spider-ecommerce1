/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model.message.impl;

import java.util.HashMap;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月15日 上午2:43:21
 */
public class ResultMessage extends HashMap<String, Object> {

    public final static String TASK_ID        = "taskId";

    public final static String WEBSITE_NAME   = "websiteName";

    public final static String STATUS         = "status";// "status":SUCCESS/FAIL/WAITING_FOR_SMS_VERIFY/WAITING_FOR_PICTURE_VERIFY,

    public final static String ISRESULT_EMPTY = "isResultEmpty";

    public final static String REMARK         = "remark";

    public final static String WEBSITE_TYPE   = "websiteType";

    public final static String LEVAL_1_STATUS = "level1Status";

    /**
     * @return the level1Status
     */
    public boolean isLevel1Status() {
        return (boolean) this.get(LEVAL_1_STATUS);
    }

    /**
     * @param level1Status the level1Status to set
     */
    public void setLevel1Status(boolean level1Status) {
        this.put(LEVAL_1_STATUS, level1Status);
    }

    public Long getTaskId() {
        return (Long) this.get(TASK_ID);
    }

    public void setTaskId(long taskId) {
        this.put(TASK_ID, taskId);
    }

    /**
     * @return the websiteName
     */
    public String getWebsiteName() {
        return (String) this.get(WEBSITE_NAME);
    }

    /**
     * @param websiteName the websiteName to set
     */
    public void setWebsiteName(String websiteName) {
        this.put(WEBSITE_NAME, websiteName);
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return (String) this.get(STATUS);
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.put(STATUS, status);
    }

    /**
     * @return the isResultEmpty
     */
    public boolean isResultEmpty() {
        return (boolean) this.get(ISRESULT_EMPTY);
    }

    /**
     * @param isResultEmpty the isResultEmpty to set
     */
    public void setResultEmpty(boolean isResultEmpty) {
        this.put(ISRESULT_EMPTY, isResultEmpty);
    }

    /**
     * @return the remark
     */
    public Object getRemark() {
        return this.get(REMARK);
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(Object remark) {
        this.put(REMARK, remark);
    }

    public Object getWebsiteType() {
        return this.get(WEBSITE_TYPE);
    }

    public void setWebsiteType(Object websiteType) {
        this.put(WEBSITE_TYPE, websiteType);
    }

}
