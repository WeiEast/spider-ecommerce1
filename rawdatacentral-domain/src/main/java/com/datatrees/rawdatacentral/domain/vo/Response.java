package com.datatrees.rawdatacentral.domain.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {

    @JSONField(ordinal = 1)
    private Request             request;

    @JSONField(ordinal = 2)
    private int                 statusCode;

    @JSONField(ordinal = 3)
    private Map<String, String> header;

    @JSONField(ordinal = 4)
    private String              receiveCookies;

    @JSONField(serialize = false)
    private byte[]              response;

    @JSONField(ordinal = 6)
    private String              charsetName = "UTF-8";

    public Response(Request request) {
        this.request = request;
    }

    public Response(Request request, String charsetName) {
        this.request = request;
        this.charsetName = charsetName;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }


    public Request getRequest() {
        return request;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
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

    @JSONField(serialize = false)
    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    @JSONField(ordinal = 10)
    public String getPateContent() {
        try {
            return new String(response, charsetName);
        } catch (Exception e) {
            throw new RuntimeException("getPateContent error,charsetName=UTF-8,request=" + request, e);
        }

    }

    public String getPateContent(String charsetName) {
        try {
            return new String(response, charsetName);
        } catch (Exception e) {
            throw new RuntimeException("getPateContent error,charsetName=" + charsetName + ",request=" + request, e);
        }
    }

    @JSONField(serialize = false)
    public String getPateContentForBase64() {
        return Base64.getEncoder().encodeToString(response);
    }

    @JSONField(serialize = false)
    public JSONObject getPateContentForJSON() {
        return JSON.parseObject(getPateContent());
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
