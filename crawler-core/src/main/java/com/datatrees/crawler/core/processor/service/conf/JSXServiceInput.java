/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.service.conf;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 24, 2014 7:11:39 PM
 */
public class JSXServiceInput {

    private Integer id      = 0;
    private String  jsonrpc = "2.0";
    private String      method;
    private JSXParamsIn params;

    public Integer getId() {
        return id;
    }

    public JSXServiceInput setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public JSXServiceInput setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public JSXServiceInput setMethod(String method) {
        this.method = method;
        return this;
    }

    public JSXParamsIn getParams() {
        return params;
    }

    public JSXServiceInput setParams(JSXParamsIn params) {
        this.params = params;
        return this;
    }
}
