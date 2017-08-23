/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.domain.vo;

import java.util.Date;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年11月7日 下午3:54:23
 */
public class WebsiteConfig {

    private static final long serialVersionUID = 1L;
    /**  */
    private Integer websiteId;
    /** 1:mail,2:operator,3:ecommerce，4:bank,5:internal */
    private String  websiteType;
    /** website name */
    private String  websiteName;
    /** website domain */
    private String  websiteDomain;
    /** 0:false,1:true */
    private Boolean isenabled;
    /** 登录过程中自定义提示 */
    private String  loginTip;
    /** 验证码提示 */
    private String  verifyTip;
    /** 登录页初始化超时 */
    private Integer initTimeout;
    /** 验证码等待时间 */
    private Integer codeWaitTime;
    /** 登录超时 */
    private Integer loginTimeout;
    /** 重置类型：SMS|WEB */
    private String  resetType;
    /** 短信模板 */
    private String  smsTemplate;
    /** 短信接收方 */
    private String  smsReceiver;
    /** 网页方式重置的url */
    private String  resetURL;
    /** 密码重置提示 */
    private String  resetTip;
    /**  */
    private Date    createdAt;
    /**  */
    private Date    updatedAt;
    /**  */
    private Boolean simulate;
    /**  */
    private Integer templateId;
    /**  */
    private Integer websiteConfId;
    /** extractor config */
    private String  extractorConfig;
    /** search config */
    private String  searchConfig;
    /**  */
    private String  initSetting;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(Integer websiteId) {
        this.websiteId = websiteId;
    }

    public String getWebsiteType() {
        return websiteType;
    }

    public void setWebsiteType(String websiteType) {
        this.websiteType = websiteType;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getWebsiteDomain() {
        return websiteDomain;
    }

    public void setWebsiteDomain(String websiteDomain) {
        this.websiteDomain = websiteDomain;
    }

    public Boolean getIsenabled() {
        return isenabled;
    }

    public void setIsenabled(Boolean isenabled) {
        this.isenabled = isenabled;
    }

    public String getLoginTip() {
        return loginTip;
    }

    public void setLoginTip(String loginTip) {
        this.loginTip = loginTip;
    }

    public String getVerifyTip() {
        return verifyTip;
    }

    public void setVerifyTip(String verifyTip) {
        this.verifyTip = verifyTip;
    }

    public Integer getInitTimeout() {
        return initTimeout;
    }

    public void setInitTimeout(Integer initTimeout) {
        this.initTimeout = initTimeout;
    }

    public Integer getCodeWaitTime() {
        return codeWaitTime;
    }

    public void setCodeWaitTime(Integer codeWaitTime) {
        this.codeWaitTime = codeWaitTime;
    }

    public Integer getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(Integer loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public String getResetType() {
        return resetType;
    }

    public void setResetType(String resetType) {
        this.resetType = resetType;
    }

    public String getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    public String getSmsReceiver() {
        return smsReceiver;
    }

    public void setSmsReceiver(String smsReceiver) {
        this.smsReceiver = smsReceiver;
    }

    public String getResetURL() {
        return resetURL;
    }

    public void setResetURL(String resetURL) {
        this.resetURL = resetURL;
    }

    public String getResetTip() {
        return resetTip;
    }

    public void setResetTip(String resetTip) {
        this.resetTip = resetTip;
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

    public Boolean getSimulate() {
        return simulate;
    }

    public void setSimulate(Boolean simulate) {
        this.simulate = simulate;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Integer getWebsiteConfId() {
        return websiteConfId;
    }

    public void setWebsiteConfId(Integer websiteConfId) {
        this.websiteConfId = websiteConfId;
    }

    public String getExtractorConfig() {
        return extractorConfig;
    }

    public void setExtractorConfig(String extractorConfig) {
        this.extractorConfig = extractorConfig;
    }

    public String getSearchConfig() {
        return searchConfig;
    }

    public void setSearchConfig(String searchConfig) {
        this.searchConfig = searchConfig;
    }

    public String getInitSetting() {
        return initSetting;
    }

    public void setInitSetting(String initSetting) {
        this.initSetting = initSetting;
    }
}

