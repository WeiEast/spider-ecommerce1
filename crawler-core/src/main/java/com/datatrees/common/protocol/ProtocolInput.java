/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol;

import java.net.URLDecoder;
import java.util.*;

import com.datatrees.common.protocol.util.HeaderParser;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 13, 2014 10:53:22 AM
 */
public class ProtocolInput {

    public static final    Logger              log                      = LoggerFactory.getLogger(ProtocolInput.class);

    private final          Map<String, Object> context                  = new HashMap<String, Object>();

    protected static final String              URL                      = "ProtocolInput.URL";
    protected static final String              PROXY                    = "ProtocolInput.PROXY";
    protected static final String              HEADER                   = "ProtocolInput.HEADER";
    protected static final String              COOKIE                   = "ProtocolInput.COOKIE";
    protected static final String              LAST_MODIFY              = "ProtocolInput.LAST_MODIFY";
    protected static final String              FOLLOW_REDIRECT          = "ProtocolInput.FOLLOW_REDIRECT";
    protected static final String REDIRECT_URI_ESCAPED = "ProtocolInput.REDIRECT_URI_ESCAPED";
    protected static final String              COOKIE_CO_EXIST          = "ProtocolInput.COOKIE_CO_EXIST";
    protected static final String              ALLOW_CIRCULAR_REDIRECTS = "ProtocolInput.ALLOW_CIRCULAR_REDIRECTS";


    protected static final String              STATES                   = "ProtocolInput.STATES";

    protected static final String              POST_BODY                = "ProtocolInput.POST_BODY";

    protected static final String              POST_STRING_BODY         = "ProtocolInput.POST_STRING_BODY";

    protected static final String              REQUEST_HEADERS          = "ProtocolInput.REQUEST_HEADER";

    private                Action              action                   = Action.GET;
    private                CookieScope         scope                    = CookieScope.REQUEST;



    public enum Action {
        GET, POST, POST_STRING, POST_FILE, PUT, DELETE;
    }

    public enum CookieScope {
        REQUEST, USER_SESSION, SESSION;
        private boolean retainQuote;

        /**
         * @return the retainQuote
         */
        public boolean isRetainQuote() {
            return retainQuote;
        }

        /**
         * @param retainQuote the retainQuote to set
         */
        public CookieScope setRetainQuote(boolean retainQuote) {
            this.retainQuote = retainQuote;
            return this;
        }
    }

    public CookieScope getCookieScope() {
        return scope;
    }

    public ProtocolInput setCookieScope(CookieScope scope) {
        this.scope = scope;
        return this;
    }

    public ProtocolInput setUrl(String url) {

        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        context.put(URL, url);
        return this;
    }

    public String getUrl() {
        return getString(URL);
    }

    protected ProtocolInput put(String key, Object val) {
        context.put(key, val);
        return this;
    }

    protected Object get(String key) {
        return context.get(key);
    }

    protected String getString(String key) {
        return (String) (context.get(key));
    }

    protected String getString(String key, String defaultS) {
        String actual = (String) (context.get(key));
        if (actual == null) {
            actual = defaultS;
        }
        return actual;
    }

    protected Long getLong(String key, long defaultS) {
        Long actual = (Long) (context.get(key));
        if (actual == null) {
            actual = defaultS;
        }
        return actual;
    }


    public String getProxy() {
        return getString(PROXY);
    }

    public ProtocolInput setProxy(String proxy) {
        put(PROXY, proxy);
        return this;
    }

    @SuppressWarnings("unchecked")
    public List<NameValuePair> getHeaders() {
        return getListWithNew(HEADER);
    }

    /**
     * add request headers the json list is key value pairs of headers
     * 
     * @param jsonHeaderList
     * @return
     */
    public ProtocolInput addHeader(String jsonHeaderList) {
        List<NameValuePair> headers = HeaderParser.getHeaders(jsonHeaderList);
        List<NameValuePair> orginalHeaders = getListWithNew(HEADER);
        orginalHeaders.addAll(headers);
        return this;
    }

    /**
     * add request header
     * 
     * @param key
     * @param value
     * @return
     */
    public ProtocolInput addHeader(String key, String value) {
        List<NameValuePair> orginalHeaders = getListWithNew(HEADER);
        orginalHeaders.add(new NameValuePair(key, value));
        return this;
    }


