package com.datatrees.rawdatacentral.domain.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求
 */
public class Request implements Serializable {

    private Long                taskId;

    private String              remarkId;

    private String              url;

    private Map<String, String> params      = new HashMap<>();

    private Map<String, String> header      = new HashMap<>();

    private String              requestTimestamp;

    private String              fullUrl;

    private String              sendCookies;

    private String              charsetName = "UTF-8";

    private String              protocol;

    private String              remark;

    private String              contentType = "application/x-www-form-urlencoded";

    private String              type        = "get";

    private String              receiveCookies;

    private int                 statusCode;

    private String              pageContent;

    @JSONField(serialize = false)
    private byte[]              response;

    public Request() {
    }

    public Request(Long taskId, String remarkId, String fullUrl) {
        this.taskId = taskId;
        this.remarkId = remarkId;
        this.fullUrl = fullUrl;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReceiveCookies() {
        return receiveCookies;
    }

    public void setReceiveCookies(String receiveCookies) {
        this.receiveCookies = receiveCookies;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getPageContent() {
        return pageContent;
    }

    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }

    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }
}
