package com.datatrees.rawdatacentral.plugin.mail.qq.com.h5;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.crawler.core.util.SeliniumUtils;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.internal.CommonPluginService;
import com.datatrees.rawdatacentral.api.internal.ThreadPoolService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.ProcessResultUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
import com.datatrees.rawdatacentral.domain.enums.TopicTag;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QQMailPlugin implements CommonPluginService {

    private static final Logger logger = LoggerFactory.getLogger(QQMailPlugin.class);

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        return new HttpResult().success();
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        return new HttpResult().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        return new HttpResult().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> validatePicCode(CommonPluginParam param) {
        return new HttpResult().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        ProcessResult<Object> processResult = ProcessResultUtils.createAndSaveProcessId();
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        String username = param.getUsername();
        String password = param.getPassword();
        BeanFactoryUtils.getBean(ThreadPoolService.class).getMailLoginExecutors().submit(new Runnable() {
            @Override
            public void run() {
                String currentUrl = "http://w.mail.qq.com";
                try {
                    WebDriver driver = SeliniumUtils.createClient(taskId, websiteName);

                    driver.get(currentUrl);
                    TimeUnit.SECONDS.sleep(3);
                    driver.findElement(By.xpath("//input[@id='u']")).sendKeys(username);
                    driver.findElement(By.xpath("//input[@id='p']")).sendKeys(password);
                    driver.findElement(By.xpath("//div[@id='go']")).click();
                    TimeUnit.SECONDS.sleep(3);
                    currentUrl = driver.getCurrentUrl();
                    logger.info("登陆后currentUrl={}", currentUrl);
                    if (StringUtils.startsWith(currentUrl, "https://w.mail.qq.com/cgi-bin/today")) {
                        String cookieString = SeliniumUtils.getCookieString(driver);
                        Map<String, String> loginData = new HashMap<>();
                        loginData.put(AttributeKey.END_URL, currentUrl);
                        loginData.put(AttributeKey.TASK_ID, taskId.toString());
                        loginData.put(AttributeKey.WEBSITE_NAME, websiteName);
                        loginData.put(AttributeKey.ACCOUNT_NO, username);
                        loginData.put(AttributeKey.COOKIE, cookieString);
                        logger.info("登陆成功,taskId={},websiteName={},cookieString={},endUrl={}", taskId, websiteName, cookieString, currentUrl);
                        BeanFactoryUtils.getBean(MessageService.class)
                                .sendMessage(TopicEnum.RAWDATA_INPUT.getCode(), TopicTag.LOGIN_INFO.getTag(), loginData);

                        ProcessResultUtils.saveProcessResult(processResult.success());
                        return;
                    }
                    logger.warn("login by selinium fail,taskId={},websiteName={},endUrl={}", taskId, websiteName, currentUrl);
                    ProcessResultUtils.saveProcessResult(processResult.fail(ErrorCode.LOGIN_ERROR));
                } catch (Throwable e) {
                    logger.warn("login by selinium error,taskId={},websiteName={},endUrl={}", taskId, websiteName, currentUrl, e);
                    ProcessResultUtils.saveProcessResult(processResult.fail(ErrorCode.LOGIN_ERROR));
                }
            }
        });
        return new HttpResult(true).success(processResult);
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        return new HttpResult().failure(ErrorCode.NOT_SUPORT_METHOD);
    }
}
