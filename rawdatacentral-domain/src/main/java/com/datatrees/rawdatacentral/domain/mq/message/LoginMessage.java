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
}
