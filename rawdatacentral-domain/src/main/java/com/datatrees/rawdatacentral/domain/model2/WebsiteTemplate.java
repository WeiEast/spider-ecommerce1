package com.datatrees.rawdatacentral.domain.model2;

import java.io.Serializable;
import java.util.Date;

 /** create by system from table t_website_template()  */
public class WebsiteTemplate implements Serializable {
    /** bank Id */
    private Integer templateId;

    /**  */
    private String name;

    /**  */
    private Date createdAt;

    /**  */
    private Date updatedAt;

    /**  */
    private String initSetting;

    private static final long serialVersionUID = 1L;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public String getInitSetting() {
        return initSetting;
    }

    public void setInitSetting(String initSetting) {
        this.initSetting = initSetting == null ? null : initSetting.trim();
    }
}