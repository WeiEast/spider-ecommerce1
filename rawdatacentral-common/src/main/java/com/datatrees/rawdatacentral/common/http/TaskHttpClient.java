package com.datatrees.rawdatacentral.common.http;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.common.utils.*;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.vo.Cookie;
import com.datatrees.rawdatacentral.domain.vo.NameValue;
import com.datatrees.rawdatacentral.domain.vo.Request;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.treefinance.proxy.domain.Proxy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskHttpClient {

    private static final Logger                     logger = LoggerFactory.getLogger(TaskHttpClient.class);
    private static       SSLConnectionSocketFactory sslsf  = null;//海南电信,重定向要忽略证书

    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"}, null,
                    NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            logger.error("init SSLConnectionSocketFactory error", e);
        }
    }

    private Request     request;
    private Response    response;
    private ContentType requestContentType;
    private ContentType responseContentType;
    private boolean isRedirect = false;//是否重定向了
    private CredentialsProvider credsProvider;
    /**
     * 自定义的cookie
     */
    private List<BasicClientCookie> extralCookie = new ArrayList<>();

    private TaskHttpClient(Request request) {
        this.request = request;
        this.response = new Response(request);
    }

    public static TaskHttpClient create(Long taskId, String websiteName, RequestType requestType, String remarkId) {
        return create(taskId, websiteName, requestType, remarkId, false);
    }

    public static TaskHttpClient create(OperatorParam operatorParam, RequestType requestType, String remarkId) {
        return create(operatorParam.getTaskId(), operatorParam.getWebsiteName(), requestType, remarkId, false);
    }

    public static TaskHttpClient create(Long taskId, String websiteName, RequestType requestType, String remarkId, boolean isRedirect) {
        Request request = new Request();
        request.setTaskId(taskId);
        request.setWebsiteName(websiteName);
        request.setType(requestType);
        request.setRemarkId(remarkId);
        request.setRedirect(isRedirect);
        TaskHttpClient client = new TaskHttpClient(request);
        return client;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public TaskHttpClient setCredsProvider(CredentialsProvider credsProvider) {
        this.credsProvider = credsProvider;
        return this;
    }

    public TaskHttpClient removeHeader(String name) {
        request.getHeaders().remove(name);
        return this;
    }

    public TaskHttpClient addHeader(String name, String value) {
        request.addHead(name, value);
        return this;
    }

    public TaskHttpClient addHeaders(Map<String, String> headers) {
        if (CollectionUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.addHead(header.getKey(), header.getValue());
            }
        }
        return this;
    }

    public TaskHttpClient setFullUrl(String url) {
        request.setUrl(url);
        return this;
    }

    public TaskHttpClient setFullUrl(String templateUrl, Object... params) {
        String fullUrl = TemplateUtils.format(templateUrl, params);
        request.setUrl(fullUrl);
        return this;
    }

    public TaskHttpClient setUrl(String url) {
        request.setUrl(url);
        return this;
    }

    public TaskHttpClient setParams(Map<String, Object> params) {
        request.setParams(params);
        return this;
    }

    public TaskHttpClient setReferer(String referer) {
        if (StringUtils.isBlank(referer)) {
            logger.warn("referer is blank,taskId={},websiteName={}", request.getTaskId(), request.getWebsiteName());
            return this;
        }
        request.addHead(HttpHeadKey.REFERER, referer);
        return this;
    }

    public TaskHttpClient setReferer(String referer, Object... params) {
        request.addHead(HttpHeadKey.REFERER, TemplateUtils.format(referer, params));
        return this;
    }

    public TaskHttpClient setResponseCharset(Charset charset) {
        response.setCharset(charset);
        return this;
    }

    public TaskHttpClient setConnectTimeout(int connectTimeout) {
        request.setConnectTimeout(connectTimeout);
        return this;
    }

    public TaskHttpClient setSocketTimeout(int socketTimeout) {
        request.setSocketTimeout(socketTimeout);
        return this;
    }

    public TaskHttpClient setMaxRetry(int maxRetry) {
        request.setMaxRetry(maxRetry);
        return this;
    }

    public TaskHttpClient setProxyEnable(boolean proxyEnable) {
        request.setProxyEnable(proxyEnable);
        return this;
    }

    public TaskHttpClient setRequestBody(String requestBody, ContentType contentType) {
        this.requestContentType = contentType;
        request.setType(RequestType.POST);
        request.setRequestBodyContent(requestBody);
        if (null != contentType) {
            request.setCharset(contentType.getCharset());
            request.setContentType(contentType.toString());
        }
        return this;
    }

    public TaskHttpClient setRequestBody(String requestBody) {
        request.setType(RequestType.POST);
        request.setRequestBodyContent(requestBody);
        return this;
    }

    public TaskHttpClient setRequestContentType(ContentType contentType) {
        this.requestContentType = contentType;
        if (null != contentType) {
            request.setCharset(contentType.getCharset());
            request.setContentType(contentType.toString());
        }
        return this;
    }

    public TaskHttpClient setResponseContentType(ContentType contentType) {
        this.responseContentType = contentType;
        if (null != contentType) {
            response.setCharset(contentType.getCharset());
            response.setContentType(contentType.toString());
        }
        return this;
    }

    public TaskHttpClient setRequestCharset(Charset charset) {
        request.setCharset(charset);
        return this;
    }

    public TaskHttpClient setDefaultResponseCharset(String defaultResponseCharset) {
        if (StringUtils.isNotBlank(defaultResponseCharset)) {
            request.setDefaultResponseCharset(defaultResponseCharset);
        }
        return this;
    }

    public TaskHttpClient setAutoRedirect(Boolean autoRedirect) {
        if (null != autoRedirect) {
            request.setAutoRedirect(autoRedirect);
        }
        return this;
    }

    public TaskHttpClient addExtralCookie(String domain, String name, String value) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setVersion(0);
        cookie.setExpiryDate(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)));
        cookie.setAttribute("path", "/");
        cookie.setAttribute("domain", domain);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        cookie.setAttribute("expires", sdf.format(cookie.getExpiryDate()));
        request.getExtralCookie().put(name, value);
        extralCookie.add(cookie);
        return this;
    }

    public Response invoke() {
        checkRequest(request);
        Long taskId = request.getTaskId();
        request.setRequestId(RequestIdUtils.createId());
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse httpResponse = null;
        int statusCode = 0;
        try {
            //参数处理
            String url = request.getUrl();
            if (CollectionUtils.isNotEmpty(request.getParams())) {
                CheckUtils.checkNotBlank(request.getUrl(), "url is blank");
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(request.getParams().size());
                for (Map.Entry<String, Object> entry : request.getParams().entrySet()) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), null == entry.getValue() ? "" : String.valueOf(entry.getValue())));
                }
                url = request.getUrl() + "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, request.getCharset()));
            }
            request.setFullUrl(url);
            logger.info("pre request taskId={},url={}", taskId, url);

            HttpRequestBase client = null;
            URI uri = URIUtils.create(url);
            if (RequestType.GET == request.getType()) {
                client = new HttpGet(uri);
            } else {
                HttpPost httpPost = new HttpPost(uri);
                if (StringUtils.isNoneBlank(request.getRequestBodyContent())) {
                    httpPost.setEntity(new StringEntity(request.getRequestBodyContent(), requestContentType));
                }
                client = httpPost;
            }
            if (StringUtils.isNoneBlank(request.getContentType())) {
                if (!request.containHeader(HttpHeadKey.CONTENT_TYPE)) {
                    request.addHead(HttpHeadKey.CONTENT_TYPE, request.getContentType());
                }
            }
            if (CollectionUtils.isNotEmpty(request.getHeaders())) {
                for (NameValue nameValue : request.getHeaders()) {
                    client.setHeader(nameValue.getName(), String.valueOf(nameValue.getValue()));
                }
            }
            request.setRequestTimestamp(System.currentTimeMillis());

            String host = client.getURI().getHost();
            request.setHost(host);
            request.setProtocol(url.startsWith("https") ? "https" : "http");

            List<Cookie> cookies = TaskUtils.getCookies(taskId, host);
            BasicCookieStore cookieStore = TaskUtils.buildBasicCookieStore(cookies);
            request.setRequestCookies(TaskUtils.getCookieMap(cookies));
            if (!extralCookie.isEmpty()) {
                for (BasicClientCookie cookie : extralCookie) {
                    cookieStore.addCookie(cookie);
                }
            }

            HttpHost proxy = null;
            if (null == request.getProxyEnable()) {
                request.setProxyEnable(ProxyUtils.getProxyEnable(taskId));
            }
            if (request.getProxyEnable()) {
                Proxy proxyConfig = ProxyUtils.getProxy(taskId, request.getWebsiteName());
                if (null != proxyConfig) {
                    proxy = new HttpHost(proxyConfig.getIp(), Integer.parseInt(proxyConfig.getPort()));
                    request.setProxy(proxyConfig.getIp() + ":" + proxyConfig.getPort());
                }
            }

            //禁止重定向
            RequestConfig config = RequestConfig.custom().setRedirectsEnabled(false).setConnectTimeout(request.getConnectTimeout())
                    .setSocketTimeout(request.getSocketTimeout()).setCookieSpec(CookieSpecs.DEFAULT).build();
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(config).setProxy(proxy)
                    .setDefaultCookieStore(cookieStore).setDefaultCredentialsProvider(credsProvider).setSSLSocketFactory(sslsf);
            if (null != credsProvider) {
                httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
            }
            httpclient = httpClientBuilder.build();
            httpResponse = httpclient.execute(client);

            statusCode = httpResponse.getStatusLine().getStatusCode();
            response.setStatusCode(statusCode);
            cookies = TaskUtils.getCookies(taskId, host, cookieStore, httpResponse);
            TaskUtils.saveCookie(taskId, cookies);
            response.setResponseCookies(TaskUtils.getResponseCookieMap(request, cookies));
            long totalTime = System.currentTimeMillis() - request.getRequestTimestamp();
            TaskUtils.saveCookie(taskId, host, cookieStore, httpResponse);
            response.setTotalTime(totalTime);
            Header[] headers = httpResponse.getAllHeaders();
            if (null != headers) {
                for (Header header : headers) {
                    response.getHeaders().add(new NameValue(header.getName(), header.getValue()));
                }
            }
            Header header = httpResponse.getFirstHeader(HttpHeadKey.CONTENT_TYPE);
            if (null != header) {
                String contentType = header.getValue();
                if (StringUtils.isBlank(response.getContentType())) {
                    response.setContentType(contentType);
                }
                if (StringUtils.isNoneBlank(contentType) && StringUtils.contains(contentType, "charset")) {
                    String charset = RegexpUtils.select(contentType, "charset=(.+)", 1);
                    if (StringUtils.isNoneBlank(charset)) {
                        response.setCharset(Charset.forName(charset.replaceAll(";", "").trim()));
                    }
                }
            }
            if (statusCode >= 200 && statusCode <= 299) {
                byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
                response.setResponse(data);
            } else if (statusCode >= 300 && statusCode <= 399) {
                String redirectUrl = httpResponse.getFirstHeader(HttpHeadKey.LOCATION).getValue();
                response.setRedirectUrl(redirectUrl);
                response.setRedirect(true);
                logger.warn("HttpClient has redirect,taskId={},url={}, statusCode={},redirectUrl={}", taskId, url, statusCode, redirectUrl);
            } else {
                client.abort();
                logger.error("HttpClient status error,taskId={},url={}, statusCode={}", taskId, url, statusCode);
            }
        } catch (SocketTimeoutException e) {
            if (request.getRetry().getAndIncrement() < request.getMaxRetry()) {
                logger.error("http timeout ,will retry request={}", request);
                return invoke();
            }
            logger.error("http timout,retry={},maxRetry={}, request={}", request.getRetry(), request.getMaxRetry(), request, e);
            throw new RuntimeException("http timeout,request=" + request, e);
        } catch (Throwable e) {
            logger.error("http error request={}", request, e);
            throw new RuntimeException("http error request=" + request, e);
        } finally {
            IOUtils.closeQuietly(httpclient);
            IOUtils.closeQuietly(httpResponse);
            //保存请求
            RedisUtils.rpush(RedisKeyPrefixEnum.TASK_REQUEST.getRedisKey(request.getTaskId()), JSON.toJSONString(response));
            RedisUtils.expire(RedisKeyPrefixEnum.TASK_REQUEST.getRedisKey(request.getTaskId()), RedisKeyPrefixEnum.TASK_REQUEST.toSeconds());
            //保存请求内容
            StringBuilder pc = new StringBuilder("url-->").append(request.getFullUrl()).append("\nrequest_id-->").append(request.getRequestId())
                    .append("\nstatus_code-->").append(response.getStatusCode()).append("\nreques_time-->")
                    .append(DateUtils.formatYmdhms(new Date(request.getRequestTimestamp()))).append("\n").append(response.getPageContent());
            RedisUtils.rpush(RedisKeyPrefixEnum.TASK_PAGE_CONTENT.getRedisKey(request.getTaskId()), pc.toString());
            RedisUtils
                    .expire(RedisKeyPrefixEnum.TASK_PAGE_CONTENT.getRedisKey(request.getTaskId()), RedisKeyPrefixEnum.TASK_PAGE_CONTENT.toSeconds());
        }
        if (request.getAutoRedirect() && statusCode >= 300 && statusCode <= 399) {
            String redirectUrl = response.getRedirectUrl();
            if (!redirectUrl.startsWith("http") && !redirectUrl.startsWith("www")) {
                if (redirectUrl.startsWith("/")) {
                    redirectUrl = request.getProtocol() + "://" + request.getHost() + redirectUrl;
                } else {
                    redirectUrl = request.getProtocol() + "://" + request.getHost() + "/" + redirectUrl;
                }

            }
            logger.info("http has redirect,taskId={},websiteName={},type={},from={} to redirectUrl={}", taskId, request.getWebsiteName(),
                    request.getType(), request.getUrl(), redirectUrl);
            response = create(request.getTaskId(), request.getWebsiteName(), RequestType.GET, request.getRemarkId(), true).setUrl(redirectUrl)
                    .invoke();
        }
        return response;
    }

    private void checkRequest(Request request) {
        CheckUtils.checkNotNull(request, "request is null");
        CheckUtils.checkNotPositiveNumber(request.getTaskId(), ErrorCode.EMPTY_TASK_ID);
        //CheckUtils.checkNotBlank(request.getRemarkId(), "remarkId is empty");
        if (StringUtils.isBlank(request.getUrl())) {
            throw new RuntimeException("url  is blank");
        }
    }

}
