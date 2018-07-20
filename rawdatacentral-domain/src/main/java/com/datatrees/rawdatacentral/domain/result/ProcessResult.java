package com.datatrees.rawdatacentral.domain.result;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.ProcessStatus;

public class ProcessResult<T> implements Serializable {

    /**
     * 命令ID
     */
    private Long    processId;
    /**
     * 执行结果:PROCESSING,SUCCESS,FAIL
     */
    private String  processStatus;
    /**
     * 错误代码
     */
    private Integer errorCode;
    /**
     * 错误信息
     */
    private String  errorMsg;
    /**
     * 返回数据
     */
    private T       data;
    /**
     * 返回信息扩展
     */
    private Map<String, Object> extra     = new HashMap<>();
    /**
     * 时间戳
     */
    private long                timestamp = System.currentTimeMillis();

    public ProcessResult() {
    }

    public ProcessResult(Long processId, String processStatus) {
        this.processId = processId;
        this.processStatus = processStatus;
    }

    public ProcessResult success() {
        this.processStatus = ProcessStatus.SUCCESS;
        return this;
    }

    public ProcessResult success(T data) {
        this.data = data;
        this.processStatus = ProcessStatus.SUCCESS;
        return this;
    }

    public ProcessResult fail(ErrorCode error) {
        this.processStatus = ProcessStatus.FAIL;
        this.errorCode = error.getErrorCode();
        this.errorMsg = error.getErrorMsg();
        return this;
    }

    public ProcessResult fail(Integer errorCode, String errorMsg) {
        this.processStatus = ProcessStatus.FAIL;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        return this;
    }

    public ProcessResult fail(Long processId, Integer errorCode, String errorMsg) {
        this.processId = processId;
        this.processStatus = ProcessStatus.FAIL;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        return this;
    }

    public ProcessResult processing(Long processId) {
        this.processId = processId;
        this.processStatus = ProcessStatus.PROCESSING;
        return this;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public boolean isSuccess() {
        return null != processStatus && processStatus.equals(ProcessStatus.SUCCESS);
    }

    @Override
    public String toString() {
        return "ProcessResult{" + "processId='" + processId + '\'' + ", processStatus='" + processStatus + '\'' + ", errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' + '}';
    }
}
