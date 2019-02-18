/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.datatrees.spider.ecommerce.plugin.taobao.com.h5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin.QRStatusManager;
import com.datatrees.spider.ecommerce.plugin.util.QRUtils;
import com.datatrees.spider.ecommerce.plugin.util.SeleniumUtils;
import com.datatrees.spider.ecommerce.plugin.util.TaobaoHelper;
import com.datatrees.spider.share.common.http.ProxyUtils;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.JsoupXpathUtils;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.LoginMessage;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.domain.http.Cookie;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.share.service.CommonPluginService;
import com.datatrees.spider.share.service.MessageService;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.service.plugin.CommonPlugin;
import com.datatrees.spider.share.service.plugin.QRPlugin;
import com.datatrees.spider.share.service.plugin.qrcode.QRCodeVerification;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by guimeichao on 18/4/8.
 */
public class TaoBaoPluginSelenium implements QRPlugin, CommonPlugin {

    private static final Logger logger = LoggerFactory.getLogger(TaoBaoPluginSelenium.class);

    private static final String IS_RUNNING = "economic_qr_is_runing_";

    private static final String IS_INIT = "economic_qr_is_init_";

    private static final String QR_STATUS = "economic_qr_status_";

    private static final String ALIPAY_URL = "https://consumeprod.alipay.com/record/advanced.htm";

    private static final String AUTO_SIGN_ALIPAY_URL = ALIPAY_URL + "?sign_from=3000";

    private static final String PRE_LOGIN_URL = "https://login.taobao.com/member/login.jhtml?style=taobao&goto=" + encodeUrl(AUTO_SIGN_ALIPAY_URL);

    private static final String UMID_CACHE_PREFIX = "TAOBAO_QRCODE_AUTH_LOGIN_UMID_TOKEN_";

    private static final String UAB_COLLINA_CACHE_PREFIX = "TAOBAO_QRCODE_AUTH_LOGIN_UAB_COLLINA_";

    private static final String UMID_PARAM = "umid_token";

    private static final String UAB_COLLINA_COOKIE = "uab_collina";

    private static final String QRCODE_GEN_TIME_KEY = "com.treefinance.spider.ecommerce.h5_login.qrcode.gen_time:";

    /**
     * qrcode 过期时间，单位：秒
     */
    private static final int QRCODE_EXPIRATION = 240;

    private MonitorService monitorService;

    private MessageService messageService;

    private static void markQRStatus(CommonPluginParam param, QRCodeVerification.QRCodeStatus qrCodeStatus) {
        RedisUtils.set(QR_STATUS + param.getTaskId(), qrCodeStatus.name(), 60 * 2);
    }

    private static String encodeUrl(String queryString) {
        try {
            return URLEncoder.encode(queryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage());
        }

        return queryString;
    }

    private static TaoBaoPluginSelenium.UMID getUMID(boolean init, CommonPluginParam param) {
        String uabCollina = RedisUtils.get(UAB_COLLINA_CACHE_PREFIX + param.getTaskId());
        long timestamp = System.currentTimeMillis();
        if (init && StringUtils.isEmpty(uabCollina)) {
            uabCollina = timestamp + random(11);
            RedisUtils.set(UAB_COLLINA_CACHE_PREFIX + param.getTaskId(), uabCollina, 60 * 10);
        }

        String umidToken = RedisUtils.get(UMID_CACHE_PREFIX + param.getTaskId());
        if (init || StringUtils.isEmpty(umidToken)) {
            umidToken = "C" + uabCollina + timestamp + random(3);
            RedisUtils.set(UMID_CACHE_PREFIX + param.getTaskId(), umidToken, 60 * 10);
        }

        return new TaoBaoPluginSelenium.UMID(umidToken, uabCollina);
    }

    private static String random(int length) {
        StringBuilder text = new StringBuilder(length);
        for (; text.length() < length;) {
            text.append(String.valueOf(Math.random()).substring(2));
        }
        return text.substring(text.length() - length);
    }

