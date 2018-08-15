/**
 * ParseException.java
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 4, 2013 8:28:45 PM
 */

package com.datatrees.crawler.core.util.xml.exception;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 4, 2013 8:28:45 PM
 */
@Deprecated
public class ParseException extends Exception {

    private static final long serialVersionUID = 1402629003842283884L;

    public ParseException() {
        super();
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}
