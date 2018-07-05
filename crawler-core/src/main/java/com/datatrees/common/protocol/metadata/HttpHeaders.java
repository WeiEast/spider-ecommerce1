/**
 * datatrees.com Inc.
 * Copyright (c) 2004-${year} All Rights Reserved.
 */
package com.datatrees.common.protocol.metadata;

/**
 * A collection of HTTP header names.
 * 
 * @see <a href="http://rfc-ref.org/RFC-TEXTS/2616/">Hypertext Transfer Protocol
 *      -- HTTP/1.1 (RFC 2616)</a>
 * 
 * @author Chris Mattmann
 * @author J&eacute;r&ocirc;me Charron
 */
public interface HttpHeaders {

    public final static String CONTENT_ENCODING = "Content-Encoding";

    public final static String CONTENT_LANGUAGE = "Content-Language";

    public final static String CONTENT_LENGTH = "Content-Length";

    public final static String CONTENT_LOCATION = "Content-Location";

    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    public final static String CONTENT_MD5 = "Content-MD5";

    public final static String CONTENT_TYPE = "Content-Type";

    public final static String LAST_MODIFIED = "Last-Modified";

    public final static String LOCATION = "Location";

}
