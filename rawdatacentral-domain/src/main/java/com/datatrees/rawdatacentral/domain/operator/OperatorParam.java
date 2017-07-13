package com.datatrees.rawdatacentral.domain.operator;

import java.io.Serializable;

/**
 * 运营商登陆过程中的参数
 * Created by zhouxinghai on 2017/7/13.
 */
public class OperatorParam implements Serializable {

    /**
     * 任务id 必填
     */
    private Long   taskId;

    /**
     * website name 必填
     */
    private String websiteName;

    /**
     * 手机号
     */
    private Long   mobile;

    /**
     * 登陆密码或者服务密码
     */
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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getWebsiteName() {
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
}
