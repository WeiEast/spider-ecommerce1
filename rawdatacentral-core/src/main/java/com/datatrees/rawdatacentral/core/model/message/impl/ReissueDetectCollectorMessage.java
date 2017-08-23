/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.core.model.message.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.core.model.message.TaskRelated;
import com.datatrees.rawdatacentral.core.model.message.TemplteAble;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年1月18日 下午4:06:25
 */
public class ReissueDetectCollectorMessage extends CollectorMessage implements TemplteAble, TaskRelated {

    private Set<String> defaultResultTag = new HashSet<String>(Arrays.asList(PropertiesConfiguration.getInstance().get("reissue.detect.result.tag", "detectResults").split(",")));
    private int    parentTaskID;
    private String templateId;

    public Set<String> getResultTagSet() {
        return defaultResultTag;
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

}
