package com.datatrees.rawdatacentral.domain.vo;

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

    private Long                taskId;

    private String              websiteName;

    private String              remarkId;

    private String              url;

    private Map<String, String> params      = new HashMap<>();

    private Map<String, String> header      = new HashMap<>();

    private String              requestTimestamp;

    private String              fullUrl;

    private String              sendCookies;

    private String              protocol;

    private String              contentType = "application/x-www-form-urlencoded";

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

    public String getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(String requestTimestamp) {
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
}
