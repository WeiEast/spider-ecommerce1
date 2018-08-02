package com.datatrees.spider.bank.domain.model;

import java.io.Serializable;
import java.util.Date;

/** create by system from table t_bank_email(Bank email info) */
public class BankMail implements Serializable {

    private static final long    serialVersionUID = 1L;

    /** bank Id */
    private              Integer id;

    /** bank Id */
    private              Integer bankId;

    /**  */
    private              String  bankEmailAddr;

    /**  */
    private              Date    createdAt;

    /**  */
    private              Date    updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getBankEmailAddr() {
        return bankEmailAddr;
    }

    public void setBankEmailAddr(String bankEmailAddr) {
        this.bankEmailAddr = bankEmailAddr == null ? null : bankEmailAddr.trim();
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