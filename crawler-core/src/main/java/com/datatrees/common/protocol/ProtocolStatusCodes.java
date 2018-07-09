/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol;

public interface ProtocolStatusCodes {

    /** Content was retrieved without errors. */
    public static final int SUCCESS = 1;
    /** Content was not retrieved. Any further errors may be indicated in args. */
    public static final int FAILED = 2;

    /** This protocol was not found. Application may attempt to retry later. */
    public static final int PROTO_NOT_FOUND = 10;
    /** Resource is gone. */
    public static final int GONE = 11;
    /** Resource has moved permanently. New url should be found in args. */
    public static final int MOVED = 12;
    /** Resource has moved temporarily. New url should be found in args. */
    public static final int TEMP_MOVED = 13;
    /** Resource was not found. */
    public static final int NOTFOUND = 14;
    /** Temporary failure. Application may retry immediately. */
    public static final int RETRY = 15;
    /** Unspecified exception cause by the server. */
    public static final int SERVER_EXCEPTION = 16;
    /** Access denied - authorization required, but missing/incorrect. */
    public static final int ACCESS_DENIED = 17;
    /** Access denied by robots.txt rules. */
    public static final int ROBOTS_DENIED = 18;
    /** Too many redirects. */
    public static final int REDIR_EXCEEDED = 19;
    /** Not fetching. */
    public static final int NOTFETCHING = 20;
    /** Unchanged since the last fetch. */
    public static final int NOTMODIFIED = 21;
    /**
     * Request was refused by protocol plugins, because it would block. The expected number of
     * milliseconds to wait before retry may be provided in args.
     */
    public static final int WOULDBLOCK = 22;
    /** Thread was blocked http.max.delays times during fetching. */
    public static final int BLOCKED = 23;


    /** Unspecified exception occured. such as timeout */
    public static final int EXCEPTION = 100;
}
