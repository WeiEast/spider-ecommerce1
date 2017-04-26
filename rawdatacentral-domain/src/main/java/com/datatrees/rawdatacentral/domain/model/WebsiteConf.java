/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.rawdatacentral.domain.model;

import java.io.Serializable;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年11月7日 下午4:30:49
 */
public class WebsiteConf implements Serializable {

    private String websiteName;
    private String websiteType;

    private String initSetting;
    private String loginTip;
    private String verifyTip;
    private int initTimeout;
    private int codeWaitTime;
    private int loginTimeout;
    private String resetType;// 重置类型：SMS|WEB
    private String smsTemplate;
    private String smsReceiver;
    private String resetURL;
    private String resetTip;

    /**
     * @return the initSetting
     */
    public String getInitSetting() {
        return initSetting;
    }

    /**
     * @param initSetting the initSetting to set
     */
    public void setInitSetting(String initSetting) {
        this.initSetting = initSetting;
    }

    /**
     * @return the loginTip
     */
    public String getLoginTip() {
        return loginTip;
    }

    /**
     * @param loginTip the loginTip to set
     */
    public void setLoginTip(String loginTip) {
        this.loginTip = loginTip;
    }

    /**
     * @return the verifyTip
     */
    public String getVerifyTip() {
        return verifyTip;
    }

    /**
     * @param verifyTip the verifyTip to set
     */
    public void setVerifyTip(String verifyTip) {
        this.verifyTip = verifyTip;
    }

    /**
     * @return the resetType
     */
    public String getResetType() {
        return resetType;
    }

    /**
     * @param resetType the resetType to set
     */
    public void setResetType(String resetType) {
        this.resetType = resetType;
    }

    /**
     * @return the smsTemplate
     */
    public String getSmsTemplate() {
        return smsTemplate;
    }

    /**
     * @param smsTemplate the smsTemplate to set
     */
    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    /**
     * @return the smsReceiver
     */
    public String getSmsReceiver() {
        return smsReceiver;
    }

    /**
     * @param smsReceiver the smsReceiver to set
     */
    public void setSmsReceiver(String smsReceiver) {
        this.smsReceiver = smsReceiver;
    }

    /**
     * @return the resetURL
     */
    public String getResetURL() {
        return resetURL;
    }

    /**
     * @param resetURL the resetURL to set
     */
    public void setResetURL(String resetURL) {
        this.resetURL = resetURL;
    }

    /**
     * @return the resetTip
     */
    public String getResetTip() {
        return resetTip;
    }

    /**
     * @param resetTip the resetTip to set
     */
    public void setResetTip(String resetTip) {
        this.resetTip = resetTip;
    }



    /**
     * @return the websiteName
     */
    public String getWebsiteName() {
        return websiteName;
    }

    /**
     * @param websiteName the websiteName to set
     */
    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    /**
     * @return the websiteType
     */
    public String getWebsiteType() {
        return websiteType;
    }

    /**
     * @param websiteType the websiteType to set
     */
    public void setWebsiteType(String websiteType) {
        this.websiteType = websiteType;
    }

    /**
     * @return the initTimeout
     */
    public int getInitTimeout() {
        return initTimeout;
    }

    /**
     * @param initTimeout the initTimeout to set
     */
    public void setInitTimeout(int initTimeout) {
        this.initTimeout = initTimeout;
    }

    /**
     * @return the codeWaitTime
     */
    public int getCodeWaitTime() {
        return codeWaitTime;
    }

    /**
     * @param codeWaitTime the codeWaitTime to set
     */
    public void setCodeWaitTime(int codeWaitTime) {
        this.codeWaitTime = codeWaitTime;
    }

    /**
     * @return the loginTimeout
     */
    public int getLoginTimeout() {
        return loginTimeout;
    }

    /**
     * @param loginTimeout the loginTimeout to set
     */
    public void setLoginTimeout(int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WebsiteConf [");
        if (websiteName != null) builder.append("websiteName=").append(websiteName).append(", ");
        if (websiteType != null) builder.append("websiteType=").append(websiteType).append(", ");
        if (initSetting != null) builder.append("initSetting=").append(initSetting).append(", ");
        if (loginTip != null) builder.append("loginTip=").append(loginTip).append(", ");
        if (verifyTip != null) builder.append("verifyTip=").append(verifyTip).append(", ");
        builder.append("initTimeout=").append(initTimeout).append(", codeWaitTime=").append(codeWaitTime).append(", loginTimeout=")
                .append(loginTimeout).append(", ");
        if (resetType != null) builder.append("resetType=").append(resetType).append(", ");
        if (smsTemplate != null) builder.append("smsTemplate=").append(smsTemplate).append(", ");
        if (smsReceiver != null) builder.append("smsReceiver=").append(smsReceiver).append(", ");
        if (resetURL != null) builder.append("resetURL=").append(resetURL).append(", ");
        if (resetTip != null) builder.append("resetTip=").append(resetTip);
        builder.append("]");
        return builder.toString();
    }

}
