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

import java.util.concurrent.atomic.AtomicBoolean;

import com.treefinance.crawler.framework.context.SearchProcessorContext;
import com.treefinance.crawler.support.selenium.WebDriverFactory;
import org.joda.time.LocalDate;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支付宝交易记录搜索
 * @author Jerry
 * @since 22:50 26/11/2017
 */
public class MockTransactionSearcher {

    private static final Logger                 LOGGER                  = LoggerFactory.getLogger(MockTransactionSearcher.class);
    private static final String                 SEARCH_DATETIME_PATTERN = "yyyy.MM.dd";
    private final        SearchPageAction       pageAction;
    private final        SearchProcessorContext context;
    private              String                 startDate;
    private              String                 endDate;
    private              String                 type;
    private              String                 url;
    private volatile     AtomicBoolean          qrCodeScan              = new AtomicBoolean(true);
    private volatile     AtomicBoolean          initialTime             = new AtomicBoolean(true);

    public MockTransactionSearcher(SearchProcessorContext context, String proxy) {
        this.context = context;
        WebDriver webDriver = WebDriverFactory.makeFirefoxDriver(proxy);
        this.pageAction = new SearchPageAction(webDriver, context.getWebsiteName().contains("taobao"));
    }

    public synchronized void initial(String cookie, int months) throws InterruptedException {
        LOGGER.info(">>>>>> 初始化交易记录搜索器");
        pageAction.initial(cookie);

        checkIncorrectPage(false);

        if (pageAction.isNormal()) {
            pageAction.switchAdvancedIfRedirect();

            LocalDate now = LocalDate.now();
            this.startDate = now.minusMonths(Math.max(0, months)).toString(SEARCH_DATETIME_PATTERN);
            this.endDate = now.toString(SEARCH_DATETIME_PATTERN);
        }
    }

    public synchronized TransactionPage search(String type) throws InterruptedException, StopException {
        LOGGER.info(">>>>>> 开始搜索交易记录，类型：{}", type);
        this.type = type;

        // 等待首页中的搜索表单加载完成
        if (pageAction.isNormal()) {
            int i = 0;
            while (true) {
                try {
                    // 首次选择交易开始时间
                    if (initialTime.compareAndSet(true, false)) {
                        pageAction.selectBeginDate(startDate);
                    }

                    // 选择交易类型
                    pageAction.selectTradeType(type);
                    break;
                } catch (Exception e) {
                    if (++i >= 3) {
                        throw new StopException("多次选择查询条件失败！", e);
                    }

                    boolean refresh;
                    try {
                        refresh = pageAction.refresh(2);
                    } catch (Exception e1) {
                        throw new StopException("刷新页面失败！直接退出", e);
                    }

                    if (!refresh) {
                        throw new StopException("刷新页面失败！直接退出");
                    }
                }
            }
            try {
                // 提交表单
                pageAction.search();
            } catch (Exception e) {
                LOGGER.warn("Error selecting conditions and searching", e);
            }
        }

        String pageContent = checkIncorrectPage(true);

        this.url = pageAction.getCurrentUrl();

        return buildPage(pageContent, pageAction.isNormal(), false);
    }

    public synchronized TransactionPage nextPage() throws InterruptedException {
        try {
            if (!pageAction.nextPage()) {
                LOGGER.info(">>>>>> 没有下一页，直接返回");
                return buildPage(null, false, true);
            }

            String pageContent = checkIncorrectPage(true);

            return buildPage(pageContent, pageAction.isNormal(), false);
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            pageAction.available();
            LOGGER.warn("Error to check and step into next page.", e);
        }

        return buildPage(null, false, false);
    }

    private TransactionPage buildPage(String pageContent, boolean success, boolean end) {
        return new TransactionPage(getSeedUrl(), this.url, this.type, this.startDate, this.endDate, pageAction.getPageNum(), pageContent, success, end);
    }

    /**
     * 处理限制页面
     */
    private String checkIncorrectPage(boolean needReturn) throws InterruptedException {
        LOGGER.info(">>>>>> 检测页面内容");
        if (pageAction.isTerminated()) {
            // 处理限制滑块页
            String pageContent = pageAction.checkLimitPage();

            LOGGER.info(">>>>>> 检测是否是二维码验证页");

            if (QRCodeScanner.isQRCodePage(pageContent) && qrCodeScan.compareAndSet(true, false)) {
                // 处理二维码页
                LOGGER.info(">>>>>> 检测到二维码验证页，准备扫描");
                QRCodeScanner scanner = new QRCodeScanner(context, pageAction);
                String actualPage = scanner.scan(pageContent);
                if (actualPage != null) {
                    LOGGER.info(">>>>>> 完成二维码扫描，返回正确页面");
                    pageContent = actualPage;
                } else {
                    LOGGER.warn(">>>>>> 未正常完成二维码扫描");
                }
            }

            return pageContent;
        }

        LOGGER.info(">>>>>> 检测到正常页面");

        return needReturn ? pageAction.getPageSource() : null;
    }

    public static String getSeedUrl() {
        return SearchPageAction.getSeedUrl();
    }

    public boolean isTerminated() {
        return pageAction.isTerminated();
    }

    public String getPageContent() {
        return pageAction.getPageSource();
    }

    public boolean hasNext() {
        return pageAction.hasNext();
    }

    public boolean reset() {
        initialTime.set(false);
        return pageAction.reset(2);
    }

    public void quit() {
        pageAction.quit();
    }

}
