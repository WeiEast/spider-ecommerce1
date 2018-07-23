/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.domain.model;

import java.io.Serializable;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年11月7日 下午4:30:49
 */
public class WebsiteConf implements Serializable {

    /**
     *
     */
    private static final long    serialVersionUID = -4488630632381744273L;

    private              Boolean simulate;

    private              String  websiteName;

    private              String  websiteType;

    private              String  name;

    private              String  initSetting;

    private              String  loginTip;

    private              String  verifyTip;

    private              String  resetType;// 重置类型：SMS|WEB

    private              String  smsTemplate;

    private              String  smsReceiver;

    private              String  resetURL;

    private              String  resetTip;

    public Boolean getSimulate() {
        return simulate;
    }

    public void setSimulate(Boolean simulate) {
        this.simulate = simulate;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WebsiteConf{");
        sb.append("simulate=").append(simulate);
        sb.append(", websiteName='").append(websiteName).append('\'');
        sb.append(", websiteType='").append(websiteType).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", initSetting='").append(initSetting).append('\'');
        sb.append(", loginTip='").append(loginTip).append('\'');
        sb.append(", verifyTip='").append(verifyTip).append('\'');
        sb.append(", resetType='").append(resetType).append('\'');
        sb.append(", smsTemplate='").append(smsTemplate).append('\'');
        sb.append(", smsReceiver='").append(smsReceiver).append('\'');
        sb.append(", resetURL='").append(resetURL).append('\'');
        sb.append(", resetTip='").append(resetTip).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
