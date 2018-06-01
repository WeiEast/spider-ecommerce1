/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.Constant;
import com.datatrees.common.protocol.NameValuePair;
import com.datatrees.common.protocol.ProtocolInput;
import com.datatrees.common.protocol.ProtocolInput.Action;
import com.datatrees.common.protocol.ProtocolInput.CookieScope;
import com.datatrees.common.protocol.Response;
import com.datatrees.common.protocol.https.EasySSLProtocolSocketFactory;
import com.datatrees.common.protocol.metadata.Metadata;
import com.datatrees.common.protocol.util.*;
import com.datatrees.common.util.ReflectionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a protocol plugin that configures an HTTP client for Basic, Digest and NTLM
 * authentication schemes for web server as well as proxy server. It takes care of HTTPS protocol as
 * well as cookies in a single fetch session.
 * 
 * @author Susam Pal
 */
public class WebClient extends HttpBase {

    private static final Logger                             LOG               = LoggerFactory.getLogger(WebClient.class);

    private              MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

    // Since the Configuration has not yet been set,
    // then an unconfigured client is returned.
    private              HttpClient                         client            = new ProtocalHttpClient(connectionManager);

    private int maxThreadsTotal = 400;
    private int maxThreadsPerHost = 200;
    private int maxRedirect = 10;

    private String proxyUsername;
    private String proxyPassword;
    private String proxyRealm;
    private String agentHost;

    /**
     * Returns the configured HTTP client.
     * 
     * @return HTTP client
     */
    public HttpClient getClient() {
        return client;
    }

    /**
     * Constructs this plugin.
     */
    public WebClient() {
        super();
    }

    /**
     * Reads the configuration from the Nutch configuration files and sets the configuration.
     * 
     * @param conf Configuration
     */
    public void setConf(Configuration conf) {
        super.setConf(conf);
        this.maxThreadsTotal = conf.getInt(HTTPConstants.MAX_TOTAL_CONNECTIONS, maxThreadsTotal);
        this.maxThreadsPerHost = conf.getInt(HTTPConstants.MAX_HOST_CONNECTIONS, maxThreadsPerHost);
        this.maxRedirect = conf.getInt(HTTPConstants.MAX_REDIRECTS, maxRedirect);
        this.proxyUsername = conf.get("http.proxy.username", "");
        this.proxyPassword = conf.get("http.proxy.password", "");
        this.proxyRealm = conf.get("http.proxy.realm", "");

        agentHost = conf.get("http.agent.host", "");
        configureClient();
    }

    /**
     * connectionParams Configures the HTTP client
     */
    private void configureClient() {

        // Set up an HTTPS socket factory that accepts self-signed certs.
        ProtocolSocketFactory factory = new EasySSLProtocolSocketFactory();
        Protocol https = new Protocol("https", factory, 443);
        Protocol.registerProtocol("https", https);

        HttpConnectionManagerParams connectionParams = connectionManager.getParams();
        connectionParams.setConnectionTimeout(connectionTimeout);
        connectionParams.setSoTimeout(socketTimeout);
        connectionParams.setSendBufferSize(BUFFER_SIZE);
        connectionParams.setReceiveBufferSize(BUFFER_SIZE);
        connectionParams.setMaxTotalConnections(maxThreadsTotal);
        connectionParams.setDefaultMaxConnectionsPerHost(maxThreadsPerHost);
        // connectionParams.setStaleCheckingEnabled(false);


        // executeMethod(HttpMethod) seems to ignore the connection timeout on
        // the connection manager.
        // set it explicitly on the HttpClient.
        client.getParams().setConnectionManagerTimeout(connectionManagerTimeout);
        client.getParams().setIntParameter(HTTPConstants.MAX_REDIRECTS, maxRedirect);
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // set retry handler
        setRetryHandler();


        HostConfiguration hostConf = client.getHostConfiguration();
        ArrayList<Header> headers = new ArrayList<Header>();
        // Set the User Agent in the header
        // headers.add(new Header("User-Agent", userAgent));
        // prefer English
        headers.add(new Header("Accept-Language", acceptLanguage));
        // prefer UTF-8
        headers.add(new Header("Accept-Charset", acceptCharset));
        // prefer understandable formats
        // headers.add(new Header("Accept", accept));
        // accept gzipped content
        headers.add(new Header("Accept-Encoding", acceptEncoding));
        // set Connection
        headers.add(new Header("Connection", connection));

        client.getParams().setParameter("http.default-headers", headers);

        ArrayList<String> patterns = new ArrayList<String>();
        patterns.addAll(Arrays.asList(new String[] {"EEE, dd MMM yyyy HH:mm:ss zzz", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy",
                "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z",
                "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z",
                "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z", "EEE MMM dd HH:mm:ss Z yyyy"}));
        client.getParams().setParameter("http.dateparser.patterns", patterns);
        // HTTP proxy server details
        if (useProxy) {
            hostConf.setProxy(proxyHost, proxyPort);
            if (proxyUsername.length() > 0) {
                AuthScope proxyAuthScope = getAuthScope(this.proxyHost, this.proxyPort, this.proxyRealm);

                NTCredentials proxyCredentials = new NTCredentials(this.proxyUsername, this.proxyPassword, agentHost, this.proxyRealm);
                client.getState().setProxyCredentials(proxyAuthScope, proxyCredentials);
            }
        }

    }


