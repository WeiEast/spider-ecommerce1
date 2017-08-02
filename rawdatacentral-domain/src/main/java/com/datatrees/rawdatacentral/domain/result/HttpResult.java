package com.datatrees.rawdatacentral.domain.result;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * http接口返回信息 Created by zhouxinghai on 2017/4/27.
 */
public class HttpResult<T> implements Serializable {

    /**
     * true:操作成功 false:操作失败
     */
    private boolean             status       = false;

    /**
     * 返回提示信息
     */
    private String              message      = "处理失败";

    /**
     * 返回代码: 小于0:失败,大于0:操作成功
     */
    private int                 responseCode = -1;

    /**
     * 返回数据
     */
    private T                   data;

    /**
     * 返回信息扩展
     */
    private Map<String, Object> extra        = new HashMap<>();

    /**
     * 时间戳
     */
    private long                timestamp    = System.currentTimeMillis();

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public HttpResult() {
        this.failure();
    }

    public HttpResult<T> failure() {
        this.setStatus(false);
        this.setResponseCode(-1);
        this.setMessage("处理失败");
        this.setTimestamp(System.currentTimeMillis());
        return this;
    }

    public HttpResult<T> failure(String errorMsg) {
        this.setStatus(false);
        this.setResponseCode(-1);
        this.setMessage(errorMsg);
        this.setTimestamp(System.currentTimeMillis());
        return this;
    }

    public HttpResult<T> failure(ErrorCode errorCode) {
        this.setStatus(false);
        this.setResponseCode(errorCode.getErrorCode());
        this.setMessage(errorCode.getErrorMsg());
        this.setTimestamp(System.currentTimeMillis());
        return this;
    }

    public HttpResult<T> failure(int errorCode, String errorMsg) {
        this.setStatus(false);
        this.setResponseCode(errorCode);
        this.setMessage(errorMsg);
        this.setTimestamp(System.currentTimeMillis());
        return this;
    }

    public HttpResult<T> failure(ErrorCode errorCode, String errorMsg) {
        this.setStatus(false);
        this.setResponseCode(errorCode.getErrorCode());
        this.setMessage(errorMsg);
        this.setTimestamp(System.currentTimeMillis());
        return this;
    }

    public HttpResult<T> success() {
        this.setStatus(true);
        this.setResponseCode(1);
        this.setMessage("处理成功!");
        this.setTimestamp(System.currentTimeMillis());
        return this;
    }

    public HttpResult<T> success(T data) {
        this.success();
        this.setData(data);
        return this;
    }

    /**
     * @return the extra
     */
    public Map<String, Object> getExtra() {
        return extra;
    }

    /**
     * @param extra the extra to set
     */
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
