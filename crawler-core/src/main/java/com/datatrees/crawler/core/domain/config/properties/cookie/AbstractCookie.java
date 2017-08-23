/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.properties.cookie;

import java.io.Serializable;

import com.datatrees.common.util.json.annotation.Description;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import org.apache.commons.lang.BooleanUtils;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 31, 2014 4:24:13 PM
 */
@Description(value = "scope", keys = {"REQUEST", "USER_SESSION", "SESSION", "CUSTOM"}, types = {BaseCookie.class, BaseCookie.class, BaseCookie.class, CustomCookie.class})
public abstract class AbstractCookie implements Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 2174790445783602115L;
    CookieScope scope;
    private Boolean retainQuote;

    @Attr("scope")
    public CookieScope getScope() {
        return scope;
    }

    @Node("@scope")
    public void setScope(String scope) {
        this.scope = CookieScope.getCookieScope(scope);
    }

    @Attr("retain-quote")
    public Boolean getRetainQuote() {
        return BooleanUtils.isTrue(retainQuote);
    }

    @Node("@retain-quote")
    public void setRetainQuote(Boolean retainQuote) {
        this.retainQuote = retainQuote;
    }

    @Override
    public AbstractCookie clone() throws CloneNotSupportedException {
        return (AbstractCookie) super.clone();
    }
}
