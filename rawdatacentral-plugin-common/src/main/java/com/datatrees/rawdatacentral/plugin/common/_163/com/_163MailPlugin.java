package com.datatrees.rawdatacentral.plugin.common._163.com;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.internal.CommonPluginService;
import com.datatrees.rawdatacentral.api.internal.QRPluginService;
import com.datatrees.rawdatacentral.api.internal.ThreadPoolService;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.qr.QRUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.ProcessResultUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.QRStatus;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.util.selenium.SeleniumUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 163邮箱账号密码登陆和二维码登陆
 * @author zhouxinghai
 * @date 2018/1/19
 */
public class _163MailPlugin implements CommonPluginService, QRPluginService {

    private static final Logger logger = LoggerFactory.getLogger(_163MailPlugin.class);

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        return null;
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> validatePicCode(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        ProcessResult<Object> processResult = ProcessResultUtils.createAndSaveProcessId();
        try {
            BeanFactoryUtils.getBean(ThreadPoolService.class).getMailLoginExecutors().submit(new Runnable() {
                @Override
                public void run() {
                    TaskUtils.addTaskShare(taskId, AttributeKey.CURRENT_LOGIN_PROCESS_ID, processResult.getProcessId().toString());
                    RemoteWebDriver driver = null;
                    try {

                        Response response = TaskHttpClient.create(param, RequestType.GET)
                                .setUrl("https://reg.163.com/services/getqrcodeid?product=mail163&usage=web").invoke();
                        String pageSource = response.getPageContent();
                        pageSource = StringUtils.substring(pageSource, 3).trim();
                        String uuid = JSON.parseObject(pageSource).getJSONObject("l").getString("i");
                        response = TaskHttpClient.create(param, RequestType.GET)
                                .setFullUrl("https://reg.163.com/services/getUrlQrcode?uuid={}&size=170", uuid).invoke();
                        String qrBase64 = response.getPageContentForBase64();
                        String qrText = QRUtils.parseCode(response.getResponse());

                        Map<String, String> dataMap = new HashMap<>();
                        dataMap.put(AttributeKey.QR_BASE64, qrBase64);
                        dataMap.put(AttributeKey.QR_TEXT, qrText);
                        ProcessResultUtils.saveProcessResult(processResult.success(dataMap));
                        TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, QRStatus.WAITING);
                        logger.info("refresh qr code success,taskId={},websiteName={}", taskId, websiteName);

                        long endTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2);
                        String qrStatus = getScandStatus(param, uuid);
                        while (!StringUtils.equals(qrStatus, QRStatus.SUCCESS) && System.currentTimeMillis() <= endTime &&
                                TaskUtils.isLastLoginProcessId(taskId, processResult.getProcessId())) {
                            TimeUnit.SECONDS.sleep(3);
                            qrStatus = getScandStatus(param, uuid);
                            TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, qrStatus);
                        }
                        qrStatus = getScandStatus(param, uuid);
                        if (StringUtils.equals(qrStatus, QRStatus.SUCCESS) && TaskUtils.isLastLoginProcessId(taskId, processResult.getProcessId())) {

                            response = TaskHttpClient.create(param, RequestType.GET)
                                    .setFullUrl("https://reg.163.com/services/qrcodeauth?uuid={}&product=mail163", uuid).invoke();
                            String ticket = response.getPageContentForJSON().getString("ticket");

                            String url = TemplateUtils
                                    .format("https://reg.163.com/services/ticketlogin?noRedirect=1&product=mail163&ticket={}&url=https://mail.163.com" +
                                            "/entry/cgi/ntesdoor?allssl=true&df=mail163_mailmaster&from=web&language=-1&net=failed&allssl=true&race" +
                                            "=&style=7&url2=https://mail.163.com/errorpage/error163.htm", ticket);

                            driver = SeleniumUtils.createClient(taskId, websiteName);
                            driver.get(url);
                            TimeUnit.SECONDS.sleep(5);
                            String endUrl = driver.getCurrentUrl();
                            String cookieString = SeleniumUtils.getCookieString(driver);
                            LoginMessage loginMessage = new LoginMessage();
                            loginMessage.setTaskId(taskId);
                            loginMessage.setWebsiteName(GroupEnum.MAIL_163.getWebsiteName());
                            loginMessage.setAccountNo(null);
                            loginMessage.setEndUrl(endUrl);
                            loginMessage.setCookie(cookieString);
                            logger.info("登陆成功,taskId={},websiteName={},endUrl={}", taskId, websiteName, endUrl);
                            BeanFactoryUtils.getBean(CommonPluginApi.class).sendLoginSuccessMsg(loginMessage);
                            TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, QRStatus.SUCCESS);
                            return;
                        }
                        logger.error("current login process timeout,will close,taskId={},websiteName={}", taskId, websiteName);
                        return;
                    } catch (Throwable e) {
                        ProcessResultUtils.saveProcessResult(processResult.fail(ErrorCode.REFESH_QR_CODE_ERROR));
                        logger.error("current login process has error,will close,taskId={},websiteName={}", taskId, websiteName, e);
                    } finally {
                        SeleniumUtils.closeClient(driver);
                    }
                }
            });
        } catch (Throwable e) {
            logger.error("refresh qr code error,taskId={},websiteName={}", taskId, websiteName, e);
        }
        return new HttpResult(true).success(processResult);
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        String qrStatus = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.QR_STATUS);
        return new HttpResult<>().success(StringUtils.isNotBlank(qrStatus) ? qrStatus : QRStatus.WAITING);
    }

    private String getScandStatus(CommonPluginParam param, String uuid) {
        try {
            Response response = TaskHttpClient.create(param, RequestType.GET)
                    .setFullUrl("https://q.reg.163.com/services/ngxqrcodeauthstatus?uuid={}&product=mail163", uuid).invoke();
            String retCode = response.getPageContentForJSON().getString("retCode");
            logger.info("query qr status,retCode={},uuid={}", response, uuid);
            switch (retCode) {
                case "408":
                    return QRStatus.WAITING;
                case "404":
                    return QRStatus.EXPIRE;
                case "409":
                    return QRStatus.SCANNED;
                case "200":
                    return QRStatus.SUCCESS;
                default:
                    return QRStatus.EXPIRE;
            }
        } catch (Throwable e) {
            logger.error("query qr status error param={},uuid={}", param, uuid);
        }
        return QRStatus.EXPIRE;
    }

}
