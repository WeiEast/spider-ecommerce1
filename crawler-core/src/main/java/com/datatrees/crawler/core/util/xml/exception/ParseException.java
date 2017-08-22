/**
 * ParseException.java
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version    1.0
 * @since      Jan 4, 2013 8:28:45 PM
 */
package com.datatrees.crawler.core.util.xml.exception;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version    1.0
 * @since      Jan 4, 2013 8:28:45 PM
 */
public class ParseException extends Exception{

	private static final long serialVersionUID = 1402629003842283884L;

	public ParseException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ParseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ParseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