    /**
     * add request header
     * 
     * @param key
     * @param value
     * @return
     */
    public ProtocolInput addHeaders(Map<String, String> headers) {
        List<NameValuePair> orginalHeaders = getListWithNew(HEADER);
        orginalHeaders.addAll(mapToList(headers));
        return this;
    }


    public ProtocolInput clearHeaders() {
        getListWithNew(HEADER).clear();
        return this;
    }


    /**
     * 
     * @param headers
     * @return
     */
    private Collection<? extends NameValuePair> mapToList(Map<String, String> headers) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (MapUtils.isNotEmpty(headers)) {
            Iterator<String> keyIterator = headers.keySet().iterator();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                String val = headers.get(key);
                pairs.add(new NameValuePair(key, val));
            }
        }
        return pairs;
    }

    /**
     * add post parameters
     * 
     * @param key
     * @param value
     * @return
     */
    public ProtocolInput addPostRequestParam(String key, String value) {
        List<NameValuePair> orginalHeaders = getListWithNew(POST_BODY);
        orginalHeaders.add(new NameValuePair(key, URLDecoder.decode(value)));
        return this;
    }

    /**
     * set raw string as post body this will ignore url parameters
     * 
     * @param content
     * @return
     */
    public ProtocolInput setPostBody(String content) {
        put(POST_STRING_BODY, content);
        return this;
    }

    /**
     * get string entity as post content body
     * 
     * @return
     */
    public String getPostBody() {
        return getString(POST_STRING_BODY);
    }

    public List<NameValuePair> getPostRequestParam() {
        return getListWithNew(POST_BODY);
    }

    @SuppressWarnings("unchecked")
    private List getListWithNew(String key) {
        List orginalHeaders = (List) get(key);
        if (orginalHeaders == null) {
            orginalHeaders = new ArrayList<Header>();
            put(key, orginalHeaders);
        }
        return orginalHeaders;
    }

//增加cookie path 的sate
    public String getCookie() {
        return getString(COOKIE);
    }
    
    
    public Map<String,Cookie> getCookies() {
        return (Map)get(COOKIE);
    }

    public ProtocolInput setCookie(String cookie) {
        put(COOKIE, cookie);
        return this;
    }


    public Action getAction() {
        return action;
    }


    public ProtocolInput setLastModify(long lastModified) {
        put(LAST_MODIFY, (lastModified));
        return this;
    }

    public long getLastModify() {
        long lastModify = getLong(LAST_MODIFY, 0);
        return lastModify;
    }


    public ProtocolInput setAction(Action action) {
        this.action = action;
        return this;
    }

    public ProtocolInput setRedirectUriEscaped(Boolean redirectUriEscaped) {
        put(REDIRECT_URI_ESCAPED, redirectUriEscaped);
        return this;
    }

    // default true
    public boolean getRedirectUriEscaped() {
        Boolean bol = (Boolean) context.get(REDIRECT_URI_ESCAPED);
        if (bol == null) {
            bol = true;
        }
        return bol;
    }

    public ProtocolInput setCoExist(Boolean coexist) {
        put(COOKIE_CO_EXIST, coexist);
        return this;
    }

    // default true
    public boolean getCoExist() {
        Boolean bol = (Boolean) context.get(COOKIE_CO_EXIST);
        if (bol == null) {
            bol = true;
        }
        return bol;
    }
    
    public ProtocolInput setAllowCircularRedirects(Boolean allowCircularRedirects) {
        put(ALLOW_CIRCULAR_REDIRECTS, allowCircularRedirects);
        return this;
    }

    public boolean getAllowCircularRedirects() {
        Boolean bol = (Boolean) context.get(ALLOW_CIRCULAR_REDIRECTS);
        if (bol == null) {
            bol = false;
        }
        return bol;
    }
    
    public ProtocolInput setFollowRedirect(Boolean followRedirect) {
        put(FOLLOW_REDIRECT, followRedirect);
        return this;
    }

    public boolean getFollowRedirect() {
        Boolean bol = (Boolean) context.get(FOLLOW_REDIRECT);
        if (bol == null) {
            bol = true;
        }
        return bol;
    }


    public HttpState getState() {
        return (HttpState) get(STATES);
    }

    public ProtocolInput setState(HttpState state) {
        put(STATES, state);
        return this;
    }

    public Header[] getRequestHeaders() {
        return (Header[]) get(REQUEST_HEADERS);
    }

    public void setRequestHeaders(Header[] headers) {
        put(REQUEST_HEADERS, headers);
    }

}
