/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.crawler.plugin.util;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;

import com.datatrees.common.protocol.util.CookieFormater;
import com.treefinance.crawler.exception.UnexpectedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 19:30 10/01/2018
 */
public class HttpSender {

    private static final Logger                     LOGGER             = LoggerFactory.getLogger(HttpSender.class);
    private static final String[]                   DATE_PATTERNS      = {DateUtils.PATTERN_RFC1123, DateUtils.PATTERN_RFC1036, DateUtils.PATTERN_ASCTIME};
    private static final String                     CUSTOM_COOKIE_SPEC = "custom";
    private static       SSLConnectionSocketFactory SSL_SOCKET_FACTORY = null;

    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, (x509Certificates, s) -> true);
            SSLContext sslContext = builder.build();
            SSL_SOCKET_FACTORY = new SSLConnectionSocketFactory(sslContext, new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            LOGGER.error("init SSLConnectionSocketFactory error", e);
        }
    }

    private final    CloseableHttpClient httpClient;
    private final    CookieStore         cookieStore;
    private volatile String              userAgent;
    private volatile String              proxy;

    public HttpSender() {
        this(StringUtils.EMPTY);
    }

    public HttpSender(String userAgent) {
        this(userAgent, null);
    }

    public HttpSender(String userAgent, String proxy) {
        this(null, userAgent, proxy);
    }

    public HttpSender(CookieStore cookieStore, String userAgent, String proxy) {
        this.cookieStore = cookieStore == null ? new BasicCookieStore() : cookieStore;
        this.userAgent = userAgent;
        this.proxy = proxy;
        this.httpClient = getDefaultHttpClient(this.cookieStore);
    }

    /**
     * 获取默认的HttpClient对象.
     * @return {@link CloseableHttpClient}
     * @param cookieStore
     */
    private CloseableHttpClient getDefaultHttpClient(CookieStore cookieStore) {
        HttpClientBuilder httpBuilder = HttpClients.custom();
        // 设置连接池
        httpBuilder.setMaxConnTotal(200).setMaxConnPerRoute(100);
        // 设置默认的socket超时
        SocketConfig defaultSocketConfig = SocketConfig.custom().setSoTimeout(10000).build();
        httpBuilder.setDefaultSocketConfig(defaultSocketConfig);
        httpBuilder.setSSLSocketFactory(SSL_SOCKET_FACTORY);
        // 设置默认的请求
        RequestConfig defaultRequestConfig = requestBuilder(null).build();
        httpBuilder.setDefaultRequestConfig(defaultRequestConfig);
        httpBuilder.setDefaultCookieStore(cookieStore);

        try {
            // 自定义cookie解析
            DefaultCookieSpecProvider custom = new DefaultCookieSpecProvider(null, PublicSuffixMatcherLoader.getDefault(), DATE_PATTERNS, false);
            Registry<CookieSpecProvider> registry = CookieSpecRegistries.createDefaultBuilder().register(CUSTOM_COOKIE_SPEC, custom).build();
            httpBuilder.setDefaultCookieSpecRegistry(registry);

            HttpHelper.setDefaultRedirectStrategy(httpBuilder);
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }

        return httpBuilder.build();
    }

    private static RequestConfig.Builder requestBuilder(HttpHost proxy) {
        return RequestConfig.custom().setCookieSpec(CUSTOM_COOKIE_SPEC).setExpectContinueEnabled(true).setRedirectsEnabled(true).setCircularRedirectsAllowed(true).setMaxRedirects(15).setConnectionRequestTimeout(5000).setConnectTimeout(10000).setSocketTimeout(10000).setProxy(proxy);
    }

    public String send(String url) throws IOException {
        return send(url, null);
    }

    public String send(String url, String referer) throws IOException {
        CloseableHttpResponse response;

        for (int i = 0; ; i++) {
            try {
                response = sendRequest(url, referer);
                break;
            } catch (IOException e) {
                if (i < 2) {
                    if (e instanceof ConnectTimeoutException) {
                        if (this.proxy != null) {
                            this.proxy = null;
                        }
                        LOGGER.warn("The proxy was not connected. Drop proxy and retry! >>> {}", e.getMessage());
                        continue;
                    } else if (e instanceof SocketTimeoutException) {
                        LOGGER.warn("Retry to send request when socket timed out. >>> {}", e.getMessage());
                        continue;
                    }
                }

                throw e;
            }
        }

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (statusCode == HttpStatus.SC_OK) {
                Charset charset = getCharset(entity);
                if (charset == null) {
                    charset = Consts.UTF_8;
                }

                return EntityUtils.toString(entity, charset);
            }

            LOGGER.warn("Unexpected response after sending request[{}], response.statusCode: {}", url, statusCode);
            EntityUtils.consume(entity);

            return StringUtils.EMPTY;
        } catch (Exception e) {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException ex) {
                LOGGER.debug("I/O error while releasing connection", ex);
            }
            throw e;
        } finally {
            response.close();
        }
    }

    private Charset getCharset(HttpEntity entity) {
        try {
            ContentType contentType = ContentType.get(entity);
            return contentType.getCharset();
        } catch (ParseException | UnsupportedCharsetException e) {
            LOGGER.warn("Error parsing charset from response entity.", e);
        }

        return null;
    }

    private CloseableHttpResponse sendRequest(String url, String referer) throws IOException {
        HttpUriRequest request = buildRequest(url, referer, this.userAgent, this.proxy);

        return httpClient.execute(request);
    }

    private static HttpUriRequest buildRequest(String url, String referer, String userAgent, String proxy) {
        RequestBuilder requestBuilder = RequestBuilder.get(url);
        if (StringUtils.isNotEmpty(userAgent)) {
            requestBuilder.setHeader(HttpHeaders.USER_AGENT, userAgent);
        }
        if (StringUtils.isNotEmpty(referer)) {
            requestBuilder.setHeader(HttpHeaders.REFERER, referer);
        }

        if (StringUtils.isNotEmpty(proxy)) {
            HttpHost proxyHost = parseProxy(proxy);

            if (proxyHost != null) {
                RequestConfig requestConfig = requestBuilder(proxyHost).build();
                requestBuilder.setConfig(requestConfig);
            }
        }

        return requestBuilder.build();
    }

    private static HttpHost parseProxy(String proxy) {
        HttpHost proxyHost = null;
        String[] values = proxy.split(":");
        if (values.length == 2) {
            String host = values[0];
            int port = Integer.parseInt(values[1]);
            if (isConnectable(host, port, 5000)) {
                proxyHost = new HttpHost(host, port);
            }
        }
        return proxyHost;
    }

    private static boolean isConnectable(@Nonnull final String host, final int port, final int timeout) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port), timeout);
        } catch (IOException e) {
            LOGGER.warn("Error connecting socket to the server[" + host + ":" + port + "]", e);
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.warn("Unexpected exception when closing socket!", e);
            }
        }
        return true;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setCookies(Map<String, String> cookies, String... domains) {
        HttpHelper.addCookies(cookieStore, cookies, domains);
    }

    public void setCookies(String cookie, String... domains) {
        Map<String, String> cookieMap = CookieFormater.INSTANCE.parserCookieToMap(cookie);
        setCookies(cookieMap, domains);
    }

    public void close() {
        if (this.httpClient != null) {
            try {
                this.httpClient.close();
            } catch (IOException e) {
                LOGGER.error("Error closing http client.", e);
            }
        }
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
