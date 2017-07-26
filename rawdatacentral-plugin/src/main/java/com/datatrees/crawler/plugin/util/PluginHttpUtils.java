package com.datatrees.crawler.plugin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.CookieUtils;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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

    enum MethodType {
                     GET, POST;
    }

    public static String getString(String url, Long taskId) throws IOException {
        return IOUtils.toString(execute(MethodType.GET, url, null, null, taskId), DEFAULT_CHARSET);
    }

    public static byte[] doGet(String url, Long taskId) throws IOException {
        return execute(MethodType.GET, url, null, null, taskId);
    }

    public static String getString(String url, String referer, Long taskId) throws IOException {
        Map<String, String> header = new HashMap<>();
        header.put(HttpHeadKey.REFERER, referer);
        return IOUtils.toString(execute(MethodType.GET, url, null, header, taskId), DEFAULT_CHARSET);
    }

    public static String postString(String url, Long taskId) throws IOException {
        return IOUtils.toString(execute(MethodType.POST, url, null, null, taskId), DEFAULT_CHARSET);
    }

    public static String postString(String url, String referer, Long taskId) throws IOException {
        Map<String, String> header = new HashMap<>();
        header.put(HttpHeadKey.REFERER, referer);
        return IOUtils.toString(execute(MethodType.POST, url, null, header, taskId), DEFAULT_CHARSET);
    }

    public static String postString(String url, Map<String, String> params, String referer,
                                    Long taskId) throws IOException {
        Map<String, String> header = new HashMap<>();
        header.put(HttpHeadKey.REFERER, referer);
        return IOUtils.toString(execute(MethodType.POST, url, params, header, taskId), DEFAULT_CHARSET);
    }

    public static String postString(String url, Map<String, String> params, String referer, Long taskId,
                                    String charsetName) throws IOException {
        if (StringUtils.isBlank(charsetName)) {
            charsetName = DEFAULT_CHARSET;
        }
        Map<String, String> header = new HashMap<>();
        header.put(HttpHeadKey.REFERER, referer);
        return IOUtils.toString(execute(MethodType.POST, url, params, header, taskId), charsetName);
    }

    /**
     * HTTP get 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String getString(String url, Map<String, String> params, Long taskId) throws IOException {
        return IOUtils.toString(execute(MethodType.GET, url, params, null, taskId), DEFAULT_CHARSET);
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String postString(String url, Map<String, String> params, Long taskId) throws IOException {
        return IOUtils.toString(execute(MethodType.POST, url, params, null, taskId), DEFAULT_CHARSET);
    }

    /**
     * HTTP get 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String getString(String url, Map<String, String> params, Map<String, String> header,
                                   Long taskId) throws IOException {
        return IOUtils.toString(execute(MethodType.GET, url, params, header, taskId), DEFAULT_CHARSET);
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String postString(String url, Map<String, String> params, Map<String, String> header,
                                    Long taskId) throws IOException {
        return IOUtils.toString(execute(MethodType.POST, url, params, header, taskId), DEFAULT_CHARSET);
    }

    /**
     * HTTP get 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String getString(String url, Map<String, String> params, Map<String, String> header, Long taskId,
                                   String charsetName) throws IOException {
        if (StringUtils.isBlank(charsetName)) {
            charsetName = DEFAULT_CHARSET;
        }
        return IOUtils.toString(execute(MethodType.GET, url, params, header, taskId), charsetName);
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String postString(String url, Map<String, String> params, Map<String, String> header, Long taskId,
                                    String charsetName) throws IOException {
        return IOUtils.toString(execute(MethodType.POST, url, params, header, taskId), charsetName);
    }

    /**
     * HTTP get 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @return 页面内容
     */
    public static byte[] doGet(String url, String referer, Long taskId) {
        Map<String, String> header = new HashMap<>();
        header.put(HttpHeadKey.REFERER, referer);
        return execute(MethodType.GET, url, null, header, taskId);
    }

    /**
     * HTTP get 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static byte[] doGet(String url, Map<String, String> params, Map<String, String> header, Long taskId) {
        return execute(MethodType.GET, url, params, header, taskId);
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static byte[] doPost(String url, Map<String, String> params, Map<String, String> header, Long taskId) {
        return execute(MethodType.POST, url, params, header, taskId);
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static byte[] execute(MethodType type, String url, Map<String, String> params, Map<String, String> header,
                                 Long taskId) {
        CloseableHttpResponse response = null;
        BasicCookieStore cookieStore = getCookie(taskId);
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(CONFIG)
            .setDefaultCookieStore(cookieStore).build();
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                String param = EntityUtils.toString(new UrlEncodedFormEntity(pairs, DEFAULT_CHARSET));
                logger.debug("httpClient doGet url = {},param={}", url, param);
                url += "?" + param;
            }

            HttpRequestBase client = null;
            if (type == MethodType.GET) {
                client = new HttpGet(url);
            } else {
                client = new HttpPost(url);
            }
            if (null == header) {
                header = new HashMap<>();
            }
            if (!header.containsKey(HttpHeadKey.CONNECTION)) {
                header.put(HttpHeadKey.CONNECTION, "close");
            }

            for (Map.Entry<String, String> entry : header.entrySet()) {
                client.setHeader(entry.getKey(), entry.getValue());
            }
            response = httpclient.execute(client);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                client.abort();
                throw new RuntimeException("HttpClient doPost error, statusCode: " + statusCode);
            }
            saveCookie(taskId, cookieStore);
            return EntityUtils.toByteArray(response.getEntity());
        } catch (Exception e) {
            logger.error("http error url={}", url, e);
            throw new RuntimeException("http error url=" + url, e);
        } finally {
            IOUtils.closeQuietly(httpclient);
            IOUtils.closeQuietly(response);
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
        if(StringUtils.isBlank(sb)){
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

    public static HttpResult<Map<String, Object>> refeshPicCodePicCode(Long taskId, String websiteName, String url,
                                                                       String returnName, String formType) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            byte[] data = PluginHttpUtils.doGet(url, taskId);
            String picCode = Base64.encodeBase64String(data);
            Map<String, Object> map = new HashMap<>();
            map.put(returnName, picCode);
            logger.info("刷新图片验证码成功,taskId={},websiteName={},formType={},url={}", taskId, websiteName, formType, url);
            return result.success(map);
        } catch (Exception e) {
            logger.error("刷新图片验证码失败 error taskId={},websiteName={},formType={},url={}", taskId, websiteName, formType,
                url, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

}
