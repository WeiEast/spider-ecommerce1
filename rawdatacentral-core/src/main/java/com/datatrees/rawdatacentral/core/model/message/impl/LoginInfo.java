/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.model.message.impl;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月13日 下午3:06:38
 */
public class LoginInfo {
    private String websiteName;
    private int userId;
    private Map<String, String> header;
    private String url;
    private boolean supplyResult;// true:上次成功;false:上次失败

    private boolean level1Status;// 标识本网站是否需要发送一级状态


    public class Header extends HashMap<String, String> {
        private static final String COOKIE = "Cookie";
        private static final String SET_COOKIE = "Set-Cookie";

        public String getCookie() {
            return get(COOKIE);
        }

        public void setCookie(String cookie) {
            put(COOKIE, cookie);
        }

        public String getSetCookie() {
            return get(SET_COOKIE);
        }

        public void setSetCookie(String setCookie) {
            put(SET_COOKIE, setCookie);
        }

    }
    
    /**
     * @param header the header to set
     */
    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    /**
     * @return the websiteName
     */
    public String getWebsiteName() {
        return websiteName;
    }

    /**
     * @param websiteName the websiteName to set
     */
    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the header
     */
    public Header getHeader() {
        Header header = new Header();
        if (this.header != null) {
            header.putAll(this.header);
        }
        return header;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the supplyResult
     */
    public boolean isSupplyResult() {
        return supplyResult;
    }

    /**
     * @param supplyResult the supplyResult to set
     */
    public void setSupplyResult(boolean supplyResult) {
        this.supplyResult = supplyResult;
    }

    /**
     * @return the level1Status
     */
    public boolean isLevel1Status() {
        return level1Status;
    }

    /**
     * @param level1Status the level1Status to set
     */
    public void setLevel1Status(boolean level1Status) {
        this.level1Status = level1Status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LoginInfo [websiteName=" + websiteName + ", userId=" + userId + ", header=" + header + ", url=" + url + "]";
    }
}
