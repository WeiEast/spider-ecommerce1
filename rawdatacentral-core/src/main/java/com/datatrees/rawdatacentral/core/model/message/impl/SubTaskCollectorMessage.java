/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model.message.impl;

import com.datatrees.rawdatacentral.core.model.message.SubTaskAble;
import com.datatrees.rawdatacentral.core.model.subtask.SubSeed;
import org.apache.commons.lang.BooleanUtils;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午4:14:22
 */
public class SubTaskCollectorMessage extends CollectorMessage implements SubTaskAble {

    private String  templateId;
    private int     parentTaskID;
    private boolean synced;
    private SubSeed subSeed;

    /**
     * @return the subSeed
     */
    public SubSeed getSubSeed() {
        return subSeed;
    }

    /**
     * @param subSeed the subSeed to set
     */
    public void setSubSeed(SubSeed subSeed) {
        this.subSeed = subSeed;
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
     * @return the synced
     */
    public boolean isSynced() {
        return synced;
    }

    /**
     * @param synced the synced to set
     */
    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    /*
     * (non-Javadoc)
     * 
     * @see SubTaskAble#noStatusSend()
     */
    @Override
    public boolean noStatus() {
        return subSeed != null && BooleanUtils.isTrue(subSeed.noStatus());
    }

}
