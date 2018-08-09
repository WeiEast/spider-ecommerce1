/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.login;

import java.io.Serializable;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 11:38:49 AM
 */
public class LoginConfig implements Serializable {

    /**
     *
     */
    private static final long             serialVersionUID = 3832213144362274287L;

    private              LoginType        type;

    private              AbstractPlugin   plugin;

    private              String           urlTemplate;

    private              String           headers;

    private              LoginCheckConfig loginCheckConfig;

    @Tag("login-check")
    public LoginCheckConfig getLoginCheckConfig() {
        return loginCheckConfig;
    }

    @Node("login-check")
    public void setLoginCheckConfig(LoginCheckConfig loginCheckConfig) {
        this.loginCheckConfig = loginCheckConfig;
    }

    @Attr("login-type")
    public LoginType getType() {
        return type;
    }

    @Node("@login-type")
    public void setType(String type) {
        this.type = LoginType.getLoginType(type);
    }

    @Attr(value = "plugin-ref", referenced = true)
    public AbstractPlugin getPlugin() {
        return plugin;
    }

    @Node(value = "@plugin-ref", referenced = true)
    public void setPlugin(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    @Tag("url-template")
    public String getUrlTemplate() {
        return urlTemplate;
    }

    @Node("url-template/text()")
    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    @Tag("headers")
    public String getHeaders() {
        return headers;
    }

    @Node("headers/text()")
    public void setHeaders(String headers) {
        this.headers = headers;
    }

}
