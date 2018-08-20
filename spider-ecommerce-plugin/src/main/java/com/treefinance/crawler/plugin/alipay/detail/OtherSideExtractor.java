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

package com.treefinance.crawler.plugin.alipay.detail;

import java.util.Map;

import com.treefinance.crawler.framework.context.ExtractorProcessorContext;
import com.treefinance.crawler.plugin.alipay.BaseFieldExtractPlugin;
import com.treefinance.crawler.plugin.util.HttpSender;
import com.treefinance.crawler.plugin.util.Unicode;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * pc端余额收支明细otherSide提取
 * User: yand
 * Date: 2018/1/9
 */
public abstract class OtherSideExtractor extends BaseFieldExtractPlugin<ExtractorProcessorContext> {

    private static final   String     DETAIL_URL = "https://shenghuo.alipay.com/send/queryTransferDetail.htm?tradeNo=%s";
    private static final   String     USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:53.0) Gecko/20100101 Firefox/53.0";
    protected static final String     BIZ_TYPE   = "D_TRANSFER";
    protected static final String     TRADE_TYPE = "300";
    private                HttpSender sender;

    @Override
    protected Object extract(String content, ExtractorProcessorContext processorContext) throws Exception {
        Map<String, String> cookies = processorContext.getCookiesAsMap();
        if (MapUtils.isEmpty(cookies)) {
            throw new IllegalArgumentException("Warn! Warn! Warn! Cookies must not be empty!");
        }

        HttpSender sender = new HttpSender(USER_AGENT);
        sender.setCookies(cookies, "taobao.com", "alipay.com");
        this.sender = sender;

        try {
            String tradeNo = getTradeNo(content, processorContext);

            if (StringUtils.isNotBlank(tradeNo)) {
                logger.info("Search otherside in detail order page. tradeNo: {}", tradeNo);
                return getOtherSideFromOrderDetailPage(tradeNo);
            } else {
                logger.warn("Trade number not found! Failure to search otherside in detail order page.");
            }

            return null;
        } finally {
            this.sender.close();
        }
    }

    protected abstract String getTradeNo(String content, ExtractorProcessorContext processorContext);

    private Object getOtherSideFromOrderDetailPage(String tradeNo) throws Exception {
        String pageContent = sender.send(getDetailUrl(tradeNo));

        if (logger.isDebugEnabled()) {
            logger.debug("Order detail page: {} >>> \n{}", tradeNo, pageContent);
        }

        String name = getValueByXpath(pageContent, "//div[@class='tb-border p-trade-slips']/table/tbody/tr//@data-emoji");
        if (StringUtils.isNotBlank(name)) {
            name = Unicode.decodeUnicode(name);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Actual other name : {} ", name);
        }

        String account = getValueByXpath(pageContent, "//div[@class='tb-border p-trade-slips']/table/tbody/tr/td/text()");
        if (StringUtils.isBlank(account)) {
            account = getValueByXpath(pageContent, "//div[@class='tb-border p-trade-slips']/table/tbody/tr[2]/td/text()");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Actual other account : {} ", account);
        }

        String otherside = StringUtils.trimToEmpty(name) + StringUtils.trimToEmpty(account);

        logger.info("Actual otherside : {}", otherside);

        return otherside;
    }

    private String getDetailUrl(String tradeNo) {
        return String.format(DETAIL_URL, tradeNo);
    }

}
