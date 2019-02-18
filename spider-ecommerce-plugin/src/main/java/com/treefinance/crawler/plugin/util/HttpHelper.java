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

package com.treefinance.crawler.plugin.util;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.treefinance.crawler.framework.util.CookieFormater;
import com.treefinance.toolkit.util.kryo.KryoUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * @author Jerry
 * @since 14:43 25/12/2017
 */
public final class HttpHelper {

    private static final String COMMON_PATH = "/";

    private HttpHelper() {
    }

    public static BasicCookieStore createCookieStore(String cookie, String... domains) {
        Map<String, String> cookieMap = CookieFormater.INSTANCE.parserCookieToMap(cookie);

        return createCookieStore(cookieMap, domains);
    }

    public static BasicCookieStore createCookieStore(Map<String, String> cookies, String... domains) {
        BasicCookieStore cookieStore = new BasicCookieStore();

        addCookies(cookieStore, cookies, domains);

        return cookieStore;
    }

    public static void addCookies(CookieStore cookieStore, Map<String, String> cookies, String[] domains) {
        if (cookieStore == null || MapUtils.isEmpty(cookies)) {
            return;
        }

        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            for (String domain : domains) {
                BasicClientCookie cookie = new BasicClientCookie(entry.getKey(), entry.getValue());
                cookie.setVersion(0);
                cookie.setDomain(domain);
                cookie.setPath(COMMON_PATH);
                cookie.setAttribute(ClientCookie.PATH_ATTR, COMMON_PATH);
                cookie.setAttribute(ClientCookie.DOMAIN_ATTR, domain);
                cookieStore.addCookie(cookie);
            }
        }
    }


    public static HttpClientContext createContext(String cookie, String... domains) {
        BasicCookieStore cookieStore = createCookieStore(cookie, domains);

        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        return context;
    }

    public static HttpClientContext createContext(Map<String, String> cookies, String... domains) {
        BasicCookieStore cookieStore = createCookieStore(cookies, domains);

        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        return context;
    }

    public static HttpClientContext createContext(HttpClientContext context) {
        HttpClientContext newContext = HttpClientContext.create();
        newContext.setCookieStore(context.getCookieStore());
        newContext.setRequestConfig(context.getRequestConfig());

        return newContext;
    }

    public static HttpClientContext copyContext(HttpClientContext context) {
        return (HttpClientContext) KryoUtils.copy(context);
    }

    public static String getCookieValue(CookieStore cookieStore, String name, boolean strict) {
        Objects.requireNonNull(cookieStore);
        Objects.requireNonNull(name);

        List<Cookie> cookies = cookieStore.getCookies();
        String ctoken = null;
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                ctoken = cookie.getValue();
                break;
            }
        }

        if (ctoken == null && strict) {
            throw new IllegalArgumentException("Can not find necessary parameter '" + name + "'!");
        }

        return ctoken;
    }

    public static void setDefaultRedirectStrategy(HttpClientBuilder httpBuilder) {
        httpBuilder.setRedirectStrategy(new DefaultRedirectStrategy() {
            @Override
            protected boolean isRedirectable(String method) {
                return super.isRedirectable(method) || HttpPost.METHOD_NAME.equalsIgnoreCase(method);
            }

            @Override
            protected URI createLocationURI(String location) throws ProtocolException {
                String url = location;
                if (url.contains("^")) {
                    url = url.replace("^", "%5E");
                }

                return super.createLocationURI(url);
            }
        });
    }

    public static CloseableHttpClient customClient(){
        HttpClientBuilder builder = HttpClients.custom();
        builder.setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build());
        builder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(5000).build());
        HttpHelper.setDefaultRedirectStrategy(builder);
        return builder.build();
    }
}
