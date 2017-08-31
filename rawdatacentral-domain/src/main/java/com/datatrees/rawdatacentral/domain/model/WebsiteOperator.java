package com.datatrees.rawdatacentral.domain.model;

import java.io.Serializable;
import java.util.Date;

 /** create by system from table website_operator(运营商配置)  */
public class WebsiteOperator implements Serializable {
    /** 主键 */
    private Integer websiteId;

    /** 配置名称 */
    private String websiteName;

    /** 配置标题 */
    private String websiteTitle;

    /** 搜索配置 */
    private String searchConfig;

    /** 解析配置 */
    private String extractorConfig;

    /** 分组 */
    private String groupCode;

    /** 运营商类型:10086,10010,10000 */
    private String operatorType;

    /** 区域 */
    private String regionName;

    /** 修改时间 */
    private Date updatedAt;

    /** 插件启动阶段(在***表单提交成功之后发送登录成功消息) */
    private String startStage;

    /** 登录地址 */
    private String loginUrl;

    /** 插件地址 */
    private String pluginClass;

    /** 登录配置 */
    private String loginConfig;

    /** 短信重发间隔时间(全局) */
    private Integer smsInterval;

    /** 描述 */
    private String remark;

    /** 登录过程中自定义提示 */
    private String loginTip;

    /** 爬过过程中校验,短信输入框提示 */
    private String verifyTip;

    /** 重置密码方式(SMS:短信, TEL:拨打电话, WEB:url重置) */
    private String resetType;

    /** 重置密码短信模板 */
    private String smsTemplate;

    /** 重置密码接收手机号 */
    private String smsReceiver;

    /** 重置密码url */
    private String resetUrl;

    /** 重置密码提示 */
    private String resetTip;

    /**  */
    private Boolean simulate;

    private static final long serialVersionUID = 1L;

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
        this.websiteName = websiteName == null ? null : websiteName.trim();
    }

    public String getWebsiteTitle() {
        return websiteTitle;
    }

    public void setWebsiteTitle(String websiteTitle) {
        this.websiteTitle = websiteTitle == null ? null : websiteTitle.trim();
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

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode == null ? null : groupCode.trim();
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType == null ? null : operatorType.trim();
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName == null ? null : regionName.trim();
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

    public Boolean getSimulate() {
        return simulate;
    }

    public void setSimulate(Boolean simulate) {
        this.simulate = simulate;
    }
}