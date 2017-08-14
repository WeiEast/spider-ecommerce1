package com.datatrees.rawdatacentral.domain.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.RequestType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求
 */
public class Request implements Serializable {

    @JSONField(ordinal = 1)
    private Long                taskId;

    @JSONField(ordinal = 2)
    private String              websiteName;

    @JSONField(ordinal = 3)
    private String              proxy;

    @JSONField(ordinal = 4)
    private String              fullUrl;

    @JSONField(ordinal = 5)
    private String              url;

    @JSONField(ordinal = 6)
    private Map<String, String> params      = new HashMap<>();

    @JSONField(ordinal = 7)
    private String              remarkId;


    @JSONField(ordinal = 8)
    private Map<String, String> header      = new HashMap<>();

    @JSONField(ordinal = 9)
    private long                requestTimestamp;

    @JSONField(ordinal = 10)
    private String              sendCookies;

    @JSONField(ordinal = 11)
    private String              protocol;

    @JSONField(ordinal = 12)
    private String              contentType = "application/x-www-form-urlencoded";

    @JSONField(ordinal = 13)
    private RequestType         requestType = RequestType.GET;

    public Request() {
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

    public String getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(String remarkId) {
        this.remarkId = remarkId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getSendCookies() {
        return sendCookies;
    }

    public void setSendCookies(String sendCookies) {
        this.sendCookies = sendCookies;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
