package com.datatrees.rawdatacentral.plugin.mail.qq.com.h5;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.concurrent.TimeUnit;

import com.datatrees.crawler.core.util.SeliniumUtils;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.internal.CommonPluginService;
import com.datatrees.rawdatacentral.api.internal.ThreadPoolService;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
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
        ProxyUtils.setProxyEnable(param.getTaskId(), true);
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
                WebDriver driver = null;
                try {
                    driver = SeliniumUtils.createClient(taskId, websiteName);
                    driver.get(currentUrl);
                    TimeUnit.SECONDS.sleep(5);
                    driver.findElement(By.xpath("//input[@id='u']")).sendKeys(username);
                    driver.findElement(By.xpath("//input[@id='p']")).sendKeys(password);
                    driver.findElement(By.xpath("//div[@id='go']")).click();
                    TimeUnit.SECONDS.sleep(5);
                    try {
                        currentUrl = driver.getCurrentUrl();
                    } catch (Exception e) {
                        logger.error("get current url error", e);
                    }
                    driver.switchTo().defaultContent();
                    currentUrl = driver.getCurrentUrl();
                    logger.info("登陆后currentUrl={}", currentUrl);

                    if (!StringUtils.startsWith(currentUrl, "https://w.mail.qq.com/cgi-bin/today")) {

                        WebElement new_vcode = SeliniumUtils.findElement(driver, By.id("new_vcode"));
                        String display = "none";
                        if (null != new_vcode) {
                            display = new_vcode.getCssValue("display");
                            logger.info("display : {}", display);
                        }
                        if (StringUtils.equals("block", display)) {
                            logger.info("安全验证出现了,{}", driver.getCurrentUrl());
                            driver.switchTo().frame(1);
                            moveHk(driver);
                        }

                        driver.switchTo().defaultContent();
                        WebElement element = SeliniumUtils.findElement(driver, By.xpath("//div[@class='qui-dialog-content']"));
                        if (null != element) {
                            ProcessResultUtils.saveProcessResult(processResult.fail(ErrorCode.VALIDATE_PASSWORD_FAIL));
                            return;
                        }
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

                    logger.warn("login by selinium fail,taskId={},websiteName={},endUrl={}", taskId, websiteName, currentUrl);
                    ProcessResultUtils.saveProcessResult(processResult.fail(ErrorCode.LOGIN_ERROR));
                } catch (Throwable e) {
                    logger.warn("login by selinium error,taskId={},websiteName={},endUrl={}", taskId, websiteName, currentUrl, e);
                    ProcessResultUtils.saveProcessResult(processResult.fail(ErrorCode.LOGIN_ERROR));
                } finally {
                    SeliniumUtils.closeClient(driver);
                }
            }
        });
        return new HttpResult(true).success(processResult);
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        return new HttpResult().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    public HttpResult<Object> isWrongPassword(WebDriver driver) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            WebElement element = SeliniumUtils.findElement(driver, By.xpath("//div[@class='qui-dialog-content']"));
            if (null != element) {
                return result.failure(element.getText());
            }
            return result.success();
        } catch (Exception e) {
            logger.error("check password error", e);
        }
        return result.success();

    }

    private void moveHk(WebDriver driver) throws Exception {
        try {
            WebElement bgImg = driver.findElement(By.id("bkBlock"));
            WebElement sideBar = driver.findElement(By.id("slideBlock"));
            int bg_img_with = bgImg.getSize().getWidth();
            int bg_img_height = bgImg.getSize().getHeight();
            int side_bar_with = sideBar.getSize().getWidth();
            int side_bar_left = Integer.valueOf(sideBar.getCssValue("left").replaceAll("px", ""));
            int side_bar_top = Integer.valueOf(sideBar.getCssValue("top").replaceAll("px", ""));
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
                BufferedImage image0 = ImageIO.read(new ByteArrayInputStream(img0));
                BufferedImage image2 = ImageIO.read(new ByteArrayInputStream(img2));
                int minX = side_bar_left * image0.getWidth() / bg_img_with + image2.getWidth() / 2;
                int minY = side_bar_top * image0.getHeight() / bg_img_height + image2.getHeight() / 2;
                ColorPoint point = ImageOcrUtils.ocr(img0, img1, img2, minY);
                logger.info("mix={},minY={},side_bar_left={},side_bar_top={},bg_img_height={},img0_height={},img2_height={}", minX, minY,
                        side_bar_left, side_bar_top, bg_img_height, image0.getHeight(), image2.getHeight());
                int move = point.getAbsoluteX() * bg_img_with / image0.getWidth() - side_bar_left - side_bar_with / 2;
                logger.info("move={},realWith={},bgWith={},x={}", move, image0.getWidth(), bgImg.getSize().getWidth(), point.getAbsoluteX());

                WebElement el = driver.findElement(By.id("tcaptcha_drag_thumb"));
                Actions actions = new Actions(driver);
                new Actions(driver).clickAndHold(el).perform();
                actions.moveByOffset(move, 0).click().perform();

                TimeUnit.SECONDS.sleep(5);
                String pageSource = null;
                try {
                    pageSource = driver.getPageSource();
                } catch (Exception e) {
                    logger.error("get page source error", e);
                    return;
                }
                if (pageSource.contains("拖动下方滑块完成拼图")) {
                    logger.info("拖动下方滑块完成拼图");
                    driver.findElement(By.xpath("//div[@class='tcaptcha-action-icon']")).click();
                    TimeUnit.SECONDS.sleep(10);
                    moveHk(driver);
                }

            }
        } catch (Throwable e) {
            logger.error("move side bar error", e);
        }
    }
}
