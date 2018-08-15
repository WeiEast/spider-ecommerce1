/**
 * ReflectionException.java
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 5, 2013 9:58:07 AM
 */

package com.datatrees.crawler.core.util.xml.exception;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 5, 2013 9:58:07 AM
 */
@Deprecated
public class ReflectionException extends RuntimeException {

    private static final long serialVersionUID = 350421172069238874L;

    public ReflectionException() {
        super();
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }

}
