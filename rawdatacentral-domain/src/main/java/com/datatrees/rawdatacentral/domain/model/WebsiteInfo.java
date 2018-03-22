package com.datatrees.rawdatacentral.domain.model;

import java.io.Serializable;
import java.util.Date;

public class WebsiteInfo implements Serializable{
    private Integer websiteId;

    private String env;

    private Integer websiteType;

    private String websiteName;

    private String websiteTitle;

    private Boolean enable;

    private Boolean proxyEnable;

    private String groupCode;

    private Date updatedAt;

    private String startStage;

    private String loginUrl;

    private String pluginClass;

    private String loginConfig;

    private Integer smsInterval;

    private String remark;

    private String loginTip;

    private String verifyTip;

    private String resetType;

    private String smsTemplate;

    private String smsReceiver;

    private String resetUrl;

    private String resetTip;

    private Date createdAt;

    private String attribute;

    private String searchConfig;

    private String extractorConfig;

    public Integer getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(Integer websiteId) {
        this.websiteId = websiteId;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env == null ? null : env.trim();
    }

    public Integer getWebsiteType() {
        return websiteType;
    }

    public void setWebsiteType(Integer websiteType) {
        this.websiteType = websiteType;
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

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getProxyEnable() {
        return proxyEnable;
    }

    public void setProxyEnable(Boolean proxyEnable) {
        this.proxyEnable = proxyEnable;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode == null ? null : groupCode.trim();
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStartStage() {
        return startStage;
    }

    public void setStartStage(String startStage) {
        this.startStage = startStage == null ? null : startStage.trim();
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl == null ? null : loginUrl.trim();
    }

    public String getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass == null ? null : pluginClass.trim();
    }

    public String getLoginConfig() {
        return loginConfig;
    }

    public void setLoginConfig(String loginConfig) {
        this.loginConfig = loginConfig == null ? null : loginConfig.trim();
    }

    public Integer getSmsInterval() {
        return smsInterval;
    }

    public void setSmsInterval(Integer smsInterval) {
        this.smsInterval = smsInterval;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getLoginTip() {
        return loginTip;
    }

    public void setLoginTip(String loginTip) {
        this.loginTip = loginTip == null ? null : loginTip.trim();
    }

    public String getVerifyTip() {
        return verifyTip;
    }

    public void setVerifyTip(String verifyTip) {
        this.verifyTip = verifyTip == null ? null : verifyTip.trim();
    }

    public String getResetType() {
        return resetType;
    }

    public void setResetType(String resetType) {
        this.resetType = resetType == null ? null : resetType.trim();
    }

    public String getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate == null ? null : smsTemplate.trim();
    }

    public String getSmsReceiver() {
        return smsReceiver;
    }

    public void setSmsReceiver(String smsReceiver) {
        this.smsReceiver = smsReceiver == null ? null : smsReceiver.trim();
    }

    public String getResetUrl() {
        return resetUrl;
    }

    public void setResetUrl(String resetUrl) {
        this.resetUrl = resetUrl == null ? null : resetUrl.trim();
    }

    public String getResetTip() {
        return resetTip;
    }

    public void setResetTip(String resetTip) {
        this.resetTip = resetTip == null ? null : resetTip.trim();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute == null ? null : attribute.trim();
    }

    public String getSearchConfig() {
        return searchConfig;
    }

    public void setSearchConfig(String searchConfig) {
        this.searchConfig = searchConfig == null ? null : searchConfig.trim();
    }

    public String getExtractorConfig() {
        return extractorConfig;
    }

    public void setExtractorConfig(String extractorConfig) {
        this.extractorConfig = extractorConfig == null ? null : extractorConfig.trim();
    }
}