    /**
     * 
     */
    private void setRetryHandler() {
        String retryHandlerClass = conf.get(HTTPConstants.RETRY_HANDLER, CustomerRetryHandler.class.getName());
        HttpMethodRetryHandler retryHandler = null;
        if (StringUtils.isNotEmpty(retryHandlerClass)) {
            try {
                retryHandler = ReflectionUtils.newInstance(retryHandlerClass);
                LOG.debug("find customer retry handler.." + retryHandlerClass);
            } catch (Exception e) {
                LOG.error("load retry handler class error!", e);
            }
        }
        if (retryHandler != null) {
            client.getParams().setParameter(HTTPConstants.RETRY_HANDLER, retryHandler);
        }
    }

    /**
     * Returns an authentication scope for the specified <code>host</code>, <code>port</code>,
     * <code>realm</code> and <code>scheme</code>.
     * 
     * @param host Host name or address.
     * @param port Port number.
     * @param realm Authentication realm.
     * @param scheme Authentication scheme.
     */
    private static AuthScope getAuthScope(String host, int port, String realm, String scheme) {

        if (host.length() == 0) host = null;

        if (port < 0) port = -1;

        if (realm.length() == 0) realm = null;

        if (scheme.length() == 0) scheme = null;

        return new AuthScope(host, port, realm, scheme);
    }

    /**
     * Returns an authentication scope for the specified <code>host</code>, <code>port</code> and
     * <code>realm</code>.
     * 
     * @param host Host name or address.
     * @param port Port number.
     * @param realm Authentication realm.
     */
    private static AuthScope getAuthScope(String host, int port, String realm) {
        return getAuthScope(host, port, realm, "");
    }

