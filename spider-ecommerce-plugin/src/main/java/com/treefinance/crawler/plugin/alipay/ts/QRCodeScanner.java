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

package com.treefinance.crawler.plugin.alipay.ts;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.directive.DirectiveEnum;
import com.datatrees.spider.share.domain.directive.DirectiveRedisCode;
import com.datatrees.spider.share.domain.directive.DirectiveResult;
import com.datatrees.spider.share.domain.directive.DirectiveType;
import com.datatrees.spider.share.service.MessageService;
import com.treefinance.crawler.support.selenium.SeleniumOperation;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支付宝交易记录爬取过程中的二维码扫描
 * @author Jerry
 * @since 14:15 28/11/2017
 */
public class QRCodeScanner extends SeleniumOperation {

    private static final Logger                   logger                 = LoggerFactory.getLogger(QRCodeScanner.class);
    private static final Pattern                  QRCODE_INFO_PATTERN    = Pattern.compile("alipay\\.security\\.riskQRCode\\(\\s*(\\{[\\s\\S]*?})\\s*\\)");
    private static final String                   QRCODE_APP_SCAN_PREFIX = "alipayqr://platformapi/startapp?saId=10000007&qrcode=";
    private static final String                   QR_WAITING             = "WAITING_FOR_QR_VERIFY";
    private static final String                   QR_CONFIRMED           = "QR_CODE_CONFIRMED";
    private static final String                   QR_SUCCESS             = "VERIFY_QR_SUCCESS";
    private static final String                   QR_FAILED              = "VERIFY_QR_FAILED";
    // 支付宝页面二维码检测时间（轮询请求10次，间隔3秒），单位：秒
    private static final int                      CHECK_TIME             = 30;
    private static final String                   QRCODE_PAGE_URL        = "/record/checkSecurity.htm";
    private final        SearchPageAction         action;
    private final        AbstractProcessorContext context;
    private              MessageService           messageService;
    private              RedisService             redisService;
    private              String                   directiveId;

    public QRCodeScanner(AbstractProcessorContext context, SearchPageAction action) {
        super(action.getWebDriver());
        this.context = Objects.requireNonNull(context);
        this.action = action;
    }

