package com.datatrees.spider.share.domain.model;

import java.io.Serializable;
import java.util.Date;

/** create by system from table t_ecommerce(ecommerce basic info) */
public class Ecommerce implements Serializable {

    private static final long    serialVersionUID = 1L;

    /** bank Id */
    private              Integer id;

    /** not null if ecommerce support search */
    private              Integer websiteId;

    /** ecommerce name */
    private              String  ecommerceName;

    /** 0:false,1:true */
    private              Boolean isenabled;

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

    public Integer getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(Integer websiteId) {
        this.websiteId = websiteId;
    }

    public String getEcommerceName() {
        return ecommerceName;
    }

    public void setEcommerceName(String ecommerceName) {
        this.ecommerceName = ecommerceName == null ? null : ecommerceName.trim();
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