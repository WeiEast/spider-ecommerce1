package com.datatrees.rawdatacentral.domain.model2;

import java.io.Serializable;
import java.util.Date;

/** create by system from table t_website(website basic info) */
public class Website implements Serializable {

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
        this.websiteType = websiteType == null ? null : websiteType.trim();
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName == null ? null : websiteName.trim();
    }

    public String getWebsiteDomain() {
        return websiteDomain;
    }

    public void setWebsiteDomain(String websiteDomain) {
        this.websiteDomain = websiteDomain == null ? null : websiteDomain.trim();
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
        this.loginTip = loginTip == null ? null : loginTip.trim();
    }

    public String getVerifyTip() {
        return verifyTip;
    }

    public void setVerifyTip(String verifyTip) {
        this.verifyTip = verifyTip == null ? null : verifyTip.trim();
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

    public String getResetURL() {
        return resetURL;
    }

    public void setResetURL(String resetURL) {
        this.resetURL = resetURL == null ? null : resetURL.trim();
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
}