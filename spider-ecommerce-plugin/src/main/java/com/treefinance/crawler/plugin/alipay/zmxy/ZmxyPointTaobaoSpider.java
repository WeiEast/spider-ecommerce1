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

import com.fasterxml.jackson.databind.JsonNode;
import com.treefinance.crawler.plugin.alipay.AppSpider;
import com.treefinance.toolkit.util.json.Jackson;
import org.apache.commons.lang3.StringUtils;

/**
 * APP端芝麻信用风爬取，来自手机淘宝app
 * @author Jerry
 * @since 19:36 22/12/2017
 */
public class ZmxyPointTaobaoSpider extends AppSpider {

    private static final String TAOBAO_URL             = "https://login.m.taobao.com/login_to.do?from=taobao&to=375a695293093fd8d792dd16d7ad0a35&redirectUrl=https://personalweb.alipay.com/tb/cardData.json";
    private static final String ZMXY_AUTH_URL_TEMPLATE = "https://consumeweb.alipay.com/record/auth.json?ctoken=";

    public ZmxyPointTaobaoSpider() {
        super("taobao.com");
    }

    @Override
    protected void process() throws Exception {
        String responseBody = sendRequest(TAOBAO_URL);

        logger.info("Zmxy search result >>> {}", responseBody);

        if (StringUtils.isEmpty(responseBody)) {
            logger.warn("Error requesting zmxy point from taobao!");
            return;
        }

        String point = PointExtractor.extract(responseBody);
        if ("***".equals(point)) {
            if (authorized()) {
                responseBody = sendRequest(TAOBAO_URL);
                logger.info("[Authorized] Zmxy search result >>> {}", responseBody);
            }
        }

        extractPageContent(TAOBAO_URL, responseBody);
    }

    private boolean authorized() {
        try {
            String token = getCToken(getSender().getCookieStore());

            String responseBody = sendRequest(ZMXY_AUTH_URL_TEMPLATE + token);

            logger.info("Zmxy authorized result: {}", responseBody);

            JsonNode node = null;
            try {
                node = Jackson.parse(responseBody);
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
            return node != null && "success".equals(node.get("stat").textValue());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return false;
    }
}
