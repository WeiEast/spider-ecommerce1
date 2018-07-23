package com.datatrees.crawler.core.processor.service.conf;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0 2012-04-19
 * @since 1.0
 */
public class JsonRPCOut {

    private Integer   id      = 123;

    private String    jsonrpc = "2.0";

    private String    method;

    private ParamsOut result;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ParamsOut getResult() {
        return result;
    }

    public void setResult(ParamsOut result) {
        this.result = result;
    }

}
