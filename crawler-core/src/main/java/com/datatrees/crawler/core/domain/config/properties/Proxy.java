/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.properties;

import java.io.Serializable;

import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:33:45 AM
 */
public class Proxy implements Serializable {

    /**
     *
     */
    private static final long   serialVersionUID = 4167867516504535277L;

    private              String proxy;

    private              Scope  scope;

    private              String pattern;

    private              Mode   mode;

    @Tag
    public String getProxy() {
        return proxy;
    }

    @Node("text()")
    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    @Attr("scope")
    public Scope getScope() {
        return scope;
    }

    @Node("@scope")
    public void setScope(String scope) {
        this.scope = Scope.getScope(scope);
    }

    @Attr("pattern")
    public String getPattern() {
        return pattern;
    }

    @Node("@pattern")
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Attr("mode")
    public Mode getMode() {
        return mode;
    }

    @Node("@mode")
    public void setMode(String mode) {
        this.mode = Mode.getMode(mode);
    }

}
