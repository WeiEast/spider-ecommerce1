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

package com.datatrees.spider.operator.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 运营商登陆配置
 * @author zhouxinghai
 * @date 2018/4/18
 */
public class OperatorLoginConfig implements Serializable {

    /**
     * 任务Id
     */
    private long             taskId;

    /**
     * 手机号
     */
    private long             mobile;

    /**
     * 运营商类别代码
     */
    private String           groupCode;

    /**
     * 运营商类别名称
     */
    private String           groupName;

    /**
     * 默认站点名称
     */
    private String           websiteName;

    /**
     * 跳转url
     */
    private String           gotoUrl;

    /**
     * 是否有图片验证码
     */
    private Boolean          hasPicCode = false;

    /**
     * 是否有短信验证码
     */
    private Boolean          hasSmsCode = false;

    /**
     * 登陆页面温馨提示
     */
    private String           loginTip;

    /**
     * 爬取过程中如果出现短信验证码框,通话详单,个人信息有
     */
    private String           verifyTip;

    /**
     * 忘记密码方式:SMS,TEL,WEB
     */
    private String           resetType;

    /**
     * 重置密码短信模板
     */
    private String           smsTemplate;

    /**
     * 重置密码短信短信收件人或者拨号重置密码电话号码
     */
    private String           smsReceiver;

    /**
     * web方式,重置密码的页面url
     */
    private String           resetURL;

    /**
     * 点击忘记密码提示
     */
    private String           resetTip;

    /**
     * 启用/停用
     */
    private Boolean          enable     = true;

    /**
     * 字段
     */
    private List<InputField> fields     = new ArrayList<>();

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getGotoUrl() {
        return gotoUrl;
    }

    public void setGotoUrl(String gotoUrl) {
        this.gotoUrl = gotoUrl;
    }

    public Boolean getHasPicCode() {
        return hasPicCode;
    }

    public void setHasPicCode(Boolean hasPicCode) {
        this.hasPicCode = hasPicCode;
    }

    public Boolean getHasSmsCode() {
        return hasSmsCode;
    }

    public void setHasSmsCode(Boolean hasSmsCode) {
        this.hasSmsCode = hasSmsCode;
    }

    public String getLoginTip() {
        return loginTip;
    }

    public void setLoginTip(String loginTip) {
        this.loginTip = loginTip;
    }

    public String getVerifyTip() {
        return verifyTip;
    }

    public void setVerifyTip(String verifyTip) {
        this.verifyTip = verifyTip;
    }

    public String getResetType() {
        return resetType;
    }

    public void setResetType(String resetType) {
        this.resetType = resetType;
    }

    public String getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    public String getSmsReceiver() {
        return smsReceiver;
    }

    public void setSmsReceiver(String smsReceiver) {
        this.smsReceiver = smsReceiver;
    }

    public String getResetURL() {
        return resetURL;
    }

    public void setResetURL(String resetURL) {
        this.resetURL = resetURL;
    }

    public String getResetTip() {
        return resetTip;
    }

    public void setResetTip(String resetTip) {
        this.resetTip = resetTip;
    }

    public List<InputField> getFields() {
        return fields;
    }

    public void setFields(List<InputField> fields) {
        this.fields = fields;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
