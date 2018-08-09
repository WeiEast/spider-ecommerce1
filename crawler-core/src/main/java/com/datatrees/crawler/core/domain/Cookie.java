/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 16, 2014 5:34:03 PM
 */
public class Cookie implements Serializable {

    public static final Cookie EMPTY = new Cookie();
    /**
     *
     */
    private static final long serialVersionUID = -3894508724345166182L;
    @SerializedName("username")
    private String userName;
    private String cookie;

    public Cookie() {
        super();
    }

    public Cookie(String cookie) {
        this.cookie = cookie;
    }

    public Cookie(String userName, String cookie) {
        super();
        this.userName = userName;
        this.cookie = cookie;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Cookie [cookie=" + cookie + ", userName=" + userName + "]";
    }

}
