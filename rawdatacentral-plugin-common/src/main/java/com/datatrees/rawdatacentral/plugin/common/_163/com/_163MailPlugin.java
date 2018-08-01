package com.datatrees.rawdatacentral.plugin.common._163.com;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.api.CommonPluginService;
import com.datatrees.spider.share.service.MessageService;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.service.plugin.CommonPlugin;
import com.datatrees.spider.share.service.plugin.QRPlugin;
import com.datatrees.rawdatacentral.api.internal.ThreadPoolService;
import com.datatrees.spider.share.common.http.ProxyUtils;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.qr.QRUtils;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.ProcessResultUtils;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.GroupEnum;
import com.datatrees.spider.share.domain.QRStatus;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.LoginMessage;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ProcessResult;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.rawdatacentral.plugin.util.selenium.SeleniumUtils;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 163邮箱账号密码登陆和二维码登陆
 * @author zhouxinghai
 * @date 2018/1/19
 */
public class _163MailPlugin implements CommonPlugin, QRPlugin {

    private static final Logger         logger = LoggerFactory.getLogger(_163MailPlugin.class);

    private              MessageService messageService;

    private              MonitorService monitorService;

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        ProxyUtils.setProxyEnable(param.getTaskId(), true);
        return new HttpResult().success();
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
        messageService = BeanFactoryUtils.getBean(MessageService.class);
        monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        Long taskId = param.getTaskId();
        ProcessResult<Object> processResult = ProcessResultUtils.createAndSaveProcessId();
        Long processId = processResult.getProcessId();
        ProcessResultUtils.setProcessExpire(taskId, processId, 2, TimeUnit.MINUTES);
        String websiteName = param.getWebsiteName();
        String username = param.getUsername();
        String password = param.getPassword();
        BeanFactoryUtils.getBean(ThreadPoolService.class).getMailLoginExecutors().submit(new Runnable() {
            @Override
            public void run() {
                String currentUrl = "https://mail.163.com/";
                RemoteWebDriver driver = null;
                try {
                    driver = SeleniumUtils.createClient(taskId, websiteName);
                    driver.get(currentUrl);
                    TimeUnit.SECONDS.sleep(3);
                    driver.switchTo().frame("x-URS-iframe");
                    driver.findElement(By.xpath("//input[@name='email']")).sendKeys(username);
                    driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
                    driver.findElement(By.xpath("//a[@id='dologin']")).click();
                    TimeUnit.SECONDS.sleep(3);
                    while (!isLoginSuccess(driver, param) && !ProcessResultUtils.processExpire(taskId, processId) && !isShowError(driver, param)) {
                        TimeUnit.SECONDS.sleep(1);
                    }

                    if (isLoginSuccess(driver, param)) {
                        ProcessResultUtils.saveProcessResult(processResult.success());
                        driver.switchTo().defaultContent();
                        currentUrl = "https://mail.163.com/entry/cgi/ntesdoor?from=smart";
                        driver.get(currentUrl);
                        TimeUnit.SECONDS.sleep(3);
                        currentUrl = driver.getCurrentUrl();
                        LoginMessage loginMessage = new LoginMessage();
                        loginMessage.setTaskId(taskId);
                        loginMessage.setWebsiteName(GroupEnum.MAIL_163.getWebsiteName());
                        loginMessage.setAccountNo(username);
                        loginMessage.setEndUrl(currentUrl);
                        logger.info("登陆成功,taskId={},websiteName={},endUrl={}", taskId, websiteName, currentUrl);
                        BeanFactoryUtils.getBean(CommonPluginService.class).sendLoginSuccessMsg(loginMessage, SeleniumUtils.getCookies(driver));
                        monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->校验-->成功", FormType.getName(param.getFormType())));
                        return;
                    }
                    messageService.sendTaskLog(taskId, "登录失败");
                    monitorService
                            .sendTaskLog(taskId, TemplateUtils.format("{}-->校验-->失败", FormType.getName(param.getFormType())), ErrorCode.LOGIN_FAIL,
                                    "登录失败,请重试!");
                    logger.warn("login by selinium fail,taskId={},websiteName={},endUrl={}", taskId, websiteName, currentUrl);
                    ProcessResultUtils.saveProcessResult(processResult.fail(ErrorCode.LOGIN_ERROR));
                } catch (Throwable e) {
                    messageService.sendTaskLog(taskId, "登录失败");
                    monitorService
                            .sendTaskLog(taskId, TemplateUtils.format("{}-->校验-->失败", FormType.getName(param.getFormType())), ErrorCode.LOGIN_ERROR,
                                    "登录失败,请重试!");
                    logger.warn("login by selinium error,taskId={},websiteName={},endUrl={}", taskId, websiteName, currentUrl, e);
                    ProcessResultUtils.saveProcessResult(processResult.fail(ErrorCode.LOGIN_ERROR));
                } finally {
                    SeleniumUtils.closeClient(driver);
                }
            }
        });
        return new HttpResult(true).success(processResult);
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        messageService = BeanFactoryUtils.getBean(MessageService.class);
        monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        ProcessResult<Object> processResult = ProcessResultUtils.createAndSaveProcessId();
        Long processId = processResult.getProcessId();
        ProcessResultUtils.setProcessExpire(taskId, processId, 2, TimeUnit.MINUTES);
        try {
            BeanFactoryUtils.getBean(ThreadPoolService.class).getMailLoginExecutors().submit(new Runnable() {
                @Override
                public void run() {
                    TaskUtils.addTaskShare(taskId, AttributeKey.CURRENT_LOGIN_PROCESS_ID, processId.toString());
                    RemoteWebDriver driver = null;
                    try {

                        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                                .setUrl("https://reg.163.com/services/getqrcodeid?product=mail163&usage=web").invoke();
                        String pageSource = response.getPageContent();
                        pageSource = StringUtils.substring(pageSource, 3).trim();
                        String uuid = JSON.parseObject(pageSource).getJSONObject("l").getString("i");
                        response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                                .setFullUrl("https://reg.163.com/services/getUrlQrcode?uuid={}&size=170", uuid).invoke();
                        String qrBase64 = response.getPageContentForBase64();
                        String qrText = QRUtils.parseCode(response.getResponse());

                        Map<String, String> dataMap = new HashMap<>();
                        dataMap.put(AttributeKey.QR_BASE64, qrBase64);
                        dataMap.put(AttributeKey.QR_TEXT, qrText);
                        ProcessResultUtils.saveProcessResult(processResult.success(dataMap));
                        ProcessResultUtils.setProcessExpire(taskId, processId, 2, TimeUnit.MINUTES);
                        TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, QRStatus.WAITING);
                        logger.info("refresh qr code success,taskId={},websiteName={}", taskId, websiteName);
                        messageService.sendTaskLog(taskId, "刷新二维码成功");
                        monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->刷新二维码-->成功", FormType.getName(param.getFormType())));
                        String qrStatus = getScandStatus(param, uuid);
                        boolean expire = ProcessResultUtils.processExpire(taskId, processId);
                        boolean lastLoginProcessId = TaskUtils.isLastLoginProcessId(taskId, processId);
                        while ((StringUtils.equals(qrStatus, QRStatus.WAITING) || StringUtils.equals(qrStatus, QRStatus.SCANNED)) && !expire &&
                                lastLoginProcessId) {
                            TimeUnit.SECONDS.sleep(3);
                            qrStatus = getScandStatus(param, uuid);
                            expire = ProcessResultUtils.processExpire(taskId, processId);
                            lastLoginProcessId = TaskUtils.isLastLoginProcessId(taskId, processId);
                            TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, qrStatus);
                        }
                        logger.warn("thread will close taskId={},qrStatus={},expire={},isLastLoginProcessId={}", taskId, qrStatus, expire,
                                lastLoginProcessId);
                        if (StringUtils.equals(qrStatus, QRStatus.SUCCESS) && TaskUtils.isLastLoginProcessId(taskId, processId)) {

                            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
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
                            String accountNo = PatternUtils.group(cookieString, "MAIL_PINFO=([^|]+)", 1);
                            String redisKey = RedisKeyPrefixEnum.TASK_INFO_ACCOUNT_NO.getRedisKey(taskId);
                            RedisUtils.setnx(redisKey, accountNo);
                            LoginMessage loginMessage = new LoginMessage();
                            loginMessage.setTaskId(taskId);
                            loginMessage.setWebsiteName(GroupEnum.MAIL_163.getWebsiteName());
                            loginMessage.setAccountNo(accountNo);
                            loginMessage.setEndUrl(endUrl);
                            logger.info("登陆成功,taskId={},websiteName={},endUrl={}", taskId, websiteName, endUrl);
                            BeanFactoryUtils.getBean(CommonPluginService.class).sendLoginSuccessMsg(loginMessage, SeleniumUtils.getCookies(driver));
                            TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, QRStatus.SUCCESS);
                            monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->校验-->成功", FormType.getName(param.getFormType())));
                            return;
                        }
                        messageService.sendTaskLog(taskId, "登录失败");
                        monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->校验-->失败", FormType.getName(param.getFormType())),
                                ErrorCode.LOGIN_FAIL, "登录失败,请重试!");
                        logger.error("current login process timeout,will close,taskId={},websiteName={}", taskId, websiteName);
                        if (TaskUtils.isLastLoginProcessId(taskId, processId)) {
                            TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, QRStatus.EXPIRE);
                        }
                        return;
                    } catch (Throwable e) {
                        messageService.sendTaskLog(taskId, "登录失败");
                        monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->校验-->失败", FormType.getName(param.getFormType())),
                                ErrorCode.LOGIN_ERROR, "登录失败,请重试!");
                        ProcessResultUtils.saveProcessResult(processResult.fail(ErrorCode.REFESH_QR_CODE_ERROR));
                        logger.error("current login process has error,will close,taskId={},websiteName={}", taskId, websiteName, e);
                    } finally {
                        SeleniumUtils.closeClient(driver);
                    }
                }
            });
        } catch (Throwable e) {
            logger.error("refresh qr code error,taskId={},websiteName={}", taskId, websiteName, e);
            messageService.sendTaskLog(taskId, "刷新二维码失败");
            monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->刷新二维码-->失败", FormType.getName(param.getFormType())),
                    ErrorCode.REFESH_QR_CODE_ERROR, "二维码刷新失败,请重试");
        }
        return new HttpResult(true).success(processResult);
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        messageService = BeanFactoryUtils.getBean(MessageService.class);
        monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        String qrStatus = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.QR_STATUS);
        String lastProcessId = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.CURRENT_LOGIN_PROCESS_ID);
        if (StringUtils.isNoneBlank(lastProcessId)) {
            ProcessResultUtils.setProcessExpire(param.getTaskId(), Long.valueOf(lastProcessId), 2, TimeUnit.MINUTES);
        }
        qrStatus = StringUtils.isNotBlank(qrStatus) ? qrStatus : QRStatus.WAITING;
        if (StringUtils.equals(qrStatus, QRStatus.EXPIRE)) {
            messageService.sendTaskLog(param.getTaskId(), "二维码过期,请重新获取");
            monitorService.sendTaskLog(param.getTaskId(), TemplateUtils.format("{}-->二维码过期", FormType.getName(param.getFormType())),
                    ErrorCode.QR_CODE_STATUS_EXPIRE, "二维码过期,请重新获取!");
            logger.warn("query qr status expire taskId={}", param.getTaskId());

        }
        return new HttpResult<>().success(qrStatus);
    }

    private String getScandStatus(CommonPluginParam param, String uuid) {
        String qrStatus = null;
        try {
            Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl("https://q.reg.163.com/services/ngxqrcodeauthstatus?uuid={}&product=mail163", uuid).invoke();
            String retCode = response.getPageContentForJSON().getString("retCode");
            logger.info("query qr status,retCode={},uuid={}", response, uuid);
            switch (retCode) {
                case "408":
                    qrStatus = QRStatus.WAITING;
                    break;
                case "404":
                    qrStatus = QRStatus.EXPIRE;
                    break;
                case "409":
                    qrStatus = QRStatus.SCANNED;
                    break;
                case "200":
                    qrStatus = QRStatus.SUCCESS;
                    break;
                default:
                    qrStatus = QRStatus.EXPIRE;
                    break;
            }
            logger.info("query qr status taskId={},status={},retCode={}", param.getTaskId(), qrStatus, retCode);
        } catch (Throwable e) {
            logger.error("query qr status error param={},uuid={}", param, uuid);
        }
        return qrStatus;
    }

    private boolean isLoginSuccess(RemoteWebDriver driver, CommonPluginParam param) {
        String currentUrl = driver.getCurrentUrl();
        logger.info("currentUrl = {},taskId={}", currentUrl, param.getTaskId());
        return StringUtils.startsWith(currentUrl, "https://mail.163.com/js6/main.jsp?sid=");
    }

    private boolean isShowError(RemoteWebDriver driver, CommonPluginParam param) {
        try {
            String currentUrl = driver.getCurrentUrl();
            if (StringUtils.equals(currentUrl, "https://mail.163.com/")) {
                WebElement nerror = driver.findElement(By.id("nerror"));
                String aClass = nerror.getAttribute("class");
                if (!StringUtils.equals(aClass, "m-nerror f-dn")) {
                    String text = nerror.findElement(By.className("ferrorhead")).getText();
                    if (StringUtils.equals("请点击验证码", text)) {
                        logger.info("验证码出现了,taskId={}", param.getTaskId());
                    }
                    if (StringUtils.isBlank(text)) {
                        logger.info("验证成功,class={},text={},taskId={}", aClass, text, param.getTaskId());
                    }
                    logger.info("class={},text={},taskId={}", aClass, text, param.getTaskId());
                    return true;
                }
            }
            return false;
        } catch (Exception e) {

        }
        return false;
    }

}
