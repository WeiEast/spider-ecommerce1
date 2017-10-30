package com.datatrees.rawdatacentral.domain.mq.message;

import java.io.Serializable;

/**
 * 登陆成功消息体
 * Created by zhouxinghai on 2017/5/2.
 */
public class LoginMessage implements Serializable {

    /**
     * 站点名
     */
    private String websiteName;
    /**
     * 任务ID
     */
    private long   taskId;
    /**
     * 结束url
     */
    private String endUrl;
    /**
     * head配置:cookie
     */
    private String cookie;
    /**
     * head配置项:Set-Cookie
     */
    private String setCookie;
    /**
     * 用户账户
     */
    private String accountNo;
    /** 分组代码 */
    private String groupCode;
    /** 分组名称 */
    private String groupName;

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getEndUrl() {
        return endUrl;
    }

    public void setEndUrl(String endUrl) {
        this.endUrl = endUrl;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getSetCookie() {
        return setCookie;
    }

    public void setSetCookie(String setCookie) {
        this.setCookie = setCookie;
    }

    /**
     * @return the accountNo
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * @param accountNo the accountNo to set
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

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

    @Override
    public String toString() {
        return "LoginMessage [websiteName=" + websiteName + ", taskId=" + taskId + ", endUrl=" + endUrl + ", accountNo=" + accountNo + "]";
    }
}
