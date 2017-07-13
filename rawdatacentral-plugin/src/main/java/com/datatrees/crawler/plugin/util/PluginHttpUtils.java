package com.datatrees.crawler.plugin.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/7/13.
 */
public class PluginHttpUtils {

    private static Logger logger = LoggerFactory.getLogger(PluginHttpUtils.class);

    private static final RequestConfig CONFIG;


    public static final String               DEFAULT_CHARSET = "UTF-8";

    static {
        CONFIG = RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(3000).build();
    }


    /**
     * HTTP Post 获取内容
     *
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static byte[] doGet(String url, Map<String, String> params, Long taskId) throws IOException {
        CloseableHttpResponse response = null;
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
        HttpGet httpPost = new HttpGet(url);
        try {
            BasicCookieStore cookieStore = new BasicCookieStore();
            if (null != cookies) {
                cookieStore.addCookies(cookies);
            }
            CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(CONFIG).setDefaultCookieStore(cookieStore)
                .build();
            response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient doPost error, statusCode: " + statusCode);
            }
            return EntityUtils.toByteArray(response.getEntity());
        } finally {
            if (null != response.getEntity()) {
                IOUtils.closeQuietly(response.getEntity().getContent());
            }
            IOUtils.closeQuietly(response);
        }
    }

    public static BasicCookieStore getCookie(Long taskId){



    }
}
