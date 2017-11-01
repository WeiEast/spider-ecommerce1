package com.datatrees.rawdatacentral.domain.operator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.datatrees.rawdatacentral.domain.constant.FormType;

/**
 * 运营商登陆过程中的参数
 * Created by zhouxinghai on 2017/7/13.
 */
public class OperatorParam implements Serializable {

    /**
     * 表单类型,LOGIN:登陆,详单VALIDATE_BILL_DETAIL:验证通话记录,VALIDATE_USER_INFO:验证个人信息
     */
    private String formType;
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
     * 扩展属性
     */
    private Map<String, Object> extral = new HashMap<>();
    /**
     * 给自定义plugin方法用
     */
    private String[] args;

    public OperatorParam() {
    }

    public OperatorParam(String formType, Long taskId, String websiteName) {
        this.formType = formType;
        this.taskId = taskId;
        this.websiteName = websiteName;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

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

    public Map<String, Object> getExtral() {
        return extral;
    }

    public void setExtral(Map<String, Object> extral) {
        this.extral = extral;
    }

    public String getActionName() {
        return FormType.getName(formType);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
