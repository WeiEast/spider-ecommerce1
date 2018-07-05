package com.datatrees.common.protocol.http;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpMethod;

/**
 * @author Jerry
 * @since 14:52 2018/4/11
 */
public class CustomRetryHandler extends DefaultHttpMethodRetryHandler {

    public CustomRetryHandler(int retryCount) {
        super(retryCount, false);
    }

    public CustomRetryHandler() {
    }

    @Override
    public boolean retryMethod(HttpMethod method, IOException exception, int executionCount) {
        if (super.retryMethod(method, exception, executionCount)) {
            // Do not retry if proxy is set
            HostConfiguration conf = method.getHostConfiguration();
            return conf == null || conf.getProxyHost() == null;
        }
        return false;
    }
}
