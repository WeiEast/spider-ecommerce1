package com.datatrees.rawdatacentral.domain.vo;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.datatrees.rawdatacentral.domain.enums.RequestType;

/**
 * 请求
 */
public class Request implements Serializable {

    /**
     * 请求ID随机,不保证重复,重复概率低
     */
    @JSONField(ordinal = 1)
    private Long   requestId;
    @JSONField(ordinal = 1)
    private Long   taskId;
    @JSONField(ordinal = 2)
    private String websiteName;
    @JSONField(ordinal = 3)
    private String proxy;
    @JSONField(ordinal = 4)
    private String fullUrl;
    @JSONField(ordinal = 5)
    private String url;
    @JSONField(ordinal = 6)
    private Map<String, String> params = new HashMap<>();
    @JSONField(ordinal = 7)
    private String remarkId;
    @JSONField(ordinal = 8)
    private Map<String, String> header = new HashMap<>();
    @JSONField(ordinal = 9)
    private long   requestTimestamp;
    @JSONField(ordinal = 10)
    private String requestCookies;
    @JSONField(ordinal = 11)
    private String protocol;
    @JSONField(ordinal = 12)
    private String        contentType    = "application/x-www-form-urlencoded";
    @JSONField(ordinal = 12)
    private Charset       charset        = Charset.forName("ISO-8859-1");
    @JSONField(ordinal = 13)
    private RequestType   type           = RequestType.GET;
    @JSONField(ordinal = 9)
    private int           maxRetry       = 1;
    @JSONField(ordinal = 10)
    private AtomicInteger retry          = new AtomicInteger(0);
    @JSONField(ordinal = 11)
    private int           connectTimeout = 10000;
    @JSONField(ordinal = 12)
    private int           socketTimeout  = 20000;
    @JSONField(ordinal = 13)
    private String requestBodyContent;
    @JSONField(ordinal = 14)
    private Boolean proxyEnable = true;

    public Request() {
    }

    public Boolean getProxyEnable() {
        return proxyEnable;
    }

    public void setProxyEnable(Boolean proxyEnable) {
        this.proxyEnable = proxyEnable;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
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

    public String getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(String remarkId) {
        this.remarkId = remarkId;
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

    public String getRequestCookies() {
        return requestCookies;
    }

    public void setRequestCookies(String requestCookies) {
        this.requestCookies = requestCookies;
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

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType requestType) {
        this.type = requestType;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public AtomicInteger getRetry() {
        return retry;
    }

    public void setRetry(AtomicInteger retry) {
        this.retry = retry;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getRequestBodyContent() {
        return requestBodyContent;
    }

    public void setRequestBodyContent(String requestBodyContent) {
        this.requestBodyContent = requestBodyContent;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
