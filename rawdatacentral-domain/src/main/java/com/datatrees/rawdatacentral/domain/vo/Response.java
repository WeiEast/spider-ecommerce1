package com.datatrees.rawdatacentral.domain.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {

    private Request             request;

    private Map<String, String> header;

    private String              receiveCookies;

    private int                 statusCode;

    private byte[]              response;

    public Response(Request request) {
        this.request = request;
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

    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    public String getPateContent() {
        try {
            return new String(response, "UTF-8");
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

    public String getPateContentForBase64() {
        return Base64.getEncoder().encodeToString(response);
    }

    public JSONObject getPateContentForJSON() {
        return JSON.parseObject(getPateContent());
    }

}
