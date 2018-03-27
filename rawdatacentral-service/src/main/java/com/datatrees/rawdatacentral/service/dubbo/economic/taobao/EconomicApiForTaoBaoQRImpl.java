package com.datatrees.rawdatacentral.service.dubbo.economic.taobao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.plugin.qrcode.QRCodeVerification;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.economic.taobao.EconomicApiForTaoBaoQR;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.JsoupXpathUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.dubbo.economic.taobao.util.QRUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by guimeichao on 18/1/11.
 */
@Service
public class EconomicApiForTaoBaoQRImpl implements EconomicApiForTaoBaoQR {

    private static final Logger logger               = LoggerFactory.getLogger(EconomicApiForTaoBaoQRImpl.class);
    private static final String IS_RUNNING           = "economic_qr_is_runing_";
    private static final String IS_INIT              = "economic_qr_is_init_";
    private static final String QR_STATUS            = "economic_qr_status_";
    private static final String QR_STATUS_QUERY_TIME = "economic_qr_status_query_time_";
    private static final String ALIPAY_URL           = "https://consumeprod.alipay.com/record/advanced.htm";
    private static final String AUTO_SIGN_ALIPAY_URL = ALIPAY_URL + "?sign_from=3000";
    private static final String preLoginUrl          = "https://login.taobao.com/member/login.jhtml?style=taobao&goto=" + encodeUrl(AUTO_SIGN_ALIPAY_URL);
            ;
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private MessageService messageService;

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        // 设置请求使用代理
        ProxyUtils.setProxyEnable(param.getTaskId(), true);

        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            String isInit = RedisUtils.get(IS_INIT + param.getTaskId());
            if (StringUtils.isEmpty(isInit)) {
                init(param);
            }

