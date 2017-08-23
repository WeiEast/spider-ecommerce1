/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.plugin.login;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年9月25日 下午8:29:58
 */
public class LoginTimeOutException extends InterruptedException {

    /**
     *
     */
    private static final long serialVersionUID = 1312461052839955907L;

    /**
     *
     */
    public LoginTimeOutException() {
        super();
    }

    /**
     * @param s
     */
    public LoginTimeOutException(String s) {
        super(s);
    }

}
