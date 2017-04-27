package com.datatrees.rawdatacentral.domain.result;

import java.io.Serializable;

/**
 * Created by zhouxinghai on 2017/4/27.
 */
public class HttpResult implements Serializable {

    private boolean status       = false;

    private String  message      = "处理失败";

    private int     responseCode = -1;

    private Object  data;

    private long    timestamp    = System.currentTimeMillis();

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

    public static HttpResult Failure() {
        HttpResult result = new HttpResult();
        result.setStatus(false);
        result.setResponseCode(-1);
        result.setMessage("处理失败");
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public static HttpResult Success() {
        HttpResult result = new HttpResult();
        result.setStatus(true);
        result.setResponseCode(1);
        result.setMessage("处理成功!");
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public static HttpResult Success(Object data) {
        HttpResult result = new HttpResult();
        result.setStatus(true);
        result.setResponseCode(1);
        result.setMessage("处理成功!");
        result.setTimestamp(System.currentTimeMillis());
        result.setData(data);
        return result;
    }
}
