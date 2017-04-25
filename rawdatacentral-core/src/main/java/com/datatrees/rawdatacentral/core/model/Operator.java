/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.model;

import java.io.Serializable;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月27日 下午2:03:52
 */
public class Operator implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7359174255855841993L;
    private int id;
    private int websiteId;
    private String operatorName;
    private String region;
    private boolean isEnabled;

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
     * @return the operatorName
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * @param operatorName the operatorName to set
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * @return the isEnabled
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * @param isEnabled the isEnabled to set
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

}
