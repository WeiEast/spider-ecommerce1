/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 下午12:21:28
 */
@SuppressWarnings({"rawtypes", "serial", "unchecked"})
public abstract class AbstractData extends HashMap {

    public static final String UNIQUESIGN  = "uniqueSign";

    public static final String URL         = "url";

    // maybe collection
    public static final String PAGECONTENT = "pageContent";

    public static final String RESULTTYPE  = "resultType";

    public static final String EXTRAINFO   = "extraInfo";

    public String getResultType() {
        return (String) this.get(RESULTTYPE);
    }

    public void setResultType(String resultType) {
        this.put(RESULTTYPE, resultType);
    }

    public Object getPageContent() {
        return this.get(PAGECONTENT);
    }

    public void setPageContent(Object pageContent) {
        this.put(PAGECONTENT, pageContent);
    }

    public String getUniqueSign() {
        return (String) this.get(UNIQUESIGN);
    }

    public void setUniqueSign(String uniqueSign) {
        this.put(UNIQUESIGN, uniqueSign);
    }

    public String getUrl() {
        return (String) this.get(URL);
    }

    public void setUrl(String url) {
        this.put(URL, url);
    }

    public Map<String, Object> getExtraInfo() {
        if (this.get(EXTRAINFO) != null && this.get(EXTRAINFO) instanceof Map) {
            return (Map<String, Object>) this.get(EXTRAINFO);
        } else {
            return null;
        }
    }

}
