/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package org.apache.commons.httpclient.methods;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年11月5日 下午3:23:18
 */
public class CustomGetMethod extends GetMethod {

    private boolean uriEscaped;

    private boolean retainQuote;

    private boolean coexist;

    /**
     *
     */
    public CustomGetMethod() {
        super();
    }

    /**
     * @param uri
     */
    public CustomGetMethod(String uri) {
        super(uri);
    }

    public boolean isCoexist() {
        return coexist;
    }

    public CustomGetMethod setCoexist(boolean coexist) {
        this.coexist = coexist;
        return this;
    }

    public boolean isRetainQuote() {
        return retainQuote;
    }

    public CustomGetMethod setRetainQuote(boolean retainQuote) {
        this.retainQuote = retainQuote;
        return this;
    }

    /**
     * @return the uriEscaped
     */
    public boolean isUriEscaped() {
        return uriEscaped;
    }

    /**
     * @param uriEscaped the uriEscaped to set
     */
    public CustomGetMethod setUriEscaped(boolean uriEscaped) {
        this.uriEscaped = uriEscaped;
        return this;
    }

}
