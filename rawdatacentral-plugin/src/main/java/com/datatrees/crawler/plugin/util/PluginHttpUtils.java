package com.datatrees.crawler.plugin.util;

import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.domain.vo.Request;
import com.datatrees.rawdatacentral.share.ProxyService;
import com.treefinance.proxy.domain.Proxy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.CookieUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.codec.binary.Base64;
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
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouxinghai on 2017/7/13.
 */
public class PluginHttpUtils {

    private static Logger              logger          = LoggerFactory.getLogger(PluginHttpUtils.class);

    private static final RequestConfig CONFIG;

    public static final String         DEFAULT_CHARSET = "UTF-8";

    static {
        CONFIG = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build();
    }

    public static String getString(Long taskId, String remarkId, String url) {
        return executeString(new Request(taskId, remarkId, url));
    }

    public static String getString(Long taskId, String remarkId, String url, String referer) {
        Request request = new Request(taskId, remarkId, url);
        request.getHeader().put(HttpHeadKey.REFERER, referer);
        return executeString(request);
    }

    public static String postString(Long taskId, String remarkId, String url) throws IOException {
        Request request = new Request(taskId, remarkId, url);
        request.setType("post");
        return executeString(request);
    }

    /**
     * HTTP Post 获取内容
     *
     */
    public static String executeString(Request request) {
        CheckUtils.checkNotNull(request, "reques is null");
        CheckUtils.checkNotNull(request.getTaskId(), "taskId is null");
        CloseableHttpResponse response = null;
        BasicCookieStore cookieStore = getCookie(request.getTaskId());
        request.setSendCookies(getCookieString(cookieStore));
        HttpHost proxy = null;
        Proxy proxyConfig = getProxy(request.getTaskId(), null);
        if (null != proxyConfig) {
            proxy = new HttpHost(proxyConfig.getId().toString(), Integer.parseInt(proxyConfig.getPort()),
                request.getProtocol());
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

            HttpRequestBase client = StringUtils.equalsIgnoreCase("post", request.getType()) ? new HttpPost(url)
                : new HttpGet(url);
            if (CollectionUtils.isNotEmpty(request.getHeader())) {
                for (Map.Entry<String, String> entry : request.getHeader().entrySet()) {
                    client.setHeader(entry.getKey(), entry.getValue());
                }
            }
            client.setHeader(HttpHeadKey.CONTENT_TYPE, request.getContentType());
            response = httpclient.execute(client);
            int statusCode = response.getStatusLine().getStatusCode();

            request.setStatusCode(statusCode);
            request.setReceiveCookies(getReceiveCookieString(request.getSendCookies(), cookieStore));
            if (statusCode != 200) {
                client.abort();
                throw new RuntimeException("HttpClient doPost error, statusCode: " + statusCode);
            }
            saveCookie(request.getTaskId(), cookieStore);
            byte[] data = EntityUtils.toByteArray(response.getEntity());
            request.setResponse(data);

            String pageContent = StringUtils.equalsIgnoreCase("base64", request.getCharsetName())
                ? Base64.encodeBase64String(data) : IOUtils.toString(data, request.getCharsetName());
            request.setPageContent(pageContent);
            return pageContent;
        } catch (Exception e) {
            logger.error("http error request={}", JSON.toJSONString(request), e);
            throw new RuntimeException("http error request=" + JSON.toJSONString(request), e);
        } finally {
            IOUtils.closeQuietly(httpclient);
            IOUtils.closeQuietly(response);
            RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);
            redisService.saveToList(RedisKeyPrefixEnum.TASK_REQUEST.getRedisKey(request.getTaskId()),
                JSON.toJSONString(request), 1, TimeUnit.DAYS);
        }
    }

    public static BasicCookieStore getCookie(Long taskId) {
        CheckUtils.checkNotNull(taskId, "taskId is null");
        RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);
        BasicCookieStore cookieStore = new BasicCookieStore();
        List<com.datatrees.rawdatacentral.domain.vo.Cookie> cookies = null;
        String cacheKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId + "");
        String json = redisService.getString(cacheKey);
        if (StringUtils.isNoneBlank(json)) {
            cookies = JSON.parseObject(json, new TypeReference<List<com.datatrees.rawdatacentral.domain.vo.Cookie>>() {
            });
        }
        if (null == cookies || cookies.isEmpty()) {
            return cookieStore;
        }
        for (com.datatrees.rawdatacentral.domain.vo.Cookie myCookie : cookies) {
            cookieStore.addCookie(CookieUtils.getBasicClientCookie(myCookie));
        }
        return cookieStore;
    }

    public static String getCookieValue(Long taskId, String cookieName) {
        BasicCookieStore cookieStore = getCookie(taskId);
        if (null != cookieStore && null != cookieStore.getCookies()) {
            for (Cookie cookie : cookieStore.getCookies()) {
                if (StringUtils.equals(cookieName, cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        logger.warn("not found cookieName={}", cookieName);
        return null;
    }

    public static String getCookieString(Long taskId) {
        StringBuilder sb = new StringBuilder();
        BasicCookieStore cookieStore = getCookie(taskId);
        if (null != cookieStore && null != cookieStore.getCookies()) {
            for (Cookie cookie : cookieStore.getCookies()) {
                sb.append(";").append(cookie.getName()).append("=").append(cookie.getValue());
            }
        }
        if (StringUtils.isBlank(sb)) {
            return "";
        }
        return sb.substring(1);
    }

    public static String getReceiveCookieString(String sendCookies, BasicCookieStore cookieStore) {
        if (StringUtils.isBlank(sendCookies)) {
            return getCookieString(cookieStore);
        }
        StringBuilder sb = new StringBuilder();
        if (null != cookieStore && null != cookieStore.getCookies()) {
            for (Cookie cookie : cookieStore.getCookies()) {
                if (!sendCookies.contains(cookie.getName() + "=")) {
                    sb.append(";").append(cookie.getName()).append("=").append(cookie.getValue());
                }
            }
        }
        if (StringUtils.isBlank(sb)) {
            return "";
        }
        return sb.substring(1);
    }

    public static String getCookieString(BasicCookieStore cookieStore) {
        StringBuilder sb = new StringBuilder();
        if (null != cookieStore && null != cookieStore.getCookies()) {
            for (Cookie cookie : cookieStore.getCookies()) {
                sb.append(";").append(cookie.getName()).append("=").append(cookie.getValue());
            }
        }
        if (StringUtils.isBlank(sb)) {
            return "";
        }
        return sb.substring(1);
    }

    public static void saveCookie(Long taskId, BasicCookieStore cookieStore) {
        CheckUtils.checkNotNull(taskId, "taskId is null");
        CheckUtils.checkNotNull(cookieStore, "cookieStore is null");
        List<com.datatrees.rawdatacentral.domain.vo.Cookie> list = CookieUtils.getCookies(cookieStore);
        BeanFactoryUtils.getBean(RedisService.class).cache(RedisKeyPrefixEnum.TASK_COOKIE, String.valueOf(taskId),
            list);
    }

    public static HttpResult<Map<String, Object>> refeshPicCodePicCode(Long taskId, String websiteName, String remarkId,
                                                                       String url, String returnName, String formType) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            Request request = new Request(taskId, remarkId, url);
            request.setCharsetName("base64");
            String picCode = executeString(request);
            Map<String, Object> map = new HashMap<>();
            map.put(returnName, picCode);
            logger.info("{}-->图片验证码-->刷新成功,taskId={},websiteName={},formType={},url={}", FormType.getName(formType),
                taskId, websiteName, formType, url);
            return result.success(map);
        } catch (Exception e) {
            logger.error("{}-->图片验证码-->刷新失败 error taskId={},websiteName={},formType={},url={}",
                FormType.getName(formType), taskId, websiteName, formType, url, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    public static Proxy getProxy(Long taskId, String websiteName) {
        CheckUtils.checkNotNull(taskId, "taskId is null");
        Proxy proxy = null;
        try {
            proxy = BeanFactoryUtils.getBean(ProxyService.class).getProxy(taskId, websiteName);
        } catch (Exception e) {
            logger.error("getProxy error taskId={},websiteName={}", taskId, websiteName, e);
        }
        return proxy;
    }

}
