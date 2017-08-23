package com.datatrees.crawler.core.processor.proxy;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月5日 下午11:29:46
 */
public class ProxyServiceException extends Exception {

    private static final long serialVersionUID = 8359880772340577605L;

    public ProxyServiceException() {
        super();
    }

    public ProxyServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyServiceException(String message) {
        super(message);
    }

    public ProxyServiceException(Throwable cause) {
        super(cause);
    }
}
