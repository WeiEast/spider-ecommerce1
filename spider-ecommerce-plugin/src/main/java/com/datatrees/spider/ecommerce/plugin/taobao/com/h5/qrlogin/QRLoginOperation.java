/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
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

package com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.DateUtils;
import com.datatrees.spider.share.common.utils.JsoupXpathUtils;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.QRStatus;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.directive.DirectiveResult;
import com.datatrees.spider.share.domain.exception.CommonException;
import com.datatrees.spider.share.domain.http.Cookie;
import com.datatrees.spider.share.domain.http.Response;
import com.treefinance.crawler.exception.UnexpectedException;
import com.treefinance.crawler.framework.util.xpath.XPathUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.LocalDateTime;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.datatrees.spider.ecommerce.plugin.taobao.com.h5.JavaScriptEngine.generateIsg;
import static com.datatrees.spider.share.domain.RequestType.GET;
import static com.datatrees.spider.share.domain.RequestType.POST;

/**
 * @author Jerry
 * @date 2019-02-15 13:36
 */
public class QRLoginOperation {
    private static final Logger logger = LoggerFactory.getLogger(QRLoginOperation.class);

    private static final String IS_INIT = "TAOBAO_QR_LOGIN_PAGE_INITIAL_";

    private static final String UMID_TOKEN_CACHE_PREFIX = "TAOBAO_QRCODE_LOGIN_COOKIE_UMID_TOKEN_";

    private static final String UAB_COLLINA_CACHE_PREFIX = "TAOBAO_QRCODE_LOGIN_COOKIE_UAB_COLLINA_";

    private static final String UAB_COLLINA_COOKIE = "_uab_collina";

    private static final String ISG_COOKIE = "isg";

    private static final String COOKIE_DOMAIN = ".taobao.com";
    private static final String COOKIE_PATH = "/";

    private static final String GOTO_TARGET_URL = "https://my.alipay.com/portal/i.htm?sign_from=3000";

    // private static final String ENCODED_GOTO_TARGET_URL = "https%3A%2F%2Flab.alipay.com%3A443%2Fuser%2Fnavigate
    // .htm%3Fsign_from%3D3000";

    private static final String ENCODED_GOTO_TARGET_URL = "https%3A%2F%2Fmy.alipay.com%2Fportal%2Fi.htm%3Fsign_from%3D3000";

    /**
     * 二维码登录页面
     */
    private static final String LOGIN_PAGE_URL = "https://login.taobao.com/member/login.jhtml?style=mini&newMini2=true&goto=" + ENCODED_GOTO_TARGET_URL;
    private static final String LOGIN_MID_URL = "https://login.taobao.com/member/login_mid.htm?type=success";
    /**
     * cookie.cna值来自该脚本的ETag值
     */
    private static final String CNA_URL = "https://log.mmstat.com/eg.js";

    private static final Pattern ETAG_PATTERN = Pattern.compile("goldlog.Etag=\"([.*]+?)\"");

    /**
     * 二维码生成请求
     */
    private static final String QRCODE_GENERATE_URL = "https://qrlogin.taobao.com/qrcodelogin/generateQRCode4Login.do?from=tb&appkey=00000000&umid_token={}&_ksTS={}&callback=json";

    /**
     * 二维码状态查询请求
     */
    private static final String QRCODE_STATUS_URL = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do?lgToken={}&defaulturl={}&_ksTS={}&callback=json";

    private static final String CERT_REDIRECT_URL_PATTERN = "window\\.location\\.href\\s*=\\s*\"([^\"]+)\";";
    /**
     * 短信验证码请求间隔时间,单位：秒
     */
    private static final int SMS_SEND_INTERVAL = 60;
    private static final long SMS_WAIT_TIMEOUT = 120;
    private static final String QRCODE_LOGIN_PAGE_INITIAL = Boolean.TRUE.toString();

    private final Random random = new Random();

    private RedisService redisService;