    public String scan(String pageContent) throws InterruptedException {
        logger.info("find qrcode and start to scan >>>>>> ");

        QRCodeInfo qrCodeInfo = extractQRCodeInformation(pageContent);
        if (qrCodeInfo == null) {
            logger.info("Can not extract qrcode info from page content!");
            return null;
        }

        try {
            String qrcodeJson = buildQRCodeJson(qrCodeInfo);
            int timeout = getTimeout(context);
            for (int i = 0; i < 3; i++) {
                if (notifyApp(QR_WAITING, qrcodeJson)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Has post qrcode to app : {}", qrcodeJson);
                    }

                    return toScan(timeout);
                } else if (i == 2) {
                    throw new QRCodeScanningException("Error post qrcode to app : " + qrcodeJson + ".");
                }
            }

            throw new QRCodeScanningException("Error processing qrcode after retrying 3 times!");
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Something is wrong when triggering the alipay qrcode scanning.", e);
            notifyApp(QR_FAILED, e.getMessage());
            return null;
        }
    }

    private String buildQRCodeJson(QRCodeInfo qrCodeInfo) throws UnsupportedEncodingException {
        Map<String, String> codeResult = new HashMap<>();
        codeResult.put("httpQRCode", qrCodeInfo.getText());
        codeResult.put("rpcQRCode", QRCODE_APP_SCAN_PREFIX + URLEncoder.encode(qrCodeInfo.getText(), "UTF-8"));

        return GsonUtils.toJson(codeResult);
    }

    private static QRCodeInfo extractQRCodeInformation(String pageContent) {
        Matcher matcher = QRCODE_INFO_PATTERN.matcher(pageContent);
        if (matcher.find()) {
            String json = matcher.group(1);

            return GsonUtils.fromJson(json, QRCodeInfo.class);
        }

        return null;
    }

    private String toScan(long timeout) throws InterruptedException, QRCodeScanningException {
        boolean flag = false;

        long start = System.currentTimeMillis();
        long millisTimeout = timeout <= CHECK_TIME ? 0 : TimeUnit.SECONDS.toMillis(timeout - CHECK_TIME);
        long deadLine = start + millisTimeout;

        logger.info("Start to check qrcode status, now: {}, deadLine: {}, timeout: {}ms", new LocalDateTime(start).toString(), new LocalDateTime(deadLine).toString(), millisTimeout);

        do {
            // 等待页面10次检测结束后二维码是否扫描成功
            QRPageStatus status;
            try {
                status = waitUtil((ExpectedCondition<QRPageStatus>) input -> {
                    if (isNotQRCodePageURL(input)) {
                        return QRPageStatus.COMPLETE;
                    }

                    WebElement element = null;
                    try {
                        element = input.findElement(By.cssSelector("div#check-submit input[type='submit']"));
                    } catch (Exception e) {
                        logger.warn("Can not find submit button in qrcode page.", e);
                    }
                    if (element == null) {
                        return QRPageStatus.COMPLETE;
                    }

                    try {
                        if (element.isDisplayed()) {
                            return QRPageStatus.FORCE_SUBMIT;
                        }
                    } catch (StaleElementReferenceException e) {
                        return QRPageStatus.COMPLETE;
                    }

                    return null;
                }, CHECK_TIME);
            } catch (TimeoutException e) {
                status = QRPageStatus.WAIT;
            }

            logger.info("QRCode status: {}", status);

            if (status == QRPageStatus.WAIT) {
                // 扫描未成功，刷新二维码页

                logger.info("Refresh page and step into next waiting time, time: {}", LocalDateTime.now().toString());

                refreshPage();
            } else if (status == QRPageStatus.COMPLETE) {
                flag = true;
                break;
            } else {
                // force submit security form in qrcode page.
                submitSecurityForm();
                flag = true;
                break;
            }
        } while (System.currentTimeMillis() < deadLine);

        if (!flag) {
            context.addProcessorResult("QRCodeStatus", "WAIT");

            long end = System.currentTimeMillis();
            long spend = end - start;
            // 用户长时间未扫二维码，超时中断
            logger.info("Time out of scanning qrcode, time: {}, deadLine: {}", new LocalDateTime(end).toString(), new LocalDateTime(deadLine).toString());
            throw new QRCodeScanningException("QR code checking was time out! start: " + new LocalDateTime(start).toString() + ", end: " + new LocalDateTime(end).toString() + ", spend: " + spend + "ms");
        }

        notifyApp(QR_CONFIRMED, null);
        String pageContent = getAndRecheck();

        if (pageContent != null) {
            notifyApp(QR_SUCCESS, null);
            return pageContent;
        }

        throw new QRCodeScanningException("Unexpected exception when scanning qrcode.");
    }

    private void submitSecurityForm() {
        try {
            logger.info(">>> Submit security form");
            WebElement securityForm = findElementById("securityForm");
            securityForm.submit();

            logger.info(">>> Waiting for redirecting the normal page");
            try {
                awaitRedirect2NormalPage(10);
            } catch (Exception e) {
                try {
                    logger.info(">>> Refresh current page");
                    refreshPage();

                    logger.info(">>> Find security form and submit");
                    securityForm = awaitElementLocated(By.id("securityForm"), 5);
                    securityForm.submit();
                    awaitRedirect2NormalPage(10);
                } catch (Exception e1) {
                    logger.warn(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private Boolean awaitRedirect2NormalPage(long timeout) {
        return waitUtil((ExpectedCondition<Boolean>) this::isNotQRCodePageURL, timeout);
    }

    private boolean isNotQRCodePageURL(WebDriver webDriver) {
        String currentUrl = webDriver.getCurrentUrl();
        return currentUrl != null && !currentUrl.contains(QRCODE_PAGE_URL);
    }

    private String getAndRecheck() {
        if (action.pageLoaded()) {
            String pageSource = action.getPageSource();

            if (logger.isDebugEnabled()) {
                logger.debug("QRCode confirmed recheck page: \n{}", pageSource);
            }

            if (!isQRCodePage(pageSource)) {
                return pageSource;
            }
        }

        return null;
    }

    /**
     * 超时时间,单位：秒
     */
    private int getTimeout(AbstractProcessorContext context) {
        String websiteName = context.getWebsiteName();
        return PropertiesConfiguration.getInstance().getInt("qrcode.scanning.timeout_" + websiteName, 180);
    }

    private boolean notifyApp(String status, String remark) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            try {
                sendMessage(status, StringUtils.trimToEmpty(remark));
                return true;
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            Thread.sleep(3000);
        }
        return false;
    }

    private void sendMessage(String status, String remark) {
        Long taskId = context.getLong(AttributeKey.TASK_ID);

        logger.info("发送二维码状态: {}, taskId: {}", status, taskId);

        switch (status) {
            case QR_WAITING:
                directiveId = getMessageService().sendDirective(taskId, DirectiveEnum.REQUIRE_QR.getCode(), remark);
                sendDirective(taskId, DirectiveRedisCode.WAITTING);
                break;
            case QR_CONFIRMED:
                sendDirective(taskId, DirectiveRedisCode.SCANNED);
                break;
            case QR_SUCCESS:
                sendDirective(taskId, DirectiveRedisCode.SUCCESS);
                break;
            default:
                sendDirective(taskId, DirectiveRedisCode.FAILED);
                break;
        }
    }

    private void sendDirective(Long taskId, String failed) {
        getRedisService().saveDirectiveResult(directiveId, new DirectiveResult<>(DirectiveType.CRAWL_QR, taskId, failed, null));
    }

    private MessageService getMessageService() {
        if (messageService == null) {
            messageService = BeanFactoryUtils.getBean(MessageService.class);
        }

        return messageService;
    }

    private RedisService getRedisService() {
        if (redisService == null) {
            redisService = BeanFactoryUtils.getBean(RedisService.class);
        }

        return redisService;
    }

    public static boolean isQRCodePage(String content) {
        return StringUtils.isNotEmpty(content) && content.contains("securityForm") && content.contains("alipay.security.riskQRCode(");
    }

    private static class QRCodeInfo implements Serializable {

        private String url;
        private String sid;
        private String text;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    private enum QRPageStatus {
        WAIT,
        FORCE_SUBMIT,
        COMPLETE
    }
}
