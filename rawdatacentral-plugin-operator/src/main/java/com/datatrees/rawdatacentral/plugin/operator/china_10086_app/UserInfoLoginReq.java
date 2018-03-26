package com.datatrees.rawdatacentral.plugin.operator.china_10086_app;

import java.io.Serializable;

public class UserInfoLoginReq implements Serializable {
    private String appurltype;
    private String artifact;
    private String backUrl;
    private String ccPasswd;
    private String cellNum;
    private String imei;
    private String imsi;
    private String sendSmsFlag;
    private String sysTime;
    private String verifyCode;
    private String pageFlag;

    public String getPageFlag() {
		return pageFlag;
	}

	public void setPageFlag(String pageFlag) {
		this.pageFlag = pageFlag;
	}

	public String getSysTime() {
        return this.sysTime;
    }

    public void setSysTime(String sysTime) {
        this.sysTime = sysTime;
    }

    public String getCellNum() {
        return this.cellNum;
    }

    public void setCellNum(String cellNum) {
        this.cellNum = cellNum;
    }

    public String getCcPasswd() {
        return this.ccPasswd;
    }

    public void setCcPasswd(String ccPasswd) {
        this.ccPasswd = ccPasswd;
    }

    public String getSendSmsFlag() {
        return this.sendSmsFlag;
    }

    public void setSendSmsFlag(String sendSmsFlag) {
        this.sendSmsFlag = sendSmsFlag;
    }

    public String getVerifyCode() {
        return this.verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getBackUrl() {
        return this.backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getArtifact() {
        return this.artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getAppurltype() {
        return this.appurltype;
    }

    public void setAppurltype(String appurltype) {
        this.appurltype = appurltype;
    }

    public String getImei() {
        return this.imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return this.imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
}
