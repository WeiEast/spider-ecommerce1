/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.treefinance.crawler.plugin.alipay.AppSpider;
import com.treefinance.toolkit.util.concurrent.NamedThreadFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * APP端余额收支明细的爬取（包含银行卡明细和花呗明细），来自手机版支付宝的账单明细
 * @author Jerry
 * @since 17:40 28/12/2017
 */
public class BalanceDetailSpider extends AppSpider {

    private static final String  APP_PREPARE_URL           = "https://login.m.taobao.com/login_to.do?from=taobao&to=375a695293093fd8d792dd16d7ad0a35&redirectUrl=https://ds.alipay.com/tbme/taobao.htm";
    private static final String  HUA_BEI_URL               = "https://consumeweb.alipay.com/record/huabei/m/index.htm?__webview_options__=showOptionMenu%3DNO";
    private static final String  HUA_BEI_PAGE_URL_TEMPLATE = "https://consumeweb.alipay.com/record/huabei/m/next_page.json?lastMonth=&pageNum=%s&dateBegin=%s&ctoken=%s";
    private static final String  BANK_LIST_URL             = "https://consumeweb.alipay.com/record/bank/m/list.htm?__webview_options__=showTitleLoading%3DTRUE%26backBehavior%3Dback";
    private static final String  BANK_URL_TEMPLATE         = "https://consumeweb.alipay.com/record/bank/m/index.htm?tradeType=&pageNum=%s&cardNo=%s&cardType=%s&beginDate=%s&endDate=%s";
    private static final int     MONTHS                    = 6;
    private static final Pattern PATTERN                   = Pattern.compile("</?body[^>]*>");
    private static final int     THREADS                   = 10;
    private ExecutorService executor;
    private CountDownLatch  latch;

    public BalanceDetailSpider() {
        super("taobao.com", "alipay.com");
    }

    @Override
    protected void process() throws Exception {
        String responseBody = sendRequest(APP_PREPARE_URL);
        if (logger.isDebugEnabled()) {
            logger.debug("APP prepare result >>> {}", responseBody);
        }

        if (!isSuccess(responseBody)) {
            logger.warn("Error doing authorise action in taobao app!");
            return;
        }

        initial();

        executor.execute(() -> {
            try {
                crawlHuaBeiDetails();
            } catch (Exception e) {
                logger.warn("Error crawling HuaBei details in alipay app.", e);
            } finally {
                latch.countDown();
            }
        });

        executor.execute(() -> {
            try {
                crawlBankDetails();
            } catch (Exception e) {
                logger.warn("Error crawling bank details in alipay app.", e);
            } finally {
                latch.countDown();
            }
        });

        complete();
    }

    /**
     * 爬取银行明细
     */
    private void crawlBankDetails() throws IOException, InterruptedException {
        List<Bank> list = requestBankCardList();
        if (!list.isEmpty()) {
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
            String endDate = formatter.print(now);
            String beginDate = formatter.print(now.minusMonths(MONTHS));

            CountDownLatch completed = new CountDownLatch(list.size());
            for (Bank bank : list) {
                executor.execute(() -> {
                    try {
                        queryBankDetails(bank, beginDate, endDate);
                    } catch (Exception e) {
                        logger.warn("Error crawling bank details in alipay app. >>> " + bank, e);
                    } finally {
                        completed.countDown();
                    }
                });
            }
            completed.await();
        }
    }

    /**
     * 爬取单个银行卡的明细
     */
    private void queryBankDetails(Bank bank, String beginDate, String endDate) throws IOException {
        int page = 1;
        while (true) {
            String url = getBankPageUrl(bank.cardNo, bank.cardType, beginDate, endDate, page);
            String content = sendRequest(url, BANK_LIST_URL);

            extractPageContent(url, content);

            // 非银行卡明细列表页，exit
            if (StringUtils.isBlank(content) || !content.contains("银行卡明细")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Incorrect bank detail page, stop!");
                }
                break;
            }

            // 没有更多内容，exit
            if (isNoMoreRecords(content) || !content.contains("go-detail-btn: 跳转详情")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No more bank records, stop!");
                }
                break;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());
            }

            page++;
        }
    }

    private String getBankPageUrl(String cardNo, String cardType, String beginDate, String endDate, int page) {
        return String.format(BANK_URL_TEMPLATE, page, cardNo, cardType, beginDate, endDate);
    }

    /**
     * 请求银行卡列表
     */
    private List<Bank> requestBankCardList() throws IOException {
        String content = sendRequest(BANK_LIST_URL);
        if (logger.isDebugEnabled()) {
            logger.debug("APP Bank list result >>> \n{}", content);
        }
        logger.info("APP Bank list result >>> \n{}", content);

        if (StringUtils.isNotEmpty(content) && content.contains("银行卡列表")) {
            return parseBankList(content);
        }

        return Collections.emptyList();
    }

    private static List<Bank> parseBankList(String content) {
        Document document = Jsoup.parse(content);
        Elements elements = document.select("ul.cm-card-list-wrap > li.cm-card-item");
        if (!elements.isEmpty()) {
            return elements.stream().map(element -> {
                String cardNo = element.attr("data-cardNo");
                String cardType = element.attr("data-cardType");
                return new Bank(cardNo, cardType);
            }).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * 爬取花呗明细
     */
    private void crawlHuaBeiDetails() throws IOException {
        String ctoken = getCToken(getSender().getCookieStore());
        String beginDate = LocalDate.now().minusMonths(MONTHS).toString("yyyy-MM-dd");
        int page = 1;
        while (true) {
            String url = getHuaBeiPageUrl(ctoken, beginDate, page);
            String content = sendRequest(url, HUA_BEI_URL);

            extractPageContent(url, content);

            // 非花呗明细列表页，exit
            if (StringUtils.isBlank(content) || PATTERN.matcher(content).find()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Incorrect huabei detail page, stop!");
                }
                break;
            }

            // 没有更多内容，exit
            if (isNoMoreRecords(content) || !content.contains("huabei-list: 记录详情")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No more huabei records, stop!");
                }
                break;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());
            }

            page++;
        }
    }

    private boolean isNoMoreRecords(String content) {
        return content.contains("class=\"no-more-records\"");
    }

    private String getHuaBeiPageUrl(String ctoken, String beginDate, int page) {
        return String.format(HUA_BEI_PAGE_URL_TEMPLATE, page, beginDate, ctoken);
    }

    private boolean isSuccess(String content) {
        return StringUtils.isNotEmpty(content) && content.contains("支付宝");
    }

    private void complete() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        if (!executor.isShutdown()) {
            executor.shutdownNow();
            if (logger.isDebugEnabled()) {
                logger.debug("Destroyed app balance executor");
            }
        }
    }

    private void initial() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initial app balance executor");
        }
        this.executor = Executors.newFixedThreadPool(THREADS, new NamedThreadFactory("app-balance"));
        this.latch = new CountDownLatch(2);
    }

    private static class Bank {

        private final String cardNo;
        private final String cardType;

        Bank(String cardNo, String cardType) {
            this.cardNo = cardNo;
            this.cardType = cardType;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("cardNo", cardNo).append("cardType", cardType).toString();
        }
    }
}
