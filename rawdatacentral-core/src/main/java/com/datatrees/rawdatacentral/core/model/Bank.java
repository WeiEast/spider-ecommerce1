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
 * @since 2015年7月26日 下午1:41:29
 */
public class Bank implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 9126330761266448145L;
    private int id;
    private int websiteId;
    private String bankName;
    private String bankEmailAddr;
    private boolean isEnabled;
    private int orderIndex;
    private String matchText;

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
     * @return the bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * @param bankName the bankName to set
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * @return the bankEmailAddr
     */
    public String getBankEmailAddr() {
        return bankEmailAddr;
    }

    /**
     * @param bankEmailAddr the bankEmailAddr to set
     */
    public void setBankEmailAddr(String bankEmailAddr) {
        this.bankEmailAddr = bankEmailAddr;
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
