package com.datatrees.rawdatacentral.service.dubbo.economic.taobao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.crawler.plugin.qrcode.QRCodeVerification;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.economic.taobao.EconomicApiForTaoBaoQR;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.JsoupXpathUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.dubbo.economic.taobao.util.QRUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by guimeichao on 18/1/11.
 */
@Service
public class EconomicApiForTaoBaoQRImpl implements EconomicApiForTaoBaoQR {

    private static final String IS_RUNING            = "economic_qr_is_runing_";
    private static final String IS_INIT              = "economic_qr_is_init_";
    private static final String QR_STATUS            = "economic_qr_status_";
    private static final String QR_STATUS_QUERY_TIME = "economic_qr_status_query_time_";
    private static final Logger logger               = LoggerFactory.getLogger(EconomicApiForTaoBaoQRImpl.class);
    private static final String preLoginUrl          = "https://login.taobao.com/member/login.jhtml?style=taobao&goto=https://consumeprod.alipay" +
            ".com/record/index.htm%3Fsign_from%3D3000";

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            String isInit = RedisUtils.get(IS_INIT + param.getTaskId());
            if (StringUtils.isEmpty(isInit)) {
                init(param);
            }
            String viewFd4PC = TaskUtils.getTaskShare(param.getTaskId(), "viewFd4PC");
            String templateUrl = "https://qrlogin.taobao.com/qrcodelogin/generateQRCode4Login" +
                    ".do?adUrl=&adImage=&adText=&viewFd4PC={}&viewFd4Mobile=" + "&from=tb&_ksTS={}&callback=json";
            response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "")
                    .setFullUrl(templateUrl, viewFd4PC, System.currentTimeMillis() + "_" + (int) (Math.random() * 1000)).setReferer(preLoginUrl)
                    .invoke();
            String jsonString = PatternUtils.group(response.getPageContent(), "json\\(([^\\)]+)\\)", 1);
            JSONObject json = JSON.parseObject(jsonString);
            String imgUrl = json.getString("url");
            String lgToken = json.getString("lgToken");
            TaskUtils.addTaskShare(param.getTaskId(), "lgToken", lgToken);
            if (!StringUtils.contains(imgUrl, "https:")) {
                imgUrl = "https:" + imgUrl;
            }
            response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "").setFullUrl(imgUrl).setReferer(preLoginUrl)
                    .invoke();
            byte[] bytes = response.getResponse();
            String qrBase64 = response.getPageContentForBase64();
            QRUtils qrUtils = new QRUtils();
            String qrText = qrUtils.parseCode(bytes);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("qrBase64", qrBase64);
            dataMap.put("qrText", qrText);
            String isRunning = RedisUtils.get(IS_RUNING + param.getTaskId());
            RedisUtils.set(QR_STATUS_QUERY_TIME + param.getTaskId(), System.currentTimeMillis() + "", 60 * 2);
            if (!StringUtils.equals(isRunning, "true")) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        try {
                            doProcess(param);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }
            logger.info("刷新二维码成功，taskId={}", param.getTaskId());
            return result.success(dataMap);
        } catch (Exception e) {
            logger.error("刷新二维码失败，param={},response={}", param, response, e);
            return result.failure("刷新二维码失败");
        }
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        String status = RedisUtils.get(QR_STATUS + param.getTaskId());
        if (StringUtils.isEmpty(status)) {
            status = "WAITING";
        }
        RedisUtils.set(QR_STATUS_QUERY_TIME + param.getTaskId(), System.currentTimeMillis() + "", 60 * 5);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("qrStatus", status);
        return new HttpResult<>().success(dataMap);
    }

    public String getQRStatusCode(CommonPluginParam param) {
        Response response = null;
        try {
            String lgToken = TaskUtils.getTaskShare(param.getTaskId(), "lgToken");
            String templateUrl
                    = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do?lgToken={}&defaulturl=https%3A%2F%2Fconsumeprod.alipay.com%2Frecord%2Findex.htm%3Fsign_from%3D3000&_ksTS={}&callback=json";
            response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "")
                    .setFullUrl(templateUrl, lgToken, System.currentTimeMillis() + "_" + (int) (Math.random() * 1000)).setReferer(preLoginUrl)
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
            return null;
        }
    }

    public void doProcess(CommonPluginParam param) throws InterruptedException {
        String status;
        while (true) {
            String code = getQRStatusCode(param);
            RedisUtils.set(IS_RUNING + param.getTaskId(), "true", 10);
            String time = RedisUtils.get(QR_STATUS_QUERY_TIME + param.getTaskId());
            long now = System.currentTimeMillis();
            if (now - Long.parseLong(time) > 1000 * 60 * 1) {
                Thread.currentThread().interrupt();
                break;
            }
            switch (code) {
                case "10000":
                    status = QRCodeVerification.QRCodeStatus.WAITING.name();
                    break;
                case "10001":
                    status = QRCodeVerification.QRCodeStatus.SCANNED.name();
                    break;
                case "10004":
                    status = QRCodeVerification.QRCodeStatus.EXPIRE.name();
                    break;
                case "10006":
                    status = QRCodeVerification.QRCodeStatus.CONFIRMED.name();
                    break;
                default:
                    status = QRCodeVerification.QRCodeStatus.FAILED.name();
            }
            RedisUtils.set(QR_STATUS + param.getTaskId(), status, 60 * 2);
            logger.info("状态更新成功,当前二维码状态：{},taskId={}", status, param.getTaskId());
            if (StringUtils.equals(status, QRCodeVerification.QRCodeStatus.CONFIRMED.name())) {
                String loginUrl = TaskUtils.getTaskShare(param.getTaskId(), "loginUrl");
                Response response = null;
                try {
                    response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "").setFullUrl(loginUrl)
                            .setReferer(preLoginUrl).invoke();
                    String redirectUrl = PatternUtils.group(response.getPageContent(), "window\\.location\\.href\\s*=\\s*\"([^\"]+)\";", 1);
                    String referer
                            = "https://auth.alipay.com/login/trust_login.do?null&sign_from=3000&goto=https://consumeprod.alipay.com/record/index.htm";
                    response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "").setFullUrl(redirectUrl).setReferer(referer)
                            .invoke();
                    executeScriptSubmit(param.getTaskId(), "taobao.com", "", response.getPageContent());
                } catch (Exception e) {
                    logger.error("淘宝二维码登录处理失败，taskId={},response={}", param.getTaskId(), response, e);
                }
                logger.info("用户已扫码并确认，准备发送登录消息，taskId={}", param.getTaskId());
                String cookieString = TaskUtils.getCookieString(param.getTaskId());
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setTaskId(param.getTaskId());
                loginMessage.setWebsiteName("taobao.com");
                loginMessage.setCookie(cookieString);
                logger.info("登陆成功,taskId={},websiteName={}", param.getTaskId(), "taobao.com");
                BeanFactoryUtils.getBean(CommonPluginApi.class).sendLoginSuccessMsg(loginMessage);
                Thread.currentThread().interrupt();
                break;
            } else {
                Thread.sleep(1000);
                continue;
            }
        }
    }

    private void init(CommonPluginParam param) {
        RedisUtils.set(IS_INIT + param.getTaskId(), "true", 60 * 5);
        Response response = null;
        try {
            String templateUrl = preLoginUrl;
            response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "").setFullUrl(templateUrl).invoke();
            String viewFd4PC = StringUtils.EMPTY;
            List<String> viewFd4PCList = XPathUtil.getXpath("//input[@name='viewFd4PC']/@value", response.getPageContent());
            if (!viewFd4PCList.isEmpty()) {
                viewFd4PC = viewFd4PCList.get(0);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "viewFd4PC", viewFd4PC);
            logger.info("淘宝二维码登录-->初始化成功，taskId={}", param.getTaskId());
        } catch (Exception e) {
            logger.error("淘宝二维码登录-->初始化失败，taskId={},response={}", param.getTaskId(), response, e);
        }

    }

    /**
     * 处理跳转服务
     * @param pageContent
     * @return
     */
    private String executeScriptSubmit(Long taskId, String websiteName, String remark, String pageContent) {
        String action = JsoupXpathUtils.selectFirst(pageContent, "//form/@action");
        String method = JsoupXpathUtils.selectFirst(pageContent, "//form/@method");
        List<Map<String, String>> list = JsoupXpathUtils.selectAttributes(pageContent, "input[name]");
        String url = action.replaceAll("\\?", "");
        Map<String, Object> params = new HashMap<>();

        if (null != list && !list.isEmpty()) {
            for (Map<String, String> map : list) {
                if (map.containsKey("name") && map.containsKey("value")) {
                    params.put(map.get("name"), map.get("value"));
                }
            }
        }
        RequestType requestType = StringUtils.equalsIgnoreCase("post", method) ? RequestType.POST : RequestType.GET;
        Response response = TaskHttpClient.create(taskId, websiteName, requestType, remark).setUrl(url).setParams(params).invoke();
        return response.getPageContent();
    }

}
