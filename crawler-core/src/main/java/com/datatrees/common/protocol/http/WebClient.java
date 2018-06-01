/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol.http;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.*;
import com.datatrees.common.protocol.NameValuePair;
import com.datatrees.common.protocol.ProtocolException;
import com.datatrees.common.protocol.ProtocolInput.Action;
import com.datatrees.common.protocol.ProtocolInput.CookieScope;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
    private String defaultUsername;
    private String defaultPassword;
    private String defaultRealm;
    private String defaultScheme;
    private String authFile;
    private String agentHost;
    private boolean authRulesRead = false;

    int maxThreadsTotal = 400;
    int maxThreadsPerHost = 200;
    int maxRedirect = 10;

    private String proxyUsername;
    private String proxyPassword;
    private String proxyRealm;

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
        authFile = conf.get("http.auth.file", "");
        configureClient();
        try {
            // setCredentials();
        } catch (Exception ex) {
            LOG.error("Could not read " + authFile + " : " + ex.getMessage());

        }
    }

    /**
     * Fetches the <code>url</code> with a configured HTTP client and gets the response.
     * 
     * @param url URL to be fetched
     * @param datum Crawl data
     * @param redirect Follow redirects if and only if true
     * @return HTTP response
     */
    protected Response getResponse(URL url, long lastModified, boolean redirect) throws ProtocolException, IOException {
        resolveCredentials(url);
        return getResponse(new ProtocolInput().setUrl(url.toString()).setLastModify(lastModified).setFollowRedirect(redirect));
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
     * Reads authentication configuration file (defined as 'http.auth.file' in Nutch configuration
     * file) and sets the credentials for the configured authentication scopes in the HTTP client
     * object.
     * 
     * @throws ParserConfigurationException If a document builder can not be created.
     * @throws SAXException If any parsing error occurs.
     * @throws IOException If any I/O error occurs.
     */
    private synchronized void setCredentials() throws ParserConfigurationException, SAXException, IOException {

        if (authRulesRead) return;

        authRulesRead = true; // Avoid re-attempting to read

        InputStream is = conf.getConfResourceAsInputStream(authFile);
        if (is != null) {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

            Element rootElement = doc.getDocumentElement();
            if (!"auth-configuration".equals(rootElement.getTagName())) {
                LOG.warn("Bad auth conf file: root element <" + rootElement.getTagName() + "> found in " + authFile
                        + " - must be <auth-configuration>");
            }

            // For each set of credentials
            NodeList credList = rootElement.getChildNodes();
            for (int i = 0; i < credList.getLength(); i++) {
                Node credNode = credList.item(i);
                if (!(credNode instanceof Element)) continue;

                Element credElement = (Element) credNode;
                if (!"credentials".equals(credElement.getTagName())) {
                    LOG.warn("Bad auth conf file: Element <" + credElement.getTagName() + "> not recognized in " + authFile
                            + " - expected <credentials>");
                    continue;
                }

                String username = credElement.getAttribute("username");
                String password = credElement.getAttribute("password");

                // For each authentication scope
                NodeList scopeList = credElement.getChildNodes();
                for (int j = 0; j < scopeList.getLength(); j++) {
                    Node scopeNode = scopeList.item(j);
                    if (!(scopeNode instanceof Element)) continue;

                    Element scopeElement = (Element) scopeNode;

                    if ("default".equals(scopeElement.getTagName())) {

                        // Determine realm and scheme, if any
                        String realm = scopeElement.getAttribute("realm");
                        String scheme = scopeElement.getAttribute("scheme");

                        // Set default credentials
                        defaultUsername = username;
                        defaultPassword = password;
                        defaultRealm = realm;
                        defaultScheme = scheme;

                        LOG.trace("Credentials - username: " + username + "; set as default" + " for realm: " + realm + "; scheme: " + scheme);

                    } else if ("authscope".equals(scopeElement.getTagName())) {

                        // Determine authentication scope details
                        String host = scopeElement.getAttribute("host");
                        int port = -1; // For setting port to AuthScope.ANY_PORT
                        try {
                            port = Integer.parseInt(scopeElement.getAttribute("port"));
                        } catch (Exception ex) {
                            // do nothing, port is already set to any port
                        }
                        String realm = scopeElement.getAttribute("realm");
                        String scheme = scopeElement.getAttribute("scheme");

                        // Set credentials for the determined scope
                        AuthScope authScope = getAuthScope(host, port, realm, scheme);
                        NTCredentials credentials = new NTCredentials(username, password, agentHost, realm);

                        client.getState().setCredentials(authScope, credentials);

                        LOG.trace("Credentials - username: " + username + "; set for AuthScope - " + "host: " + host + "; port: " + port
                                + "; realm: " + realm + "; scheme: " + scheme);

                    } else {
                        LOG.warn("Bad auth conf file: Element <" + scopeElement.getTagName() + "> not recognized in " + authFile
                                + " - expected <authscope>");
                    }
                }
                is.close();
            }
        }
    }

    /**
     * If credentials for the authentication scope determined from the specified <code>url</code> is
     * not already set in the HTTP client, then this method sets the default credentials to fetch
     * the specified <code>url</code>. If credentials are found for the authentication scope, the
     * method returns without altering the client.
     * 
     * @param url URL to be fetched
     */
    private void resolveCredentials(URL url) {

        if (defaultUsername != null && defaultUsername.length() > 0) {

            int port = url.getPort();
            if (port == -1) {
                if ("https".equals(url.getProtocol()))
                    port = 443;
                else
                    port = 80;
            }

            AuthScope scope = new AuthScope(url.getHost(), port);

            if (client.getState().getCredentials(scope) != null) {
                LOG.trace("Pre-configured credentials with scope - host: " + url.getHost() + "; port: " + port + "; found for url: " + url);

                // Credentials are already configured, so do nothing and return
                return;
            }

            LOG.trace("Pre-configured credentials with scope -  host: " + url.getHost() + "; port: " + port + "; not found for url: " + url);

            AuthScope serverAuthScope = getAuthScope(url.getHost(), port, defaultRealm, defaultScheme);

            NTCredentials serverCredentials = new NTCredentials(defaultUsername, defaultPassword, agentHost, defaultRealm);

            client.getState().setCredentials(serverAuthScope, serverCredentials);
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
