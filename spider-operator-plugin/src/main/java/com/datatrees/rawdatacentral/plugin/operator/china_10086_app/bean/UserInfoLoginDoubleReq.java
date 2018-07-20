package com.datatrees.rawdatacentral.plugin.operator.china_10086_app.bean;

import java.io.Serializable;

/**
 * 详单校验 reqBody对应的Bean
 * Created by guimeichao on 18/3/28.
 */
public class UserInfoLoginDoubleReq implements Serializable {
    private String businessCode;
    private String cellNum;
    private String imei;
    private String imsi;
    private String passwd;
    private String smsPasswd;

    public String getBusinessCode() {
        return this.businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getCellNum() {
        return this.cellNum;
    }

    public void setCellNum(String cellNum) {
        this.cellNum = cellNum;
    }

    public String getPasswd() {
        return this.passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSmsPasswd() {
        return this.smsPasswd;
    }

    public void setSmsPasswd(String smsPasswd) {
        this.smsPasswd = smsPasswd;
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
