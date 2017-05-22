package com.datatrees.rawdatacentral.domain.result;

import java.io.Serializable;

/**
 * redis交互指令
 * Created by zhouxinghai on 2017/5/19.
 */
public class DirectiveResult<T> implements Serializable {

    /**
     * 应用名称
     */
    private String appName  = "rawdatacentral";

    /**
     * 交互类别
     */
    private String type;

    /**
     * 任务ID
     */
    private long   taskId;

    /**
     * 当前线程ID
     */
    private String threadId = "t0";

    /**
     * 当前指令状态
     */
    private String status;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 交互数据
     */
    private T      data;

    public DirectiveResult() {
    }

    public DirectiveResult(String type, long taskId) {
        this.type = type;
        this.taskId = taskId;
    }

    /**
     * 交互指令redis key
     * @return
     */
    public String getRedisKey() {
        StringBuilder key = new StringBuilder(appName).append("directive").append(type).append(threadId);
        key.append(taskId);
        return key.toString();
    }

    /**
     * 处理成功
     * @param status
     * @return
     */
    public DirectiveResult<T> fill(String status) {
        this.setStatus(status);
        return this;
    }

    /**
     * 处理成功
     * @param status
     * @param data
     * @return
     */
    public DirectiveResult<T> fill(String status, T data) {
        this.setStatus(status);
        this.setData(data);
        return this;
    }

    /**
     * 发生错误
     * @param status
     * @param errorMsg
     * @return
     */
    public DirectiveResult<T> failTure(String status, String errorMsg) {
        return failTure(status, null, errorMsg);
    }

    /**
     * 发生错误
     * @param status
     * @param errorCode
     * @param errorMsg
     * @return
     */
    public DirectiveResult<T> failTure(String status, String errorCode, String errorMsg) {
        return failTure(status, errorCode, errorMsg);
    }

    /**
     * 发生错误
     * @param status
     * @param errorCode
     * @param errorMsg
     * @param data
     * @return
     */
    public DirectiveResult<T> failTure(String status, String errorCode, String errorMsg, T data) {
        this.setStatus(status);
        this.setErrorCode(errorCode);
        this.setErrorMsg(errorMsg);
        this.setData(data);
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public DirectiveResult(String appName) {
        this.appName = appName;
    }
}