    private void setPostBody(String postData, PostMethod method) throws UnsupportedEncodingException {
        if (postData != null) {
            try {
                method.setRequestEntity(new StringRequestEntity(postData, null, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                try {
                    method.setRequestEntity(new StringRequestEntity(postData, null, CharsetUtil.getDefaultCharsetName()));
                } catch (UnsupportedEncodingException e1) {
                    throw e1;
                }
                LOG.warn("set postbody " + postData + " error,UnsupportedEncoding.");
            }
        }
    }


    /**
     * 
     * @param action
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    private HttpMethod getMethodByAction(Action action, String url, ProtocolInput input) throws UnsupportedEncodingException {
        HttpMethod method = null;
        Action result = action;
        if (url.contains(URLSPLIT)) {
            LOG.debug("find post pattern " + url);
            result = Action.POST;
        }
        switch (result) {
            case GET:
                try {
                    if (!url.contains("%")) {
                        url = new org.apache.commons.httpclient.URI(url, false, "UTF-8").toString();
                    }
                } catch (Exception e) {
                    LOG.error("url escaped error", e);
                }
                method =
                        new CustomGetMethod(url).setUriEscaped(input.getRedirectUriEscaped()).setCoexist(input.getCoExist())
                                .setRetainQuote(input.getCookieScope().isRetainQuote());
                break;
            case POST:
                String postUrl = StringUtils.substringBefore(url, URLSPLIT);
                String postData = StringUtils.substringAfter(url, URLSPLIT);
                method = new PostMethod(postUrl);
                this.setPostBody(postData, (PostMethod) method);
                break;
            case POST_STRING:
                method = new PostMethod(url);
                this.setPostBody(input.getPostBody(), (PostMethod) method);
                break;
            case PUT:
                method = new PutMethod(url);
                this.setPostBody(input.getPostBody(), (PostMethod) method);
                break;
            case DELETE:
                method = new DeleteMethod(url);
                break;
            default:
                method = new CustomGetMethod(url).setUriEscaped(input.getRedirectUriEscaped());
                break;
        }
        return method;
    }

    protected static String[] split(String data, String splitChar) {
        if (StringUtils.isNotEmpty(data) && StringUtils.isNotEmpty(splitChar)) {
            return StringUtils.split(data, splitChar);
        }
        return null;
    }



    /**
     * get response from input s
     */
    @Override
    Response getResponse(ProtocolInput input) throws IOException {
        // set up
        Action action = input.getAction();
        CookieScope scope = input.getCookieScope();
        String url = input.getUrl();
        HttpMethod method = getMethodByAction(action, url, input);

        if (url.contains(URLSEPARATOR)) {
            LOG.debug("get header by url...");
            String headerData = StringUtils.substringAfter(url, URLSEPARATOR);
            input.addHeader(headerData);
        }

        HostConfiguration hostConfiguration = getHostConfiguration(input.getProxy());
        fillMethodWithParameter(input, method);
        HttpResponse response = new HttpResponse();

        boolean needManualHandlerRedirect = input.getFollowRedirect();
        // request
        int MAX_REDIRECT_COUTN = 3;
        String orgianlUrl = null;
        for (int i = 0; i < MAX_REDIRECT_COUTN; i++) {
            try {
                // post 302
                if (orgianlUrl != null) {
                    String refer = method.getURI().toString();
                    method = new CustomGetMethod(orgianlUrl).setUriEscaped(input.getRedirectUriEscaped());
                    method.addRequestHeader("Referer", refer);
                    // reset cookie string
                    try {
                        Map<String, String> cookieMap = CookieFormater.INSTANCE.parserCookieToMap(input.getCookie(), scope.isRetainQuote());
                        String[] cookieVals =
                                (String[]) ArrayUtils.addAll(response.getHeaders().getValues(HTTPConstants.HTTP_HEADER_SET_COOKIE), response
                                        .getHeaders().getValues(HTTPConstants.HTTP_HEADER_SET_COOKIE2));
                        cookieMap.putAll(CookieFormater.INSTANCE.parserCookietToMap(cookieVals, scope.isRetainQuote()));
                        String cookieString = CookieFormater.INSTANCE.listToString(cookieMap);
                        LOG.info("redirect input reset cookie string " + cookieString);
                        input.setCookie(cookieString);
                    } catch (Exception e) {
                        LOG.warn("redirect input reset cookie string error " + e.getMessage());
                    }
                    fillMethodWithParameter(input, method);
                    // clear header except cookie
                    for (String name : response.getHeaders().names()) {
                        if (!name.startsWith(HTTPConstants.HTTP_HEADER_SET_COOKIE)) {
                            response.getHeaders().remove(name);
                        }
                    }
                }
                HttpState state = getHttpState(scope, input);
                Metadata headers = response.getHeaders();
                byte[] content = null;
                // state = new HttpState();
                int code = getClient().executeMethod(hostConfiguration, method, state);

                // set real request headers
                input.setRequestHeaders(method.getRequestHeaders());

                if (input.getFollowRedirect()) {
                    response.setRedirectUrl(method.getURI().toString());
                }
                printState(state);
                response.setCode(code);
                Header[] heads = method.getResponseHeaders();

                for (int j = 0; j < heads.length; j++) {
                    headers.add(heads[j].getName(), heads[j].getValue());
                }

                // 302 cookie append, 兼容老逻辑
                if (state != null && CookieScope.SESSION != scope) {
                    Cookie[] cks = state.getCookies();
                    for (Cookie cookie : cks) {
                        headers.add(HTTPConstants.HTTP_HEADER_SET_COOKIE, CookieParser.formatCookieFull(cookie));
                    }
                }

                // Limit download size
                content = getResponseContent(url, method, headers, content, code);
                response.setContent(content);
                response.setState(state);

                if (needManualHandlerRedirect && isRedirectNeeded(method)) {

                    String redirectUrl = processRedirectResponse(method);
                    if (StringUtils.isNotEmpty(redirectUrl)) {
                        orgianlUrl = redirectUrl;
                        continue;
                    } else {
                        break;
                    }

                } else {
                    break;
                }

            } finally {
                method.releaseConnection();
            }
        }

        return response;
    }

    private String getRedirectURLInRefreshHeader(Header refreshHeader) {
        if (refreshHeader != null) {
            List<String> textUrls = TextUrlExtractor.extractor(refreshHeader.getValue(), Constant.URL_REGEX, 1);
            if (CollectionUtils.isNotEmpty(textUrls)) {
                return textUrls.get(0);
            }
        }
        return null;
    }

    private String processRedirectResponse(final HttpMethod method) throws RedirectException {
        String result = null;
        // get the location header to find out where to redirect to
        Header locationHeader = method.getResponseHeader("Location");
        locationHeader = locationHeader == null ? method.getResponseHeader("location") : locationHeader;
        Header refreshHeader = method.getResponseHeader("Refresh");
        refreshHeader = refreshHeader == null ? method.getResponseHeader("refresh") : refreshHeader;
        if (locationHeader == null && refreshHeader == null) {
            // got a redirect response, but no location header
            LOG.error("Received redirect response " + method.getStatusCode() + " but no location header");
            return result;
        }
        String orignal;
        try {
            orignal = method.getURI().toString();
            if (locationHeader != null) {
                result = UrlUtils.resolveUrl(orignal, locationHeader.getValue());
            }
            result = (result == null) ? getRedirectURLInRefreshHeader(refreshHeader) : result;
        } catch (Exception e) {
            LOG.error("handler redirce error!", e);
        }
        return result;
    }

    /**
     * Tests if the {@link HttpMethod method} requires a redirect to another location.
     * 
     * @param method HTTP method
     * 
     * @return boolean <tt>true</tt> if a retry is needed, <tt>false</tt> otherwise.
     */
    private boolean isRedirectNeeded(final HttpMethod method) {
        switch (method.getStatusCode()) {
            case HttpStatus.SC_MOVED_TEMPORARILY:
            case HttpStatus.SC_MOVED_PERMANENTLY:
            case HttpStatus.SC_SEE_OTHER:
            case HttpStatus.SC_TEMPORARY_REDIRECT:
                LOG.debug("Redirect required");
                return true;
            default:
                return false;
        } // end of switch
    }

    protected byte[] getResponseContent(String url, HttpMethod method, Metadata headers, byte[] content, int code) throws IOException {
        int contentLength = Integer.MAX_VALUE;
        String contentLengthString = headers.get(Response.CONTENT_LENGTH);
        if (contentLengthString != null) {
            try {
                contentLength = Integer.parseInt(contentLengthString.trim());
                if (contentLength == 0) {
                    contentLength = getMaxContent();
                }
            } catch (NumberFormatException ex) {
                // ignore
            }
        }

        // limit size
        if (getMaxContent() >= 0 && contentLength > getMaxContent()) {
            contentLength = getMaxContent();
        }

        // always read content. Sometimes content is useful to find a cause
        // for error.
        InputStream in = null;
        try {
            in = method.getResponseBodyAsStream();
            byte[] buffer = new byte[HttpBase.BUFFER_SIZE];
            int bufferFilled = 0;
            int totalRead = 0;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((bufferFilled = in.read(buffer, 0, buffer.length)) != -1 && totalRead + bufferFilled <= contentLength) {
                totalRead += bufferFilled;
                out.write(buffer, 0, bufferFilled);
            }

            // use read length if response header doesn't content length
            if (contentLengthString == null) {
                headers.set(Response.CONTENT_LENGTH, String.valueOf(totalRead));
            }
            content = out.toByteArray();
        } catch (Exception e) {
            if (code == 200) throw new IOException(e.toString());
            // for codes other than 200 OK, we are fine with empty content
        } finally {
            IOUtils.closeQuietly(in);
        }

        // Extract gzip, x-gzip and deflate content
        if (content != null) {
            // check if we have to uncompress it
            String contentEncoding = headers.get(Response.CONTENT_ENCODING);
            if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
                content = processGzipEncoded(content, url);
            } else if ("deflate".equals(contentEncoding)) {
                content = processDeflateEncoded(content, url);
            }
        }
        return content;
    }

    /**
     * 
     * @param state
     */
    private void printState(HttpState state) {
        if (LOG.isDebugEnabled() && state != null) {
            Cookie[] cks = state.getCookies();
            LOG.debug("cookie received..." + cks.length);
            for (Cookie cookie : cks) {
                LOG.debug("cookie:\t" + cookie.getName() + " value: " + cookie.getValue());
            }
        }
    }

    /**
     * 
     * @param input
     * @param method
     */
    private void fillMethodWithParameter(ProtocolInput input, HttpMethod method) {
        // add header
        boolean hasContentType = false;
        List<NameValuePair> headers = input.getHeaders();
        if (CollectionUtils.isNotEmpty(headers)) {
            for (NameValuePair hd : headers) {
                method.addRequestHeader(hd.getName(), hd.getValue());
                if (method instanceof PostMethod && "Content-Type".equals(hd.getName())) {
                    hasContentType = true;
                }
            }
        }

        if (method.getRequestHeader("Accept") == null) {
            // add default Accept
            method.addRequestHeader("Accept", accept);
        }

        if (method.getRequestHeader("User-Agent") == null) {
            // add default user agent
            method.addRequestHeader("User-Agent", userAgent);
        }

        if (!(method instanceof EntityEnclosingMethod)) {
            method.setFollowRedirects(input.getFollowRedirect());
        }

        long lastModified = input.getLastModify();
        // set coustom parameter... to do

        if (lastModified > 0) {
            method.setRequestHeader("If-Modified-Since", HttpDateFormat.toString(lastModified));
        }
        // set default post content type
        if (method instanceof PostMethod && !hasContentType) {
            method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        }

        // Set HTTP parameters
        HttpMethodParams params = method.getParams();
        if (this.getUseHttp11()) {
            params.setVersion(HttpVersion.HTTP_1_1);
        } else {
            params.setVersion(HttpVersion.HTTP_1_0);
        }
        // set SINGLE_COOKIE_HEADER true
        params.setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);

        if (StringUtils.isNotEmpty(input.getCookie()) && (input.getState() == null || ArrayUtils.isEmpty(input.getState().getCookies()))) {
            method.setRequestHeader("Cookie", input.getCookie());
        }
        
        params.setBooleanParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, input.getAllowCircularRedirects());
    }

    /**
     * create httpstate to hold cookie info
     * 
     * @param scope
     * @param cookie
     * @return
     */
    private HttpState getHttpState(CookieScope scope, ProtocolInput input) {
        HttpState state = input.getState();
        if (state == null) {
            if (StringUtils.isBlank(input.getCookie()) && scope == CookieScope.SESSION) {
                state = getClient().getState();
            } else {
                if (BooleanUtils.isTrue(scope.isRetainQuote())) {
                    state = new CustomHttpState();
                } else {
                    state = new HttpState();
                }
            }
            // input.setState(state);
        }
        return state;
    }



    protected HostConfiguration getHostConfiguration(String proxy) {
        HostConfiguration configuration = new HostConfiguration();
        ProxyHost host = null;
        if (StringUtils.isNotEmpty(proxy)) {

            if (!proxy.startsWith("http") && !proxy.startsWith("https")) {
                proxy = "http://" + proxy;
            }
            int port = 80;
            try {
                URI u = new URI(proxy);
                proxy = u.getHost() + u.getPath();
                port = u.getPort();
                host = new ProxyHost(proxy, port);
            } catch (Exception e) {
                LOG.error("parse proxy server error! " + proxy, e);
            }
        }
        configuration.setProxyHost(host);
        return configuration;
    }

}