            String templateUrl = "https://qrlogin.taobao.com/qrcodelogin/generateQRCode4Login" +
                    ".do?adUrl=&adImage=&adText=&viewFd4PC=&viewFd4Mobile=&from=tb&_ksTS={}&callback=json";
            response = TaskHttpClient.create(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(), RequestType.GET, "")
                    .setFullUrl(templateUrl, System.currentTimeMillis() + "_" + (int) (Math.random() * 1000)).setReferer(preLoginUrl).invoke();
            String jsonString = PatternUtils.group(response.getPageContent(), "json\\(([^\\)]+)\\)", 1);
            JSONObject json = JSON.parseObject(jsonString);
            String imgUrl = json.getString("url");
            String lgToken = json.getString("lgToken");
            TaskUtils.addTaskShare(param.getTaskId(), "lgToken", lgToken);
            if (!StringUtils.contains(imgUrl, "https:")) {
                imgUrl = "https:" + imgUrl;
            }
            response = TaskHttpClient.create(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(), RequestType.GET, "").setFullUrl(imgUrl)
                    .setReferer(preLoginUrl).invoke();
            byte[] bytes = response.getResponse();
            String qrBase64 = response.getPageContentForBase64();
            QRUtils qrUtils = new QRUtils();
            String qrText = qrUtils.parseCode(bytes);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("qrBase64", qrBase64);
            dataMap.put("qrText", qrText);
            String isRunning = RedisUtils.get(IS_RUNNING + param.getTaskId());
            RedisUtils.set(QR_STATUS_QUERY_TIME + param.getTaskId(), System.currentTimeMillis() + "", 60 * 2);
            if (!StringUtils.equals(isRunning, "true")) {
                Thread t = new Thread(() -> {
                    try {
                        doProcess(param);
                    } catch (InterruptedException e) {
                        logger.error("QRCode status checking process was interrupted!", e);
                    }
                });
                t.start();
            }
            RedisUtils.set(QR_STATUS + param.getTaskId(), "WAITING", 60 * 2);
            logger.info("刷新二维码成功，taskId={}", param.getTaskId());
            messageService.sendTaskLog(param.getTaskId(), "刷新二维码成功");
            monitorService.sendTaskLog(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(),
                    TemplateUtils.format("{}-->刷新二维码-->成功", FormType.getName(FormType.LOGIN)));
            return result.success(dataMap);
        } catch (Exception e) {
            logger.error("刷新二维码失败，param={},response={}", param, response, e);
            messageService.sendTaskLog(param.getTaskId(), "刷新二维码失败");
            monitorService.sendTaskLog(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(),
                    TemplateUtils.format("{}-->刷新二维码-->失败", FormType.getName(FormType.LOGIN)), ErrorCode.REFESH_QR_CODE_ERROR, "二维码刷新失败,请重试");
            return result.success("刷新二维码失败");
        }
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        String status = RedisUtils.get(QR_STATUS + param.getTaskId());
        if (StringUtils.isEmpty(status)) {
            status = "WAITING";
        }
        RedisUtils.set(QR_STATUS_QUERY_TIME + param.getTaskId(), System.currentTimeMillis() + "", 60 * 5);
        return new HttpResult<>().success(status);
    }


    private void doProcess(CommonPluginParam param) throws InterruptedException {
        while (true) {
            RedisUtils.set(IS_RUNNING + param.getTaskId(), "true", 10);
            String time = RedisUtils.get(QR_STATUS_QUERY_TIME + param.getTaskId());
            long now = System.currentTimeMillis();
            if (now - Long.parseLong(time) > 1000 * 30) {
                break;
            }

            QRCodeVerification.QRCodeStatus status = queryQRCodeStatus(param);

            logger.info("状态更新成功,当前二维码状态：{},taskId={}", status, param.getTaskId());
            if (QRCodeVerification.QRCodeStatus.CONFIRMED.equals(status)) {
                String loginUrl = TaskUtils.getTaskShare(param.getTaskId(), "loginUrl");

                String accountNo = getAccountNo(loginUrl);
                if(accountNo != null){
                    String redisKey = RedisKeyPrefixEnum.TASK_INFO_ACCOUNT_NO.getRedisKey(param.getTaskId());
                    RedisUtils.setnx(redisKey, accountNo);
                }

                Response response = null;
                try {
                    response = TaskHttpClient.create(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(), RequestType.GET, "")
                            .setFullUrl(loginUrl).setReferer(preLoginUrl).invoke();
                    String redirectUrl = PatternUtils.group(response.getPageContent(), "window\\.location\\.href\\s*=\\s*\"([^\"]+)\";", 1);
                    String referer = "https://auth.alipay.com/login/trust_login.do?null&sign_from=3000&goto="+ALIPAY_URL;
                    response = TaskHttpClient.create(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(), RequestType.GET, "")
                            .setFullUrl(redirectUrl).setReferer(referer).invoke();
                    processCertCheck(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(), "", response.getPageContent());
                } catch (Exception e) {
                    RedisUtils.set(QR_STATUS + param.getTaskId(), QRCodeVerification.QRCodeStatus.FAILED.name(), 60 * 2);

                    logger.error("淘宝二维码登录处理失败，taskId={},response={}", param.getTaskId(), response, e);
                    messageService.sendTaskLog(param.getTaskId(), "登录失败");
                    monitorService.sendTaskLog(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(),
                            TemplateUtils.format("{}-->校验-->失败", FormType.getName(FormType.LOGIN)), ErrorCode.LOGIN_FAIL, "登陆失败,请重试");
                    break;
                }

                RedisUtils.set(QR_STATUS + param.getTaskId(), QRCodeVerification.QRCodeStatus.CONFIRMED.name(), 60 * 2);

                logger.info("用户已扫码并确认，准备发送登录消息，taskId={}", param.getTaskId());
                String cookieString = TaskUtils.getCookieString(param.getTaskId());
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setTaskId(param.getTaskId());
                loginMessage.setWebsiteName(GroupEnum.TAOBAO_COM.getWebsiteName());
                loginMessage.setCookie(cookieString);
                loginMessage.setAccountNo(StringUtils.defaultString(accountNo));
                TaskUtils.addTaskShare(param.getTaskId(), "username", accountNo);
                logger.info("登陆成功,taskId={},websiteName={}", param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName());
                monitorService.sendTaskLog(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(),
                        TemplateUtils.format("{}-->校验-->成功", FormType.getName(FormType.LOGIN)));
                BeanFactoryUtils.getBean(CommonPluginApi.class).sendLoginSuccessMsg(loginMessage);
                break;
            }

            RedisUtils.set(QR_STATUS + param.getTaskId(), status.name(), 60 * 2);
            Thread.sleep(2000);
        }
    }

    /**
     * 处理跳转服务
     */
    private void processCertCheck(Long taskId, String websiteName, String remark, String pageContent) {
        String url = JsoupXpathUtils.selectFirst(pageContent, "//form/@action");
        if (StringUtils.isEmpty(url)) throw new IllegalArgumentException("Error find form action when redirecting to alipay auth.");

        String params = null;
        List<Element> list = JsoupXpathUtils.selectElements(pageContent, "//form//input[@name]|//form//textarea[@name]");
        if (null != list && !list.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<>(list.size());
            for (Element element : list) {
                pairs.add(new BasicNameValuePair(element.attr("name"), element.val()));
            }
            params = pairs.stream().map(pair -> pair.getName() + "=" + pair.getValue()).collect(Collectors.joining("&"));
        }
        Response response = TaskHttpClient.create(taskId, websiteName, RequestType.POST, remark).setUrl(url).setRequestBody(params).invoke();
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

    private QRCodeVerification.QRCodeStatus queryQRCodeStatus(CommonPluginParam param){
        String code = getQRStatusCode(param);
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

    private String getQRStatusCode(CommonPluginParam param) {
        Response response = null;
        try {
            String lgToken = TaskUtils.getTaskShare(param.getTaskId(), "lgToken");
            String templateUrl
                    = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do?lgToken={}&defaulturl={}&_ksTS={}&callback=json";
            response = TaskHttpClient.create(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(), RequestType.GET, "")
                    .setFullUrl(templateUrl, lgToken, encodeUrl(AUTO_SIGN_ALIPAY_URL), System.currentTimeMillis() + "_" + (int) (Math.random() * 1000)).setReferer(preLoginUrl)
                    .invoke();
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

    private void init(CommonPluginParam param) {
        RedisUtils.set(IS_INIT + param.getTaskId(), "true", 60 * 5);
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "淘宝");
            response = TaskHttpClient.create(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(), RequestType.GET, "").setFullUrl(preLoginUrl)
                    .invoke();
            logger.info("淘宝二维码登录-->初始化成功，taskId={}", param.getTaskId());
            messageService.sendTaskLog(param.getTaskId(), "初始化二维码成功");
            monitorService.sendTaskLog(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(),
                    TemplateUtils.format("{}-->初始化-->成功", FormType.getName(FormType.LOGIN)));
        } catch (Exception e) {
            logger.error("淘宝二维码登录-->初始化失败，taskId={},response={}", param.getTaskId(), response, e);
            messageService.sendTaskLog(param.getTaskId(), "初始化二维码失败");
            monitorService.sendTaskLog(param.getTaskId(), GroupEnum.TAOBAO_COM.getWebsiteName(),
                    TemplateUtils.format("{}-->初始化-->失败", FormType.getName(FormType.LOGIN)), ErrorCode.TASK_INIT_ERROR, "初始化失败");
        }

    }

    private static String encodeUrl(String queryString){
        try {
            return URLEncoder.encode(queryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage());
        }

        return queryString;
    }
}
