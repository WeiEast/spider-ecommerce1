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

package com.treefinance.crawler.plugin.alipay.huabei;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.GsonUtils;
import com.treefinance.crawler.framework.context.ProcessorContextUtil;
import com.treefinance.crawler.framework.extension.plugin.AbstractClientPlugin;
import com.treefinance.crawler.framework.extension.plugin.PluginConstants;
import com.treefinance.crawler.framework.extension.plugin.PluginFactory;
import com.treefinance.crawler.plugin.util.HttpHelper;
import com.treefinance.crawler.plugin.util.TopApi;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: yand
 * Date: 2018/2/8
 */
public class HuabeiOpenApiSpider extends AbstractClientPlugin {

    private static final Logger           logger            = LoggerFactory.getLogger(HuabeiOpenApiSpider.class);
    private static final String           REQUEST_URL       = "https://pcreditweb.alipay.com/h5/ajax/openapi/taobao/queryHuabei.json?";
    private static final String           TRUST_API         = "com.taobao.mtop.login.getclienttrustloginurl";
    private static final String           TRUST_API_VERSION = "1.0";
    private static final String           TRUST_URL         = "https://acs.m.taobao.com/h5/" + TRUST_API + "/" + TRUST_API_VERSION + "/";
    private static final String           REFERER           = "https://i.taobao.com/my_taobao.htm";
    private              BasicCookieStore cookieStore;

    @Override
    public String process(String... strings) throws Exception {
        String responseBody = null;
        Map<String, String> map = getClientTrustLoginUrl();
        CloseableHttpClient httpClient = HttpHelper.customClient();
        try {
            String url = REQUEST_URL + "_args="+ URLEncoder.encode("[{\"bizScenario\":\"mytaobao\"}]","UTF-8")+"&domain=" + map.get("domain") + "&goto=" + map.get("goto") + "&token=" + map.get("token") + "&actionType=" + map.get("actionType")+"&_ksTS=" + System.currentTimeMillis() + "_605" ;
            logger.info("huabei quest url is {}", url);
            HttpUriRequest request = RequestBuilder.get(url).addHeader(HttpHeaders.REFERER, REFERER).build();
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(cookieStore);
            HttpResponse httpResponse = httpClient.execute(request, context);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                responseBody = EntityUtils.toString(httpResponse.getEntity());
                logger.info("huabei responseBody is {}", responseBody);
            }
        } catch (Exception e) {
            logger.error(" process request fail", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.warn("Error closing the http client.", e);
            }
        }

        if(StringUtils.isNotEmpty(responseBody)){
            Map<String, Object> result = new HashMap<>();
            result.put(PluginConstants.FIELD, responseBody);
            return GsonUtils.toJson(result);
        }
        return null;
    }



    /**
     * 获取接口授权
     * 拿到trustLoginUrl,解析出token和actionType访问花呗
     * 这里要降低tls版本,否则ssl报错
     * @return
     * @exception Exception
     */
    private Map<String, String> getClientTrustLoginUrl() throws Exception {
        String trustLoginUrl = null;
        String appKey = "12574478";
        String data = "{\"redirectUrl\":\"https://huabei.alipay.com/wap/moonlight/sign.htm\",\"to\":\"alipay\"}";
        CloseableHttpClient httpClient = HttpHelper.customClient();
        Map<String, String> cookieMap = ProcessorContextUtil.getCookieMap(PluginFactory.getProcessorContext());
        logger.info("cookieMap{}", cookieMap);
        cookieStore = HttpHelper.createCookieStore(cookieMap, "taobao.com");
        try {
            int times = 0;
            String h5Token = getH5Token();
            while (times < 3) {
                // mtop签名,token 就是cookie里的_m_h5_tk以'_'分割第0个
                // 如果没有_m_h5_tk,top接口调用一次就有了
                long timestamp = System.currentTimeMillis();
                String sign = TopApi.sign(h5Token, appKey, timestamp, data);
                List<BasicNameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("appKey", appKey));
                list.add(new BasicNameValuePair("t", String.valueOf(timestamp)));
                list.add(new BasicNameValuePair("sign", sign));
                list.add(new BasicNameValuePair("api", TRUST_API));
                list.add(new BasicNameValuePair("v", TRUST_API_VERSION));
                list.add(new BasicNameValuePair("dataType", "jsonp"));
                list.add(new BasicNameValuePair("ecode", "1"));
                list.add(new BasicNameValuePair("isSec", "1"));
                list.add(new BasicNameValuePair("data", data));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                HttpUriRequest request = RequestBuilder.post(TRUST_URL).addHeader(HttpHeaders.REFERER, REFERER).setEntity(entity).build();
                HttpClientContext context = HttpClientContext.create();
                context.setCookieStore(cookieStore);
                HttpResponse httpResponse = httpClient.execute(request, context);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String responseBody = EntityUtils.toString(httpResponse.getEntity());
                    logger.info("responseBody is {}", responseBody);
                    if (StringUtils.contains(responseBody, "SUCCESS")) {
                        trustLoginUrl = JSON.parseObject(responseBody).getJSONObject("data").getString("trustLoginUrl");
                        break;
                    }

                    if (StringUtils.isEmpty(h5Token)) {
                        h5Token = getH5Token();
                    }
                }
                times++;
            }
        } catch (Exception e) {
            logger.error("getClientTrustLoginUrl request fail", e);
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.warn("Error closing the http client.", e);
            }
        }

        logger.info("trustLoginUrl is {}", trustLoginUrl);

        Map<String, String> map = getQueryParams(trustLoginUrl);
        logger.info("trustLoginUrl map is {}", map);
        return map;
    }

    /**
     * 获取h5 token
     * @return
     */
    private String getH5Token() {
        String h5Token = HttpHelper.getCookieValue(cookieStore, "_m_h5_tk", false);
        if (StringUtils.isNotEmpty(h5Token)) {
            h5Token = h5Token.split("_")[0];
        }
        logger.info("h5Token is {}", h5Token);
        return h5Token;
    }


    private Map<String, String> getQueryParams(String fullUrl) {
        if (StringUtils.isBlank(fullUrl)) {
            logger.warn("fullUrl is blank");
            return null;
        }
        Map<String, String> map = new HashMap<>();
        String queryString = StringUtils.substringAfter(fullUrl, "?");
        if (StringUtils.isNotBlank(queryString)) {
            String[] kvs = StringUtils.split(queryString, "&");
            if (null == kvs || kvs.length == 0) {
                logger.warn("query param is blank ,fullUrl={}", fullUrl);
                return map;
            }
            for (String kv : kvs) {
                if (StringUtils.isBlank(kv)) {
                    logger.warn("key value is blank");
                    continue;
                }
                String[] ss = StringUtils.split(kv, "=");
                if (ArrayUtils.isEmpty(ss)) {
                    logger.warn("invalid name-value kv={}", kv);
                } else if (ss.length == 1) {
                    map.put(ss[0], "");
                } else {
                    map.put(ss[0], ss[1]);
                }
            }
        }
        return map;
    }

}
