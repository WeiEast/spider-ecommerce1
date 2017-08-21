package com.datatrees.crawler.core.processor.proxy;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月5日 下午11:29:04 
 */
public class ProxyConfigException extends RuntimeException{

	private static final long serialVersionUID = -652565804330041893L;

	public ProxyConfigException() {
		super();
	}

	public ProxyConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProxyConfigException(String message) {
		super(message);
	}

	public ProxyConfigException(Throwable cause) {
		super(cause);
	}
}