    public MonitorService getMonitorService() {
        if (monitorService == null) {
            monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        }
        return monitorService;
    }

    public MessageService getMessageService() {
        if (messageService == null) {
            messageService = BeanFactoryUtils.getBean(MessageService.class);
        }
        return messageService;
    }

    private void notifyLogger(CommonPluginParam param, String taskMsg, String monitorMsg, ErrorCode monitorErrorCode, String monitorError) {
        getMessageService().sendTaskLog(param.getTaskId(), taskMsg);

        String msg = TemplateUtils.format("{}-->{}", FormType.getName(FormType.LOGIN), monitorMsg);
        if (monitorErrorCode == null) {
            getMonitorService().sendTaskLog(param.getTaskId(), param.getWebsiteName(), msg);
        } else if (monitorError == null) {
            getMonitorService().sendTaskLog(param.getTaskId(), param.getWebsiteName(), msg, monitorErrorCode);
        } else {
            getMonitorService().sendTaskLog(param.getTaskId(), param.getWebsiteName(), msg, monitorErrorCode, monitorError);
        }
    }

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        // 清理二维码状态
        QRStatusManager.clear(param.getTaskId());

        HttpResult<Object> result = new HttpResult<>();

        try {
            // 设置请求使用代理, 默认区域：浙江杭州
            ProxyUtils.setProxyLocation(param.getTaskId(), "浙江", "杭州");

            String isInit = RedisUtils.get(IS_INIT + param.getTaskId());
            if (!Boolean.TRUE.toString().equals(isInit)) {
                if (prepareQRCodePage(param)) {
                    RedisUtils.set(IS_INIT + param.getTaskId(), Boolean.TRUE.toString(), 60 * 5);
                } else {
                    logger.warn("刷新二维码失败，param={}", param);

                    notifyLogger(param, "刷新二维码失败", "刷新二维码-->失败", ErrorCode.REFESH_QR_CODE_ERROR, "二维码刷新失败,请重试");

                    return result.success("刷新二维码失败");
                }
            }

            Map<String, String> dataMap = getQRCode(param);

            startQueryTask(param);

            logger.info("刷新二维码成功，taskId={}", param.getTaskId());

            notifyLogger(param, "刷新二维码成功", "刷新二维码-->成功", null, null);

            return result.success(dataMap);
        } catch (Exception e) {
            logger.error("刷新二维码失败，param={}", param, e);

            notifyLogger(param, "刷新二维码失败", "刷新二维码-->失败", ErrorCode.REFESH_QR_CODE_ERROR, "二维码刷新失败,请重试");

            return result.success("刷新二维码失败");
        }
    }

    private void startQueryTask(CommonPluginParam param) {
        RedisUtils.set(QRCODE_GEN_TIME_KEY + param.getTaskId(), String.valueOf(System.currentTimeMillis()), QRCODE_EXPIRATION);

        String isRunning = RedisUtils.get(IS_RUNNING + param.getTaskId());
        if (!Boolean.TRUE.toString().equals(isRunning)) {
            new Thread(new QRCodeStatusQuery(param)).start();
        }
    }

    private Map<String, String> getQRCode(CommonPluginParam param) throws Exception {
        Exception ex = null;
        for (int i = 0; i < 3; i++) {
            Response response = null;
            try {
                UMID umid = getUMID(true, param);

                String templateUrl =
                        "https://qrlogin.taobao.com/qrcodelogin/generateQRCode4Login.do?adUrl=&adImage=&adText=&viewFd4PC=&viewFd4Mobile=&from=tb&appkey=00000000&_ksTS={}&callback=json&"
                                + UMID_PARAM + "=" + umid.token;
                response =
                    TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, TaobaoHelper
                        .timestampFlag()).setReferer(PRE_LOGIN_URL)
                    .addExtraCookie(UAB_COLLINA_COOKIE, umid.uab, "taobao.com").invoke();
                String jsonString = PatternUtils.group(response.getPageContent(), "json\\(([^\\)]+)\\)", 1);
                JSONObject json = JSON.parseObject(jsonString);

                String imgUrl = json.getString("url");
                if (!imgUrl.startsWith("https:")) {
                    imgUrl = "https:" + imgUrl;
                }

                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(imgUrl).setReferer(PRE_LOGIN_URL).invoke();

                QRUtils qrUtils = new QRUtils();
                String qrText = qrUtils.parseCode(response.getResponse());
                if (StringUtils.isEmpty(qrText)) {
                    throw new Exception("解析二维码内容失败。");
                }

                Map<String, String> dataMap = new HashMap<>(2);
                dataMap.put("qrBase64", response.getPageContentForBase64());
                dataMap.put("qrText", qrText);

                String lgToken = json.getString("lgToken");
                logger.debug("refresh lgToken: {}", lgToken);
                TaskUtils.addTaskShare(param.getTaskId(), "lgToken", lgToken);

                return dataMap;
            } catch (Exception e) {
                logger.warn("请求二维码失败, response= {}", response, e);
                ex = e;
            }
        }

        throw new Exception("请求二维码失败.", ex);
    }

    private boolean prepareQRCodePage(CommonPluginParam param) {
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "淘宝");
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(PRE_LOGIN_URL).invoke();
            logger.info("淘宝二维码登录-->初始化成功，taskId={}", param.getTaskId());

            notifyLogger(param, "初始化二维码成功", "初始化-->成功", null, null);

            return true;
        } catch (Exception e) {
            logger.error("淘宝二维码登录-->初始化失败，taskId={},response={}", param.getTaskId(), response, e);

            notifyLogger(param, "初始化二维码失败", "初始化-->失败", ErrorCode.TASK_INIT_ERROR, "初始化失败");
        }

        return false;
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        String status = RedisUtils.get(QR_STATUS + param.getTaskId());
        if (StringUtils.isEmpty(status)) {
            status = QRCodeVerification.QRCodeStatus.WAITING.name();
        }

        return new HttpResult<>().success(status);
    }

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        return null;
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        return null;
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        return null;
    }

    @Override
    public HttpResult<Object> validatePicCode(CommonPluginParam param) {
        return null;
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        return null;
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        return null;
    }

    private static class UMID {

        private final String token;

        private final String uab;

        public UMID(String token, String uab) {
            this.token = token;
            this.uab = uab;
        }
    }

    private class QRCodeStatusQuery implements Runnable {

        private final CommonPluginParam param;

        private final String cacheKey;

        private String lastFailure;

        QRCodeStatusQuery(CommonPluginParam param) {
            this.param = param;
            cacheKey = QRCODE_GEN_TIME_KEY + param.getTaskId();
        }

        @Override
        public void run() {
            RedisUtils.set(IS_RUNNING + param.getTaskId(), Boolean.TRUE.toString());
            try {
                while (true) {
                    try {
                        String time = RedisUtils.get(cacheKey);
                        if (StringUtils.isEmpty(time)) {
                            markQRStatus(param, QRCodeVerification.QRCodeStatus.EXPIRE);
                            notifyLogger(param, "登录超时", "校验-->超时", ErrorCode.LOGIN_TIMEOUT_ERROR, "登陆超时,请重试");
                            break;
                        }

                        String lgToken = TaskUtils.getTaskShare(param.getTaskId(), "lgToken");
                        if (lgToken != null && !lgToken.equals(lastFailure)) {
                            lastFailure = null;
                            QRCodeVerification.QRCodeStatus status = queryQRCodeStatus(param, lgToken);

                            logger.info("状态更新成功,当前二维码状态：{},taskId={}", status, param.getTaskId());
                            if (QRCodeVerification.QRCodeStatus.CONFIRMED.equals(status)) {
                                triggerAfterConfirmed(param);
                                break;
                            }

                            if (RedisUtils.incr(QR_STATUS + param.getTaskId() + "_" + lgToken + "_" + status.name()) == 1) {
                                if (QRCodeVerification.QRCodeStatus.SCANNED == status) {
                                    notifyLogger(param, "二维码已扫描", "扫描二维码-->已扫描", null, null);
                                } else if (QRCodeVerification.QRCodeStatus.EXPIRE == status) {
                                    notifyLogger(param, "二维码已过期", "扫描二维码-->已过期", null, null);
                                    RedisUtils.expire(cacheKey, 30);
                                    lastFailure = lgToken;
                                } else if (QRCodeVerification.QRCodeStatus.FAILED == status) {
                                    notifyLogger(param, "二维码扫描失败", "扫描二维码-->失败", null, null);
                                    RedisUtils.expire(cacheKey, 30);
                                    lastFailure = lgToken;
                                }
                            }
                            markQRStatus(param, status);
                        }

                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        logger.error("QRCode status querying process was interrupted!", e);
                        break;
                    } catch (Exception e) {
                        logger.warn("Something was wrong when querying QRCode status!", e);
                    }
                }
            } finally {
                RedisUtils.del(IS_RUNNING + param.getTaskId());
            }
        }

        private void triggerAfterConfirmed(CommonPluginParam param) {
            String loginUrl = TaskUtils.getTaskShare(param.getTaskId(), "loginUrl");

            String accountNo = getAccountNo(loginUrl);
            if (accountNo != null) {
                String redisKey = RedisKeyPrefixEnum.TASK_INFO_ACCOUNT_NO.getRedisKey(param.getTaskId());
                RedisUtils.setnx(redisKey, accountNo);
            }

            TaoBaoPluginSelenium.UMID umid = getUMID(false, param);

            loginUrl += "&" + UMID_PARAM + "=" + umid.token;

            Response response = null;
            RemoteWebDriver driver = null;
            try {
                driver = SeleniumUtils.createClient(param.getTaskId(), param.getWebsiteName());
                driver.get("https://login.taobao.com/favicon.ico");
                List<Cookie> cookies = TaskUtils.getCookies(param.getTaskId());
                for (Cookie cookie : cookies) {
                    org.openqa.selenium.Cookie cookie1 =
                            new org.openqa.selenium.Cookie(cookie.getName(), cookie.getValue(), cookie.getDomain(),
                                    cookie.getPath(), cookie.getExpiryDate(), cookie.isSecure());
                    driver.manage().addCookie(cookie1);
                }
                driver.get(loginUrl);
                new WebDriverWait(driver, 60).until(d -> d.findElement(By.className("fn-left")));
                TaskUtils.saveCookie(param.getTaskId(), SeleniumUtils.getCookies(driver));
                // response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(),
                // RequestType.GET).setFullUrl(loginUrl).setReferer(PRE_LOGIN_URL).invoke();
                //String redirectUrl = PatternUtils.group(response.getPageContent(), "window\\.location\\.href\\s*=\\s*\"([^\"]+)\";", 1);
                //String referer = "https://auth.alipay.com/login/trust_login.do?null&sign_from=3000&goto=" + ALIPAY_URL;
                //response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(redirectUrl).setReferer(referer).invoke();
                //processCertCheck(param.getTaskId(), param.getWebsiteName(), "", response.getPageContent());
            } catch (Exception e) {
                markQRStatus(param, QRCodeVerification.QRCodeStatus.FAILED);

                logger.error("淘宝二维码登录处理失败，taskId={},response={}", param.getTaskId(), response, e);

                notifyLogger(param, "登录失败", "校验-->失败", ErrorCode.LOGIN_FAIL, "登陆失败,请重试");
                return;
            } finally {
                driver.close();
            }

            markQRStatus(param, QRCodeVerification.QRCodeStatus.CONFIRMED);

            logger.info("用户已确认二维码，发送登录消息，taskId={}, website={}", param.getTaskId(), param.getWebsiteName());

            notifyLogger(param, "二维码确认成功", "校验-->成功", null, null);

            String cookieString = TaskUtils.getCookieString(param.getTaskId());
            LoginMessage loginMessage = new LoginMessage();
            loginMessage.setTaskId(param.getTaskId());
            loginMessage.setWebsiteName(param.getWebsiteName());
            loginMessage.setCookie(cookieString);
            loginMessage.setAccountNo(StringUtils.defaultString(accountNo));
            TaskUtils.addTaskShare(param.getTaskId(), "username", accountNo);

            BeanFactoryUtils.getBean(CommonPluginService.class).sendLoginSuccessMsg(TopicEnum.SPIDER_ECOMMERCE.getCode(), loginMessage);
        }

        /**
         * 处理跳转服务
         */
        private void processCertCheck(Long taskId, String websiteName, String remark, String pageContent) {
            String url = JsoupXpathUtils.selectFirst(pageContent, "//form/@action");
            if (StringUtils.isEmpty(url)) {
                throw new IllegalArgumentException("Error find form action when redirecting to alipay auth.");
            }

            String params = null;
            List<Element> list = JsoupXpathUtils.selectElements(pageContent, "//form//input[@name]|//form//textarea[@name]");
            if (null != list && !list.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<>(list.size());
                for (Element element : list) {
                    pairs.add(new BasicNameValuePair(element.attr("name"), element.val()));
                }
                params = pairs.stream().map(pair -> pair.getName() + "=" + pair.getValue()).collect(Collectors.joining("&"));
            }
            Response response = TaskHttpClient.create(taskId, websiteName, RequestType.POST).setUrl(url).setRequestBody(params).invoke();
            if (HttpStatus.SC_OK != response.getStatusCode()) {
                throw new IllegalStateException("Something is wrong when request '" + url + "'");
            }
        }

        private String getAccountNo(String loginUrl) {
            try {
                String accountNoTemp = PatternUtils.group(loginUrl, "cntaobao(.*)&token", 1);
                return URLDecoder.decode(accountNoTemp, "UTF-8");
            } catch (Exception e) {
                logger.warn("淘宝会员名抓取失败", e);
            }

            return null;
        }

        private QRCodeVerification.QRCodeStatus queryQRCodeStatus(CommonPluginParam param, String lgToken) {
            String code = getQRStatusCode(param, lgToken);
            switch (code) {
                case "10000":
                    return QRCodeVerification.QRCodeStatus.WAITING;
                case "10001":
                    return QRCodeVerification.QRCodeStatus.SCANNED;
                case "10004":
                    return QRCodeVerification.QRCodeStatus.EXPIRE;
                case "10006":
                    return QRCodeVerification.QRCodeStatus.CONFIRMED;
                default:
                    return QRCodeVerification.QRCodeStatus.FAILED;
            }
        }

        private String getQRStatusCode(CommonPluginParam param, String lgToken) {
            Response response = null;
            try {
                String templateUrl = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do?lgToken={}&defaulturl={}&_ksTS={}&callback=json";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, lgToken, encodeUrl(AUTO_SIGN_ALIPAY_URL), TaobaoHelper.timestampFlag()).setReferer(PRE_LOGIN_URL).invoke();
                String resultJson = PatternUtils.group(response.getPageContent(), "json\\(([^\\)]+)\\)", 1);
                JSONObject json = JSON.parseObject(resultJson);
                String code = json.getString("code");
                if (StringUtils.equals(code, "10006")) {
                    TaskUtils.addTaskShare(param.getTaskId(), "loginUrl", json.getString("url"));
                }
                return code;
            } catch (Exception e) {
                logger.error("获取二维码状态失败，param={},response={}", param, response, e);
            }
            return StringUtils.EMPTY;
        }
    }
}
