package com.datatrees.rawdatacentral.domain.result;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * http接口返回信息
 * Created by zhouxinghai on 2017/4/27.
 */
public class HttpResult implements Serializable {

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
    private Object              data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
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

    public HttpResult failure() {
        this.setStatus(false);
        this.setResponseCode(-1);
        this.setMessage("处理失败");
        this.setTimestamp(System.currentTimeMillis());
        return this;
    }

    public HttpResult success() {
        this.setStatus(true);
        this.setResponseCode(1);
        this.setMessage("处理成功!");
        this.setTimestamp(System.currentTimeMillis());
        return this;
    }

    public HttpResult success(Object data) {
        this.success();
        this.setData(data);
        return this;
    }
}
