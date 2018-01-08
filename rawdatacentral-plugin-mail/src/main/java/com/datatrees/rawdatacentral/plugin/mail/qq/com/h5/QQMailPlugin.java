package com.datatrees.rawdatacentral.plugin.mail.qq.com.h5;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.concurrent.TimeUnit;

import com.datatrees.crawler.core.util.SeliniumUtils;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.internal.CommonPluginService;
import com.datatrees.rawdatacentral.api.internal.ThreadPoolService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.ProcessResultUtils;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;
import com.datatrees.rawdatacentral.plugin.mail.qq.com.h5.util.ImageOcrUtils;
import com.datatrees.rawdatacentral.plugin.mail.qq.com.h5.util.ImageUtils;
import com.datatrees.rawdatacentral.plugin.mail.qq.com.h5.util.domain.ColorPoint;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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

                    if (!StringUtils.startsWith(currentUrl, "https://w.mail.qq.com/cgi-bin/today")) {
                        logger.info("安全验证出现了,{}",currentUrl);
                        driver.switchTo().frame(0);
                        WebElement element = driver.findElement(By.xpath("//div[contains(.,'安全验证')]"));
                        moveHk(driver);
                    }

                    if (StringUtils.startsWith(currentUrl, "https://w.mail.qq.com/cgi-bin/today")) {
                        String cookieString = SeliniumUtils.getCookieString(driver);
                        LoginMessage loginMessage = new LoginMessage();
                        loginMessage.setTaskId(taskId);
                        loginMessage.setWebsiteName(websiteName);
                        loginMessage.setAccountNo(username);
                        loginMessage.setEndUrl(currentUrl);
                        loginMessage.setCookie(cookieString);
                        logger.info("登陆成功,taskId={},websiteName={},endUrl={}", taskId, websiteName, currentUrl);
                        BeanFactoryUtils.getBean(CommonPluginApi.class).sendLoginSuccessMsg(loginMessage);

                        ProcessResultUtils.saveProcessResult(processResult.success());
                        return;
                    }

                    SeliniumUtils.closeClient(driver);
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

    private void moveHk(WebDriver driver) throws Exception {
        WebElement bgImg = driver.findElement(By.xpath("//img[@id='slideBkg']"));
        if (null != bgImg) {
            String src = bgImg.getAttribute("src");
            logger.info("src={}", src);
            String baseUrl = src.substring(0, src.length() - 1);
            logger.info("baseUrl={}", baseUrl);
            byte[] img0 = ImageUtils.downImage(baseUrl + "0");
            byte[] img1 = ImageUtils.downImage(baseUrl + "1");
            byte[] img2 = ImageUtils.downImage(baseUrl + "2");
            FileUtils.writeByteArrayToFile(new File("/data/0.jpeg"), img0);
            FileUtils.writeByteArrayToFile(new File("/data/1.jpeg"), img1);
            FileUtils.writeByteArrayToFile(new File("/data/2.png"), img2);
            ColorPoint point = ImageOcrUtils.ocr(img0, img1, img2);
            int realWith = ImageIO.read(new ByteArrayInputStream(img0)).getWidth();
            int move = point.getAbsoluteX() * 280 / realWith - 12 - 55 / 2;
            logger.info("move={},realWith={},bgWith={},x={}", move, realWith, bgImg.getSize().getWidth(), point.getAbsoluteX());

            WebElement el = driver.findElement(By.id("tcaptcha_drag_thumb"));
            Actions actions = new Actions(driver);
            new Actions(driver).clickAndHold(el).perform();
            actions.moveByOffset(move, 0).click().perform();

            TimeUnit.SECONDS.sleep(5);
            String pageSource = driver.getPageSource();
            if (pageSource.contains("拖动下方滑块完成拼图")) {
                logger.info("拖动下方滑块完成拼图");
                driver.findElement(By.id("e_reload")).click();
                TimeUnit.SECONDS.sleep(10);
                moveHk(driver);
            }

        }
    }
}