    private RedisService getRedisService() {
        if (redisService == null) {
            redisService = BeanFactoryUtils.getBean(RedisService.class);
        }
        return redisService;
    }

    private String timestampFlag() {
        return System.currentTimeMillis() + "_" + random.nextInt(1000);
    }

    private boolean isInitial(CommonPluginParam param) {
        String isInit = RedisUtils.get(IS_INIT + param.getTaskId());
        return QRCODE_LOGIN_PAGE_INITIAL.equals(isInit);
    }

    private void setInitial(CommonPluginParam param) {
        RedisUtils.set(IS_INIT + param.getTaskId(), QRCODE_LOGIN_PAGE_INITIAL, 60 * 5);
    }

    public boolean startLoginPage(CommonPluginParam param) {
        if (isInitial(param)) {
            return true;
        }

        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "淘宝");

            openLoginPage(param);

            initCNA(param);

            // TODO:埋点-初始化登录页 2019-02-15 李梁杰

            setInitial(param);

            logger.info("淘宝二维码登录-->初始化成功，taskId={}", param.getTaskId());
            QRLoginMonitor.notifyLogger(param, "初始化二维码成功", "初始化二维码-->成功", null, null);

            return true;
        } catch (Exception e) {
            logger.error("淘宝二维码登录-->初始化失败，taskId={}", param.getTaskId(), e);
            QRLoginMonitor.notifyLogger(param, "初始化二维码失败", "初始化二维码-->失败", ErrorCode.TASK_INIT_ERROR, "初始化二维码失败");
        }

        return false;
    }

    private void initCNA(CommonPluginParam param) {
        logger.info("登录页面打开成功，开始设置CNA值");
        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(CNA_URL).invoke();
        String pageContent = response.getPageContent();
        Matcher matcher = ETAG_PATTERN.matcher(pageContent);
        String cna = null;
        if (matcher.find()) {
            cna = matcher.group(1);
        }
        logger.error("淘宝二维码登录-->请求eg.js获取CNA值失败，taskId={}, response={}", param.getTaskId(), response);
        if (StringUtils.isEmpty(cna)) {
            throw new UnexpectedException("CNA值获取失败！请求eg.js结果 >> " + response);
        }

        Cookie cookie = new Cookie();
        cookie.setName("cna");
        cookie.setValue(cna);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath(COOKIE_PATH);
        cookie.setVersion(1);
        cookie.setSecure(false);
        cookie.getAttribs().put("domain", COOKIE_DOMAIN);
        cookie.getAttribs().put("path", COOKIE_PATH);
        cookie.setExpiryDate(LocalDateTime.now().plusYears(10).toDate());
        TaskUtils.saveCookie(param.getTaskId(), Collections.singletonList(cookie));
        logger.info("设置CNA值成功，cna: {}", cna);
    }

    private void openLoginPage(CommonPluginParam param) {
        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(LOGIN_PAGE_URL).invoke();

        checkResponseStatus(response, LOGIN_PAGE_URL);
    }

    private void checkResponseStatus(Response response, String url) {
        if (HttpStatus.SC_OK != response.getStatusCode()) {
            throw new IllegalStateException("Error response! - url: " + url + " response: " + response);
        }
    }

    public byte[] getQrCodeImage(CommonPluginParam param) {
        String imgUrl = generateQrCode(param);

        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(imgUrl).setReferer(LOGIN_PAGE_URL).invoke();

        checkResponseStatus(response, imgUrl);

        return response.getResponse();
    }

    private String generateQrCode(CommonPluginParam param) {
        UMID umid = getUMID(true, param);

        String url = TemplateUtils.format(QRCODE_GENERATE_URL, umid.token, timestampFlag());

        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(url).setReferer(LOGIN_PAGE_URL)
            .addExtralCookie(ISG_COOKIE, generateIsg(), COOKIE_DOMAIN).invoke();

        checkResponseStatus(response, url);

        try {
            String jsonString = PatternUtils.group(response.getPageContent(), "json\\(([^\\)]+)\\)", 1);
            JSONObject json = JSON.parseObject(jsonString);

            String lgToken = json.getString("lgToken");
            logger.debug("refresh lgToken: {}", lgToken);
            TaskUtils.addTaskShare(param.getTaskId(), "lgToken", lgToken);

            String imgUrl = json.getString("url");
            if (!imgUrl.startsWith("https:")) {
                imgUrl = "https:" + imgUrl;
            }
            return imgUrl;
        } catch (Exception e) {
            logger.warn("解析二维码图片失败！response： {}", response, e);
            throw e;
        }
    }

    public String queryQRCodeStatus(CommonPluginParam param, String lgToken) {
        Response response = null;
        try {
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(QRCODE_STATUS_URL, lgToken, ENCODED_GOTO_TARGET_URL, timestampFlag())
                .setReferer(LOGIN_PAGE_URL).addExtralCookie(ISG_COOKIE, generateIsg(), COOKIE_DOMAIN).invoke();
            String resultJson = PatternUtils.group(response.getPageContent(), "json\\(([^\\)]+)\\)", 1);
            JSONObject json = JSON.parseObject(resultJson);
            String code = json.getString("code");
            if (code != null) {
                switch (code) {
                    case "10000":
                        return QRStatus.WAITING;
                    case "10001":
                        return QRStatus.SCANNED;
                    case "10004":
                        return QRStatus.EXPIRE;
                    case "10006":
                        // save login url
                        TaskUtils.addTaskShare(param.getTaskId(), "loginUrl", json.getString("url"));
                        return QRStatus.CONFIRMED;
                    default:
                        break;
                }
            }
            logger.error("获取二维码状态失败，param={},response={}", param, response);
        } catch (Exception e) {
            logger.error("获取二维码状态失败，param={},response={}", param, response, e);
        }
        return QRStatus.FAILED;
    }

    public LoginResult startLogin(CommonPluginParam param) {
        String loginUrl = TaskUtils.getTaskShare(param.getTaskId(), "loginUrl");

        UMID umid = getUMID(false, param);

        loginUrl += "&umid_token=" + umid.token;

        String accountNo = getAccountNo(loginUrl);
        if (accountNo != null) {
            String redisKey = RedisKeyPrefixEnum.TASK_INFO_ACCOUNT_NO.getRedisKey(param.getTaskId());
            RedisUtils.setnx(redisKey, accountNo, 10 * 60);
        }

        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(loginUrl).setReferer(LOGIN_PAGE_URL)
            .addExtralCookie(ISG_COOKIE, generateIsg(), COOKIE_DOMAIN).addExtraCookie(UAB_COLLINA_COOKIE, umid.uab, COOKIE_DOMAIN).invoke();

        checkResponseStatus(response, loginUrl);

        return new LoginResult(loginUrl, accountNo, response);
    }

    public void doTrustLogin(CommonPluginParam param, LoginResult result) {
        String redirectUrl = findRedirectUrl(result.getResponse(), result.getLoginUrl());

        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(redirectUrl).setReferer(result.getRedirectUrl()).invoke();

        checkResponseStatus(response, redirectUrl);

        processCertCheck(param, response);
    }

    /**
     * 处理跳转服务
     */
    private void processCertCheck(CommonPluginParam param, Response trustLoginResponse) {
        String pageContent = trustLoginResponse.getPageContent();
        String url;
        try {
            url = JsoupXpathUtils.selectFirst(pageContent, "//form/@action");
        } catch (Exception e) {
            throw new UnexpectedException("Error parsing trust_login page to  process cert check. - response: " + trustLoginResponse, e);
        }
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("Error parsing trust_login page to  process cert check. - response: " + trustLoginResponse);
        }

        String params;
        try {
            params = null;
            List<Element> list = JsoupXpathUtils.selectElements(pageContent, "//form//input[@name]|//form//textarea[@name]");
            if (null != list && !list.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<>(list.size());
                for (Element element : list) {
                    pairs.add(new BasicNameValuePair(element.attr("name"), element.val()));
                }
                params = pairs.stream().map(pair -> pair.getName() + "=" + pair.getValue()).collect(Collectors.joining("&"));
            }
        } catch (Exception e) {
            throw new UnexpectedException("Error parsing trust_login page to  process cert check. - response: " + trustLoginResponse, e);
        }

        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), POST).setUrl(url).setRequestBody(params).invoke();

        checkResponseStatus(response, url);

        String redirectUrl = findRedirectUrl(response, url);

        response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(redirectUrl).setReferer(url).invoke();

        checkResponseStatus(response, redirectUrl);

        String page = response.getPageContent();
        if (!page.contains("我的支付宝 － 支付宝")) {
            throw new IllegalStateException("Incorrect response when request '" + redirectUrl + "', location: " + response.getRedirectUrl() + ", pageContent: " + page);
        }
    }

    public void doUnusualLogin(CommonPluginParam param, LoginResult result) throws InterruptedException {
        String validUrl = PatternUtils.group(result.getPageContent(), "var\\s*durexPop\\s*=\\s*AQPop\\(\\{\\s*url:'([^']+)',", 1);

        for (int i = 0;; i++) {
            try {
                checkSmsCode(validUrl, param);
                break;
            } catch (Exception e) {
                logger.error("短信验证码校验失败，taskId={}", param.getTaskId(), e);
                TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.QR_STATUS, QRStatus.VALIDATE_SMS_FAIL);
            }
            if (i == 2) {
                throw new IllegalStateException("短信校验失败！");
            }
            TimeUnit.SECONDS.sleep(1);
        }

        String referer = result.getRedirectUrl();

        String redirectUrl = findRedirectUrl(result.getResponse(), result.getLoginUrl());

        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(redirectUrl).setReferer(referer).invoke();

        checkResponseStatus(response, redirectUrl);

        response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(GOTO_TARGET_URL).setReferer(referer).invoke();

        checkResponseStatus(response, GOTO_TARGET_URL);

        redirectUrl = findRedirectUrl(response, GOTO_TARGET_URL);

        response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), GET).setFullUrl(redirectUrl).setReferer(referer).invoke();

        checkResponseStatus(response, redirectUrl);
    }

    private void checkSmsCode(String validUrl, CommonPluginParam param) throws InterruptedException {
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        String pageContent = openSmsValidPage(taskId, websiteName, validUrl);

        String jsonString = getXpathValue("//@value", getXpathValue("input#J_DurexData", pageContent));
        jsonString = jsonString.replaceAll("&quot;", "\"").replaceAll("&lt;", "<").replaceAll("&gt;", ">");

        JSONObject jsonObject = JSON.parseObject(jsonString);

        String requestParam = jsonObject.getString("param");

        String target = (String)(((JSONArray)JSONPath.eval(jsonObject, "$.options[?(@.checkType='phone')].optionText[?(@.type='phone')]" + ".code")).get(0));

        // 刷新短信间隔时间
        checkSmsSendInterval(taskId);

        sendSmsCode(taskId, websiteName, requestParam, target, validUrl);

        // TODO:埋点-短信验证码已发送 2019-02-14 李梁杰

        // 发送MQ指令(要求短信密码)
        QRStatusManager.setStatus(param.getTaskId(), QRStatus.REQUIRE_SMS);
        String directiveId = getRedisService().createDirectiveId();
        TaskUtils.addTaskShare(taskId, AttributeKey.DIRECTIVE_ID, directiveId);

        // 等待用户输入短信验证码,等待120秒
        QRLoginMonitor.sendTaskLog(taskId, "等待用户输入短信验证码");

        DirectiveResult<Map<String, Object>> receiveDirective = getRedisService().getDirectiveResult(directiveId, SMS_WAIT_TIMEOUT, TimeUnit.SECONDS);
        if (null == receiveDirective) {
            logger.error("等待用户输入短信验证码超时({}秒),taskId={},websiteName={},directiveId={}", SMS_WAIT_TIMEOUT, taskId, websiteName, directiveId);
            QRLoginMonitor.notifyLogger(param, "短信验证码校验超时", "输入短信验证码-->超时", ErrorCode.VALIDATE_SMS_TIMEOUT, "用户输入短信验证码超时,任务即将失败!超时时间(单位:秒):" + SMS_WAIT_TIMEOUT);
            throw new CommonException(ErrorCode.VALIDATE_SMS_TIMEOUT);
        }
        QRStatusManager.setStatus(param.getTaskId(), QRStatus.RECEIVED_SMS);
        // TODO:为什么等待3秒 2019-02-17 李梁杰
        TimeUnit.SECONDS.sleep(3);

        // TODO:埋点-短信验证码已接收 2019-02-14 李梁杰
        String smsCode = (String)receiveDirective.getData().get(AttributeKey.CODE);

        checkSmsCode(taskId, websiteName, requestParam, target, smsCode, validUrl);

        Response response = TaskHttpClient.create(taskId, websiteName, GET).setFullUrl(LOGIN_MID_URL).setReferer(validUrl).invoke();
        pageContent = response.getPageContent();
        logger.info("验证短信后跳转的页面响应, taskId={}, statusCode={}, pageContent={}", taskId, response.getStatusCode(), pageContent);

        checkResponseStatus(response, LOGIN_MID_URL);

        // TODO:埋点-短信验证通过 2019-02-14 李梁杰

        TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.TASK_SMS_CODE.getRedisKey(FormType.LOGIN), smsCode);
        QRLoginMonitor.notifyLogger(param, "短信验证码校验成功", "校验短信-->成功");
    }

    private String openSmsValidPage(Long taskId, String websiteName, String validUrl) {
        Response response = TaskHttpClient.create(taskId, websiteName, GET).setFullUrl(validUrl).setReferer(validUrl).invoke();

        String pageContent = response.getPageContent();
        logger.info("记录验证页面的页面响应,taskId={}, statusCode={}, pageContent={}", taskId, response.getStatusCode(), pageContent);

        checkResponseStatus(response, validUrl);

        return pageContent;
    }

    private void checkSmsCode(Long taskId, String websiteName, String params, String target, String smsCode, String referer) {
        String checkCodeUrl = "https://aq.taobao.com/durex/checkcode?param=" + params;
        String checkCodeData = "checkType=phone&target=" + target + "&safePhoneNum=&checkCode=" + smsCode;

        Response response = TaskHttpClient.create(taskId, websiteName, POST).setFullUrl(checkCodeUrl).setRequestBody(checkCodeData).setReferer(referer).invoke();

        String pageContent = response.getPageContent();
        logger.info("验证短信的页面响应, taskId={}, statusCode={}, pageContent={}", taskId, response.getStatusCode(), pageContent);

        checkResponseStatus(response, checkCodeUrl);

        if (!StringUtils.contains(pageContent, "\"isSuccess\":true")) {
            throw new IllegalStateException("验证短信验证码失败！response: " + response);
        }
    }

    private void sendSmsCode(Long taskId, String websiteName, String params, String target, String referer) {
        String sendCodeUrl = "https://aq.taobao.com/durex/sendcode?param=" + params + "&checkType=phone";
        String sendCodeData = "checkType=phone&target=" + target + "&safePhoneNum=&checkCode=";

        Response response = TaskHttpClient.create(taskId, websiteName, POST).setFullUrl(sendCodeUrl).setRequestBody(sendCodeData).setReferer(referer).invoke();

        String pageContent = response.getPageContent();
        logger.info("请求短信的页面响应, taskId={}, statusCode={}, pageContent={}", taskId, response.getStatusCode(), pageContent);

        checkResponseStatus(response, sendCodeUrl);

        if (!StringUtils.contains(pageContent, "\"isSuccess\":true")) {
            throw new IllegalStateException("发送短信验证码失败！response: " + response);
        }
    }

    /**
     * 检查短信发送间隔时间
     * 
     * @param taskId 任务ID
     */
    private void checkSmsSendInterval(Long taskId) {
        String latestSendSmsTime = TaskUtils.getTaskShare(taskId, AttributeKey.LATEST_SEND_SMS_TIME);
        if (StringUtils.isNoneBlank(latestSendSmsTime) && SMS_SEND_INTERVAL > 0) {
            long endTime = Long.parseLong(latestSendSmsTime) + TimeUnit.SECONDS.toMillis(SMS_SEND_INTERVAL);
            long nowTimeMillis = System.currentTimeMillis();
            if (nowTimeMillis < endTime) {
                try {
                    logger.info("刷新短信有间隔时间限制,latestSendSmsTime={},将等待{}秒", DateUtils.formatYmdhms(Long.parseLong(latestSendSmsTime)),
                        DateUtils.getUsedTime(nowTimeMillis, endTime));
                    TimeUnit.MILLISECONDS.sleep(endTime - nowTimeMillis);
                } catch (InterruptedException e) {
                    throw new RuntimeException("refeshSmsCode error", e);
                }
            }
        }
    }

    private String getXpathValue(String select, String content) {
        List<String> resultList = XPathUtil.getXpath(select, content);
        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        }
        return StringUtils.EMPTY;
    }

    private String findRedirectUrl(Response response, String url) {
        String redirectUrl = PatternUtils.group(response.getPageContent(), CERT_REDIRECT_URL_PATTERN, 1);
        if (StringUtils.isEmpty(redirectUrl)) {
            throw new IllegalStateException("Incorrect response when request '" + url + "', response: " + response);
        }

        return redirectUrl;
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

    private static UMID getUMID(boolean init, CommonPluginParam param) {
        String uabCollina = RedisUtils.get(UAB_COLLINA_CACHE_PREFIX + param.getTaskId());
        long timestamp = System.currentTimeMillis();
        if (init && StringUtils.isEmpty(uabCollina)) {
            uabCollina = timestamp + random(11);
            RedisUtils.set(UAB_COLLINA_CACHE_PREFIX + param.getTaskId(), uabCollina, 60 * 10);
        }

        String umidToken = RedisUtils.get(UMID_TOKEN_CACHE_PREFIX + param.getTaskId());
        if (init || StringUtils.isEmpty(umidToken)) {
            umidToken = "C" + uabCollina + timestamp + random(3);
            RedisUtils.set(UMID_TOKEN_CACHE_PREFIX + param.getTaskId(), umidToken, 60 * 10);
        }

        return new UMID(umidToken, uabCollina);
    }

    private static String random(int length) {
        StringBuilder text = new StringBuilder(length);
        for (; text.length() < length;) {
            text.append(String.valueOf(Math.random()).substring(2));
        }
        return text.substring(text.length() - length);
    }

    public static class LoginResult {
        private final String loginUrl;
        private final String accountNo;
        private final Response response;

        public LoginResult(String loginUrl, String accountNo, Response response) {
            this.loginUrl = loginUrl;
            this.accountNo = accountNo;
            this.response = response;
        }

        public String getLoginUrl() {
            return loginUrl;
        }

        public String getAccountNo() {
            return accountNo;
        }

        public Response getResponse() {
            return response;
        }

        public String getRedirectUrl() {
            return response.getRedirectUrl();
        }

        public String getPageContent() {
            return response.getPageContent();
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

    private static class UMID {

        private final String token;

        private final String uab;

        public UMID(String token, String uab) {
            this.token = token;
            this.uab = uab;
        }
    }
}
