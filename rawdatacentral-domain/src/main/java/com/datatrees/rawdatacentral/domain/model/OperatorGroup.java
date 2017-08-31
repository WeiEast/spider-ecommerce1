package com.datatrees.rawdatacentral.domain.model;

import java.io.Serializable;
import java.util.Date;

 /** create by system from table operator_group(运营商分组)  */
public class OperatorGroup implements Serializable {
    /** 分组代码 */
    private String groupCode;

    /** 分组名称 */
    private String groupName;

    /** 配置名称 */
    private String websiteName;

    /** 配置标题 */
    private String websiteTitle;

    /** 权重(0:不启用,权重越高,比重越大) */
    private Integer weight;

    /** 修改时间 */
    private Date updatedAt;

    private static final long serialVersionUID = 1L;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode == null ? null : groupCode.trim();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName == null ? null : websiteName.trim();
    }

    public String getWebsiteTitle() {
        return websiteTitle;
    }

    public void setWebsiteTitle(String websiteTitle) {
        this.websiteTitle = websiteTitle == null ? null : websiteTitle.trim();
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}