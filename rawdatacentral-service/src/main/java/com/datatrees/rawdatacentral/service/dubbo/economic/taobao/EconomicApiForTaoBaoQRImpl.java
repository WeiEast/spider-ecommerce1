package com.datatrees.rawdatacentral.service.dubbo.economic.taobao;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.plugin.qrcode.QRCodeVerification;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.economic.taobao.EconomicApiForTaoBaoQR;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
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

    private static final String IS_RUNING = "economic_qr_is_runing_";
    private static final String QR_STATUS = "economic_qr_status_";
    private static final Logger logger    = LoggerFactory.getLogger(EconomicApiForTaoBaoQRImpl.class);

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://qrlogin.taobao.com/qrcodelogin/generateQRCode4Login.do?adUrl=&adImage=&adText=&viewFd4PC=&viewFd4Mobile=" +
                    "&from=tb&_ksTS={}&callback=json";
            response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "")
                    .setFullUrl(templateUrl, System.currentTimeMillis() + "_" + (int) (Math.random() * 1000)).invoke();
            String jsonString = PatternUtils.group(response.getPageContent(), "json\\(([^\\)]+)\\)", 1);
            JSONObject json = JSON.parseObject(jsonString);
            String imgUrl = json.getString("url");
            String lgToken = json.getString("lgToken");
            TaskUtils.addTaskShare(param.getTaskId(), "lgToken", lgToken);
            if (!StringUtils.contains(imgUrl, "https:")) {
                imgUrl = "https:" + imgUrl;
            }
            response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "").setFullUrl(imgUrl).invoke();
            byte[] bytes = response.getResponse();
            String qrBase64 = response.getPageContentForBase64();
            QRUtils qrUtils = new QRUtils();
            String qrText = qrUtils.parseCode(bytes);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("qrBase64", qrBase64);
            dataMap.put("qrText", qrText);
            String isRunning = RedisUtils.get(IS_RUNING + param.getTaskId());
            if (!StringUtils.equals(isRunning, "true")) {
                RedisUtils.set(IS_RUNING + param.getTaskId(), "true", 60);
                doProcess(param);
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
            status = "FAILED";
        }
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("qrStatus", status);
        return new HttpResult<>().success(dataMap);
    }

    public String getQRStatusCode(CommonPluginParam param) {
        Response response = null;
        try {
            String lgToken = TaskUtils.getTaskShare(param.getTaskId(), "lgToken");
            String templateUrl = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do?lgToken={}&defaulturl=&_ksTS={}&callback=json";
            response = TaskHttpClient.create(param.getTaskId(), "taobao.com", RequestType.GET, "")
                    .setFullUrl(templateUrl, lgToken, System.currentTimeMillis() + "_" + (int) (Math.random() * 1000)).invoke();
            String resultJson = PatternUtils.group(response.getPageContent(), "json\\(([^\\)]+)\\)", 1);
            JSONObject json = JSON.parseObject(resultJson);
            String code = json.getString("code");
            return code;
        } catch (Exception e) {
            logger.error("获取二维码状态失败，param={},response={}", param, response, e);
            return null;
        }
    }

    public void doProcess(CommonPluginParam param) {
        String status;
        while (true) {
            String code = getQRStatusCode(param);
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
            RedisUtils.set(QR_STATUS + param.getTaskId(), status, 60);
            if (StringUtils.equals(status, QRCodeVerification.QRCodeStatus.CONFIRMED.name())) {
                logger.info("用户已扫码并确认，准备发送登录消息，taskId={}",param.getTaskId());
                String cookieString = TaskUtils.getCookieString(param.getTaskId());
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setTaskId(param.getTaskId());
                loginMessage.setWebsiteName("taobao.com");
                loginMessage.setCookie(cookieString);
                logger.info("登陆成功,taskId={},websiteName={}", param.getTaskId(), "taobao.com");
                BeanFactoryUtils.getBean(CommonPluginApi.class).sendLoginSuccessMsg(loginMessage);
                break;
            } else {
                continue;
            }
        }
    }
}
