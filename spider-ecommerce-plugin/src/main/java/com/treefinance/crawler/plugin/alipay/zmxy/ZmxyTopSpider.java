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

package com.treefinance.crawler.plugin.alipay.zmxy;

import java.util.ArrayList;
import java.util.List;

import com.treefinance.crawler.exception.UnexpectedException;
import com.treefinance.crawler.plugin.alipay.TopSpider;
import com.treefinance.crawler.plugin.util.TopApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author Jerry
 * @since 12:34 25/03/2018
 */
abstract class ZmxyTopSpider extends TopSpider {

    protected static final String APP_KEY          = "12574478";
    protected static final String REFERER          = "https://i.taobao.com/my_taobao.htm";
    private static final   String DATA             = "{}";
    private static final   String AUTH_API         = "com.taobao.idle.zhima.auth.redirect";
    private static final   String AUTH_API_VERSION = "1.0";
    private static final   String TRUST_URL        = "https://h5api.m.taobao.com/h5/" + AUTH_API + "/" + AUTH_API_VERSION + "/";

    @Override
    protected boolean needRetry(String content, String h5Token) {
        //用户未授权 FAIL_BIZ_BAD_REQUEST
        //用户取消授权 FAIL_BIZ_ZMCREDIT
        return (StringUtils.containsAny(content, "FAIL_BIZ_BAD_REQUEST", "FAIL_BIZ_NOT_AUTHORIZABLE", "FAIL_BIZ_ZMCREDIT") || !content.contains("ownerUserInfo")) && getClientTrust(h5Token);
    }

    /**
     * 默认对用户进行授权
     */
    private boolean getClientTrust(String h5Token) {
        try {
            long timestamp = System.currentTimeMillis();
            String sign = TopApi.sign(h5Token, APP_KEY, timestamp, DATA);
            List<BasicNameValuePair> list = new ArrayList<>();
            //请求参数
            list.add(new BasicNameValuePair("appKey", APP_KEY));
            list.add(new BasicNameValuePair("t", String.valueOf(timestamp)));
            list.add(new BasicNameValuePair("sign", sign));
            list.add(new BasicNameValuePair("api", AUTH_API));
            list.add(new BasicNameValuePair("v", AUTH_API_VERSION));
            list.add(new BasicNameValuePair("ecode", "1"));
            list.add(new BasicNameValuePair("jsv", "2.4.2"));
            list.add(new BasicNameValuePair("data", DATA));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            HttpUriRequest request = RequestBuilder.post(TRUST_URL).addHeader(HttpHeaders.REFERER, REFERER).setEntity(entity).build();
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(cookieStore);
            HttpResponse httpResponse = httpClient.execute(request, context);
            try {
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String responseBody = EntityUtils.toString(httpResponse.getEntity());

                    if (responseBody != null && responseBody.contains("RGV587_ERROR")) {
                        throw new UnexpectedException("Unexpected response >> api: " + AUTH_API + "\nresponse-body: " + responseBody);
                    }

                    logger.info("zmxy getClientTrust success {}", responseBody);
                    return true;
                }
            } finally {
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
        } catch (UnexpectedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedException("Error requesting zmxy's client-trust api.", e);
        }

        return false;
    }

}
