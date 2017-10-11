package com.datatrees.rawdatacentral.domain.vo;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class Response implements Serializable {

    @JSONField(serialize = false)
    private static final String DEFAULT_CHARSET = "UTF-8";
    @JSONField(ordinal = 1)
    private Request request;
    @JSONField(ordinal = 1)
    private long    totalTime;
    @JSONField(ordinal = 2)
    private int     statusCode;
    @JSONField(ordinal = 3)
    private List<NameValue> headers = new ArrayList<>();
    @JSONField(ordinal = 4)
    private Map<String, String> responseCookies;
    @JSONField(serialize = false)
    private byte[]              response;
    @JSONField(ordinal = 6)
    private Charset             charset;
    @JSONField(ordinal = 7)
    private String              redirectUrl;
    @JSONField(ordinal = 8)
    private String              contentType;

    public Response(Request request) {
        this.request = request;
    }

    public Response(Request request, Charset charset) {
        this.request = request;
        this.charset = charset;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public List<NameValue> getHeaders() {
        return headers;
    }

    public void setHeaders(List<NameValue> headers) {
        this.headers = headers;
    }

    public Map<String, String> getResponseCookies() {
        return responseCookies;
    }

    public void setResponseCookies(Map<String, String> responseCookies) {
        this.responseCookies = responseCookies;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @JSONField(serialize = false)
    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    @JSONField(ordinal = 10)
    public String getPageContent() {
        try {
            if (null == response) {
                return "";
            }
            if (null != contentType && (contentType.contains("image") || contentType.contains("IMAGE"))) {
                return getPageContentForBase64();
            }
            if (null != charset) {
                return new String(response, charset);
            }
            return new String(response, DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("getPateContent error,charsetName=UTF-8,request=" + request, e);
        }

    }

    public String getPageContent(Charset charsetName) {
        try {
            return new String(response, charsetName);
        } catch (Exception e) {
            throw new RuntimeException("getPateContent error,charsetName=" + charsetName + ",request=" + request, e);
        }
    }

    @JSONField(serialize = false)
    public String getPageContentForBase64() {
        return Base64.getEncoder().encodeToString(response);
    }

    @JSONField(serialize = false)
    public JSONObject getPageContentForJSON() {
        String json = getPageContent().trim();
        if ((json.startsWith("{") && json.endsWith("}")) || (json.startsWith("[") && json.endsWith("]"))) {
            return JSON.parseObject(json);
        }
        //有的结尾带";"
        if (null != json && json.contains("(") && json.trim().contains(")")) {
            json = json.substring(json.indexOf("(") + 1, json.lastIndexOf(")"));
            return JSON.parseObject(json);
        }
        return JSON.parseObject(json);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
