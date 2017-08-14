package com.datatrees.rawdatacentral.common.utils;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.vo.Request;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.treefinance.proxy.domain.Proxy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TaskHttpClient {

    private static final Logger        logger          = LoggerFactory.getLogger(TaskHttpClient.class);

    private Request                    request;

    private Response                   response;

    private static final RequestConfig CONFIG;

    private static final String        DEFAULT_CHARSET = "UTF-8";

    static {
        CONFIG = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build();
    }

    private TaskHttpClient(Request request) {
        this.request = request;
        this.response = new Response(request);
    }

    public static TaskHttpClient create(Long taskId, String websiteName, RequestType requestType, String remark) {
        Request request = new Request();
        request.setTaskId(taskId);
        request.setWebsiteName(websiteName);
        request.setRequestType(requestType);
        request.setRemarkId(remark);
        TaskHttpClient client = new TaskHttpClient(request);
        return client;
    }

    public static TaskHttpClient create(OperatorParam operatorParam, RequestType requestType, String remark) {
        Request request = new Request();
        request.setTaskId(operatorParam.getTaskId());
        request.setWebsiteName(operatorParam.getWebsiteName());
        request.setRequestType(requestType);
        request.setRemarkId(remark);
        TaskHttpClient client = new TaskHttpClient(request);
        return client;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public TaskHttpClient setFullUrl(String url) {
        request.setFullUrl(url);
        return this;
    }

    public TaskHttpClient setFullUrl(String templateUrl, Object... params) {
        String fullUrl = TemplateUtils.format(templateUrl, params);
        request.setFullUrl(fullUrl);
        return this;
    }

    public TaskHttpClient setUrl(String url) {
        request.setUrl(url);
        return this;
    }

    public TaskHttpClient setParams(Map<String, String> params) {
        request.setParams(params);
        return this;
    }

    public TaskHttpClient setReferer(String referer) {
        request.getHeader().put(HttpHeadKey.REFERER, referer);
        return this;
    }

    public Response invoke() {
        checkRequest(request);
        CloseableHttpResponse httpResponse = null;
        BasicCookieStore cookieStore = CookieUtils.getCookie(request.getTaskId());
        request.setSendCookies(CookieUtils.getCookieString(cookieStore));
        HttpHost proxy = null;
        Proxy proxyConfig = ProxyUtils.getProxy(request.getTaskId(), null);
        if (null != proxyConfig) {
            proxy = new HttpHost(proxyConfig.getId().toString(), Integer.parseInt(proxyConfig.getPort()),
                request.getProtocol());
            request.setProxy(proxyConfig.getId() + ":" + proxyConfig.getPort());
        }
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(CONFIG).setProxy(proxy)
            .setDefaultCookieStore(cookieStore).build();

        try {
            //参数处理
            String url = null;
            if (CollectionUtils.isEmpty(request.getParams())) {
                url = StringUtils.isNoneBlank(request.getFullUrl()) ? request.getFullUrl() : request.getUrl();
            } else {
                CheckUtils.checkNotBlank(request.getUrl(), "url is blank");
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(request.getParams().size());
                for (Map.Entry<String, String> entry : request.getParams().entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url = request.getUrl() + "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, DEFAULT_CHARSET));
            }

            HttpRequestBase client = RequestType.POST == request.getRequestType() ? new HttpPost(url)
                : new HttpGet(url);
            if (CollectionUtils.isNotEmpty(request.getHeader())) {
                for (Map.Entry<String, String> entry : request.getHeader().entrySet()) {
                    client.setHeader(entry.getKey(), entry.getValue());
                }
            }
            client.setHeader(HttpHeadKey.CONTENT_TYPE, request.getContentType());
            request.setRequestTimestamp(System.currentTimeMillis());
            httpResponse = httpclient.execute(client);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            response.setStatusCode(statusCode);
            response.setReceiveCookies(CookieUtils.getReceiveCookieString(request.getSendCookies(), cookieStore));
            if (statusCode != 200) {
                client.abort();
                throw new RuntimeException("HttpClient doPost error, statusCode: " + statusCode);
            }
            CookieUtils.saveCookie(request.getTaskId(), cookieStore);
            //            httpResponse.getAllHeaders()
            byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
            response.setResponse(data);
        } catch (Exception e) {
            logger.error("http error request={}", JSON.toJSONString(request), e);
            throw new RuntimeException("http error request=" + JSON.toJSONString(request), e);
        } finally {
            IOUtils.closeQuietly(httpclient);
            IOUtils.closeQuietly(httpResponse);
            RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);
            redisService.saveToList(RedisKeyPrefixEnum.TASK_REQUEST.getRedisKey(request.getTaskId()),
                JSON.toJSONString(response), 1, TimeUnit.DAYS);
        }
        return response;
    }

    private void checkRequest(Request request) {
        CheckUtils.checkNotNull(request, "request is null");
        CheckUtils.checkNotPositiveNumber(request.getTaskId(), ErrorCode.EMPTY_TASK_ID);
        CheckUtils.checkNotBlank(request.getRemarkId(), "remarkId is empty");
        if (StringUtils.isBlank(request.getUrl()) && StringUtils.isBlank(request.getFullUrl())) {
            throw new RuntimeException("url and fullUrl is blank");
        }
    }

}
