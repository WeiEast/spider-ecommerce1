/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.crawler.framework.login;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 16, 2014 5:34:03 PM
 */
public class Cookie implements Serializable {

    public static final  Cookie EMPTY            = new Cookie();

    /**
     *
     */
    private static final long   serialVersionUID = -3894508724345166182L;

    @SerializedName("username")
    private              String userName;

    private              String cookie;

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
