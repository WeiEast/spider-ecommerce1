package com.datatrees.rawdatacentral.domain.model2;

import java.io.Serializable;
import java.util.Date;

/** create by system from table t_website_conf(website config,sopport search) */
public class WebsiteConf implements Serializable {

    private static final long serialVersionUID = 1L;
    /**  */
    private Integer websiteConfId;
    /**  */
    private Integer websiteId;
    /** extractor config */
    private String  extractorConfig;
    /**  */
    private Date    createdAt;
    /**  */
    private Date    updatedAt;
    /** search config */
    private String  searchConfig;

    public Integer getWebsiteConfId() {
        return websiteConfId;
    }

    public void setWebsiteConfId(Integer websiteConfId) {
        this.websiteConfId = websiteConfId;
    }

    public Integer getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(Integer websiteId) {
        this.websiteId = websiteId;
    }

    public String getExtractorConfig() {
        return extractorConfig;
    }

    public void setExtractorConfig(String extractorConfig) {
        this.extractorConfig = extractorConfig == null ? null : extractorConfig.trim();
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

    public String getSearchConfig() {
        return searchConfig;
    }

    public void setSearchConfig(String searchConfig) {
        this.searchConfig = searchConfig == null ? null : searchConfig.trim();
    }
}