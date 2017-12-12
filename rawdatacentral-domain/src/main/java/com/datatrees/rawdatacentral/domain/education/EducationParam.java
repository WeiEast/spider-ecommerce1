package com.datatrees.rawdatacentral.domain.education;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by zhangyanjia on 2017/11/30.
 */
public class EducationParam implements Serializable {
    /**
     * 任务id 必填
     */
    private Long taskId;
    /**
     * website name 必填
     */
    private String websiteName;
    /**
     * 手机号
     */
    private Long mobile;

    /**
     * 登录用户名参数
     */

    private String loginName;
    /**
     * 登陆密码
     */
    @JSONField(serialize = false)
    private String password;
    /**
     * 图片验证码
     */
    private String picCode;
    /**
     * 短信验证码
     */
    private String smsCode;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 身份证号码
     */
    private String idCard;
    /**
     * 身份证类型
     */
    private String idCardType;

    /**
     * 注册输入密码
     */
    private String pwd;

    /**
     * 注册确认密码
     */
    private String surePwd;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getWebsiteName() {
        if (websiteName == null) {
            websiteName = "chsi.com.cn";
        }
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicCode() {
        return picCode;
    }

    public void setPicCode(String picCode) {
        this.picCode = picCode;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(String idCardType) {
        this.idCardType = idCardType;
    }

    public String getSurePwd() {
        return surePwd;
    }

    public void setSurePwd(String surePwd) {
        this.surePwd = surePwd;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
