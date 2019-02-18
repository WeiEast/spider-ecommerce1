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

package com.treefinance.crawler.plugin.alipay.ts;

import com.treefinance.crawler.exception.UnexpectedException;
import com.treefinance.crawler.framework.extension.spider.BaseSpider;
import com.treefinance.crawler.framework.extension.spider.page.AlipayRecordPage;
import com.treefinance.crawler.framework.proxy.Proxy;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * WEB端支付宝交易记录的爬取主类
 * @author Jerry
 * @since 01:06 27/11/2017
 */
public class TransactionSpider extends BaseSpider {

    private static final String[] TYPE_LIST = {"TRANSFER", "SHOPPING", "DEPOSIT", "MOBILE_RECHARGE", "CCR", "FINANCE", "PUC_CHARGE"/*, "OFFLINENETSHOPPING"*/, "WITHDRAW", "PERLOAN"};

    @Override
    public void run() {
        Objects.requireNonNull(getContext());
        Objects.requireNonNull(getPageProcessor());

        String cookies = getContext().getCookiesAsString();
        if (StringUtils.isEmpty(cookies)) {
            throw new UnexpectedException("Can not find the available cookie string when calling transaction spider!");
        }

        logger.info("Spider cookies: {}", cookies);

        String proxyString = null;
        try {
            Proxy proxy = getContext().getProxy(MockTransactionSearcher.getSeedUrl());
            if (proxy != null) {
                proxyString = proxy.format();
            }
        } catch (Exception e) {
            logger.error("Error acquiring proxy from proxy-manger!", e);
        }

        searchRecords(cookies, proxyString);
    }

    private void searchRecords(String cookie, String proxy) {
        MockTransactionSearcher searcher = null;
        try {
            searcher = new MockTransactionSearcher(getContext(), proxy);
            searcher.initial(cookie, 6);

            if (searcher.isTerminated()) {
                logger.warn(">>>>>> Unexpected page.\n {}", searcher.getPageContent());
                logger.warn(">>>>>> Exit the transaction searcher.");
                return;
            }

            for (String type : TYPE_LIST) {
                try {
                    TransactionPage page = searcher.search(type);

                    extractPageContent(page);

                    if (!searcher.isTerminated()) {
                        do {
                            Thread.sleep(500);

                            page = searcher.nextPage();

                            extractPageContent(page);
                        } while (searcher.hasNext() && page.getPageNum() < 10);
                    }

                    if (searcher.isTerminated()) {
                        break;
                    } else if (page.isSuccess()) {
                        page.setSuccess(false);
                        page.setEnd(true);
                        extractPageContent(page);
                    }
                } catch (StopException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error("Error searching alipay transaction records, type: {}", type, e);
                    if (!searcher.reset()) {
                        throw new StopException("重置页面失败！直接退出！", e);
                    }
                }
            }
        } catch (Exception e) {
            throw new UnexpectedException("Unexpected exception when running transaction searcher!", e);
        } finally {
            if (searcher != null) {
                searcher.quit();
            }
            logger.info("交易记录查询结束！Exit the transaction searcher.");
        }
    }

    private void extractPageContent(TransactionPage page) {
        logger.info("<<<<<<<<< 处理交易记录页 pageNum: {}, type: {}, success: {}, end: {}", page.getPageNum(), page.getType(), page.isSuccess(), page.isEnd());
        Map<String, Object> extra = new HashMap<>(2);
        extra.put("pageNum", page.getPageNum());
        extra.put("type", page.getType());
        getPageProcessor().process(new AlipayRecordPage(page.getExpectedUrl(), page.getContent(), extra, "EcommerceData", page.isSuccess(), page.isEnd()));
    }

}
