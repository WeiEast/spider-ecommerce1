package com.datatrees.spider.bank.domain.model;

import java.io.Serializable;
import java.util.Date;

/** create by system from table t_bank(Bank basic info) */
public class Bank implements Serializable {

    private static final long    serialVersionUID = 1L;

    /** bank Id */
    private              Integer bankId;

    /** not null if bank support search */
    private              Integer websiteId;

    private              String  websiteName;

    /**  */
    private              String  bankMark;

    /** bank name */
    private              String  bankName;

    /**  */
    private              Short   orderIndex;

    /**  */
    private              String  matchText;

    /** 0:false,1:true */
    private              Boolean isenabled;

    /**  */
    private              Date    createdAt;

    /**  */
    private              Date    updatedAt;

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public Integer getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(Integer websiteId) {
        this.websiteId = websiteId;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getBankMark() {
        return bankMark;
    }

    public void setBankMark(String bankMark) {
        this.bankMark = bankMark == null ? null : bankMark.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getMatchText() {
        return matchText;
    }

    public void setMatchText(String matchText) {
        this.matchText = matchText == null ? null : matchText.trim();
    }

    public Boolean getIsenabled() {
        return isenabled;
    }

    public void setIsenabled(Boolean isenabled) {
        this.isenabled = isenabled;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}