/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.operator.plugin.china_10086_app.bean;

import java.io.Serializable;

/**
 * 查询个人信息相关 reqBody对应的Bean
 * Created by guimeichao on 18/3/28.
 */
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
