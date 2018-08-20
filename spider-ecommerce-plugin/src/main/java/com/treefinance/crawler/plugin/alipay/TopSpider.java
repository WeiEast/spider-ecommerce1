/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
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

package com.treefinance.crawler.plugin.alipay;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.treefinance.crawler.exception.UnexpectedException;
import com.treefinance.crawler.framework.extension.spider.BaseSpider;
import com.treefinance.crawler.framework.extension.spider.page.SimplePage;
import com.treefinance.crawler.framework.proxy.Proxy;
import com.treefinance.crawler.framework.proxy.ProxyManager;
import com.treefinance.crawler.plugin.util.HttpHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 11:13 25/03/2018
 */
public abstract class TopSpider extends BaseSpider {

    private static final String              TOKEN_COOKIE_NAME = "_m_h5_tk";
    protected final      Logger              logger            = LoggerFactory.getLogger(getClass());
    protected            CloseableHttpClient httpClient;
    protected            CookieStore         cookieStore;

    private String h5Token;

    @Override
    public void run() throws InterruptedException {
        try {
            init();

            doProcess();
        } catch (Exception e) {
            logger.error("Error requesting taobao's top api!", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.warn("Error closing the http client.", e);
            }

            if (h5Token != null) {
                List<Cookie> cookies = cookieStore.getCookies();
                String cookieString = cookies.stream().map(cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.joining(";"));

                logger.info("Set new cookies >> {}", cookieString);
                getContext().setCookies(cookieString);
            }
        }
    }

    protected void extractPageContent(String url, String content) {
        if (logger.isDebugEnabled()) {
            logger.debug("Url >>> {}", url);
            logger.debug("PageContent >>> {}", content);
        }
        getPageProcessor().process(new SimplePage(url, content, "EcommerceData"));
    }

    protected void init() {
        Map<String, String> cookieMap = getContext().getCookiesAsMap();
        logger.info("cookieMap {}", cookieMap);

        this.cookieStore = HttpHelper.createCookieStore(cookieMap, "taobao.com");
        this.httpClient = HttpHelper.customClient();
    }

    protected abstract void doProcess() throws Exception;

    public Info sendTopRequest(String appKey, String api, String apiVersion, String data, String referer, boolean ignore) throws Exception {
        return sendTopRequest(api, apiVersion, appKey, data, referer, () -> Collections.singletonList(new BasicNameValuePair("jsv", "2.4.8")), ignore);
    }

    public Info sendTopRequest(String api, String apiVersion, String appKey, String data, String referer, Supplier<List<BasicNameValuePair>> supplier, boolean ignore) throws Exception {
        String h5Token = getH5Token(cookieStore);
        HttpResponse httpResponse = null;
        int times = 0;
        while (times < 3) {
            long timestamp = System.currentTimeMillis();
            String sign = mtopSign(h5Token, appKey, timestamp, data);
            //请求参数
            List<BasicNameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("api", api));
            list.add(new BasicNameValuePair("appKey", appKey));
            list.add(new BasicNameValuePair("t", String.valueOf(timestamp)));
            list.add(new BasicNameValuePair("sign", sign));
            list.add(new BasicNameValuePair("v", apiVersion));
            list.add(new BasicNameValuePair("ecode", "1"));
            list.add(new BasicNameValuePair("data", data));

            if (supplier != null) {
                List<BasicNameValuePair> basicNameValuePairs = supplier.get();
                list.addAll(basicNameValuePairs);
            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            String apiUrl = "https://h5api.m.taobao.com/h5/" + api + "/" + apiVersion + "/";

            RequestConfig.Builder builder = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000);
            if (needProxy()) {
                HttpHost proxy = getProxy(apiUrl);
                if (proxy != null) {
                    logger.info("use proxy: {}:{}", proxy.getHostName(), proxy.getPort());
                    builder.setProxy(proxy);
                }
            }

            RequestBuilder requestBuilder = RequestBuilder.post(apiUrl).addHeader(HttpHeaders.REFERER, referer).setConfig(builder.build()).setEntity(entity);

            HttpUriRequest request = requestBuilder.build();
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(cookieStore);
            logger.info("httpPost is {}", request);
            httpResponse = httpClient.execute(request, context);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String responseBody = EntityUtils.toString(httpResponse.getEntity());

                if (responseBody != null && responseBody.contains("RGV587_ERROR")) {
                    throw new UnexpectedException("Unexpected response >> api: " + api + "\nresponse-body: " + responseBody);
                }

                logger.info("top-api: {}, responseBody: {}", api, responseBody);
                if (isOk(responseBody, h5Token, ignore)) {
                    return new Info(apiUrl, responseBody);
                }

                if (StringUtils.isEmpty(h5Token)) {
                    h5Token = getH5Token(cookieStore);
                }
            }
            times++;
        }

        EntityUtils.consumeQuietly(httpResponse.getEntity());

        throw new UnexpectedException("Unexpected exception when requesting api '" + api + "' for 3 times.");
    }

    protected boolean needProxy() {
        return true;
    }

    private HttpHost getProxy(String url) {
        try {
            Proxy proxy;
            ProxyManager proxyManager = getProxyManager();
            if (proxyManager != null) {
                proxy = proxyManager.getProxy();
            } else {
                proxy = getContext().getProxy(url);
            }

            if (proxy != null) {
                return new HttpHost(proxy.getHost(), proxy.getPort());
            }

        } catch (Exception e) {
            logger.warn("Error getting proxy!", e);
        }

        return null;
    }

    private boolean isOk(String content, String h5Token, boolean ignore) {
        if (StringUtils.contains(content, "令牌为空")) {
            return false;
        }

        return ignore || !needRetry(content, h5Token);
    }

    protected boolean needRetry(String content, String h5Token) {
        return false;
    }

    private String mtopSign(String h5Token, String appKey, long timestamp, String data) throws Exception {
        StringBuilder c = new StringBuilder();
        if (StringUtils.isNoneBlank(h5Token)) {
            c.append(h5Token);
        }
        c.append("&").append(timestamp).append("&").append(appKey).append("&").append(data);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("js/mtop.sign.js")) {
            Invocable invocable = evalScript(inputStream);
            String sign = (String) invocable.invokeFunction("h", c.toString());
            logger.info("sign is {}", sign);
            return sign;
        }
    }

    private static Invocable evalScript(InputStream stream) throws ScriptException {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptEngine.eval(new InputStreamReader(stream));
        return (Invocable) scriptEngine;
    }

    private String getH5Token(CookieStore cookieStore) {
        if (this.h5Token == null) {
            String h5Token = HttpHelper.getCookieValue(cookieStore, TOKEN_COOKIE_NAME, false);
            if (StringUtils.isNotEmpty(h5Token)) {
                h5Token = h5Token.split("_")[0];
            }
            this.h5Token = h5Token;
        }
        logger.info("h5Token is {}", this.h5Token);
        return this.h5Token;
    }

    protected static class Info {

        private final String url;
        private final String data;

        public Info(String url, String data) {
            this.url = url;
            this.data = data;
        }

        public String getUrl() {
            return url;
        }

        public String getData() {
            return data;
        }
    }
}
