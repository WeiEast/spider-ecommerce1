package com.datatrees.rawdatacentral.domain.model;

import java.io.Serializable;
import java.util.Date;

/**
 * User: yand
 * Date: 2018/3/28
 */
public class AppCrawlerConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 爬取业务类型 电商/运营商
     */
    private Integer websiteType;
    /**
     * 商户标识
     */
    private String  appId;
    /**
     * 爬取业务标识 {BusinessType}
     */
    private String  project;
    /**
     * 是否爬取
     */
    private Boolean isCrawler;
    private Date    createdAt;
    private Date    updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWebsiteType() {
        return websiteType;
    }

    public void setWebsiteType(Integer websiteType) {
        this.websiteType = websiteType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Boolean getCrawler() {
        return isCrawler;
    }

    public void setCrawler(Boolean crawler) {
        isCrawler = crawler;
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
