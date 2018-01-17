package com.datatrees.rawdatacentral.plugin.common.qq.com.h5;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.internal.CommonPluginService;
import com.datatrees.rawdatacentral.api.internal.QRPluginService;
import com.datatrees.rawdatacentral.api.internal.ThreadPoolService;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.ProcessResultUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.QRStatus;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;
import com.datatrees.rawdatacentral.plugin.common.qq.com.h5.util.ImageOcrUtils;
import com.datatrees.rawdatacentral.plugin.common.qq.com.h5.util.ImageUtils;
import com.datatrees.rawdatacentral.plugin.common.qq.com.h5.util.domain.ColorPoint;
import com.datatrees.rawdatacentral.plugin.util.selenium.SeleniumUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QQMailPlugin implements CommonPluginService, QRPluginService {

    private static final Logger logger = LoggerFactory.getLogger(QQMailPlugin.class);

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        ProxyUtils.setProxyEnable(param.getTaskId(), false);
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
                    driver = SeleniumUtils.createClient(taskId, websiteName);
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

                        WebElement new_vcode = SeleniumUtils.findElement(driver, By.id("new_vcode"));
                        String display = "none";
                        if (null != new_vcode) {
                            display = new_vcode.getCssValue("display");
                            logger.info("display : {}", display);
                        }
                        if (StringUtils.equals("block", display)) {
                            logger.info("安全验证出现了,{}", driver.getCurrentUrl());
                            driver.switchTo().frame(1);
                            moveHk(driver, processResult.getProcessId());
                        }

                        driver.switchTo().defaultContent();
                        WebElement element = SeleniumUtils.findElement(driver, By.xpath("//div[@class='qui-dialog-content']"));
                        if (null != element) {
                            ProcessResultUtils.saveProcessResult(processResult.fail(ErrorCode.VALIDATE_PASSWORD_FAIL));
                            return;
                        }
                    }

                    if (StringUtils.startsWith(currentUrl, "https://w.mail.qq.com/cgi-bin/today")) {
                        String cookieString = SeleniumUtils.getCookieString(driver);
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
                    SeleniumUtils.closeClient(driver);
                }
            }
        });
        return new HttpResult(true).success(processResult);
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        return new HttpResult().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private void moveHk(WebDriver driver, Long processId) throws Exception {
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
                List<Integer> list = new ArrayList<>();
                int left = move;
                //if (RedisUtils.incr("move.side.bar.count." + processId) <= 2) {
                //    left += 30;
                //}
                actions.moveByOffset(left, 0).click().perform();

                TimeUnit.SECONDS.sleep(2);
                String pageSource = null;
                try {
                    pageSource = driver.getPageSource();
                } catch (Exception e) {
                    logger.error("get page source error,will switch to default content");
                    driver.switchTo().defaultContent();
                    pageSource = driver.getPageSource();
                }
                if (pageSource.contains("拖动下方滑块完成拼图")) {
                    logger.info("拖动下方滑块完成拼图");
                    driver.findElement(By.xpath("//div[@class='tcaptcha-action-icon']")).click();
                    TimeUnit.SECONDS.sleep(10);
                    moveHk(driver, processId);
                }

            }
        } catch (Throwable e) {
            logger.error("move side bar error", e);
        }
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
                    String currentUrl = "https://mail.qq.com/cgi-bin/loginpage?lang=cn";
                    RemoteWebDriver driver = null;
                    try {
                        driver = SeleniumUtils.createClient(taskId, websiteName);
                        driver.get(currentUrl);
                        TimeUnit.SECONDS.sleep(2);
                        driver.switchTo().frame("login_frame");
                        driver.findElement(By.id("switcher_qlogin")).click();
                        byte[] inData = driver.getScreenshotAs(OutputType.BYTES);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        ImageUtils.crop(new ByteArrayInputStream(inData), out, 96, 123, 127, 127, false);
                        ProcessResultUtils.saveProcessResult(processResult.success(Base64.getEncoder().encodeToString(out.toByteArray())));
                        TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, QRStatus.WAITING);
                        logger.info("refresh qr code success,taskId={},websiteName={}", taskId, websiteName);

                        long endTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2);
                        currentUrl = driver.getCurrentUrl();
                        while (!isLoginSuccess(currentUrl) && System.currentTimeMillis() <= endTime) {
                            TimeUnit.SECONDS.sleep(3);
                            currentUrl = driver.getCurrentUrl();
                        }
                        currentUrl = driver.getCurrentUrl();
                        String currentLoginProcessId = TaskUtils.getTaskShare(taskId, AttributeKey.CURRENT_LOGIN_PROCESS_ID);
                        if (isLoginSuccess(currentUrl) && StringUtils.equals(currentLoginProcessId, processResult.getProcessId().toString())) {
                            currentUrl = "http://w.mail.qq.com";
                            driver.get(currentUrl);
                            TimeUnit.SECONDS.sleep(3);
                            String cookieString = SeleniumUtils.getCookieString(driver);
                            LoginMessage loginMessage = new LoginMessage();
                            loginMessage.setTaskId(taskId);
                            loginMessage.setWebsiteName(websiteName);
                            loginMessage.setAccountNo(null);
                            loginMessage.setEndUrl(currentUrl);
                            loginMessage.setCookie(cookieString);
                            logger.info("登陆成功,taskId={},websiteName={},endUrl={}", taskId, websiteName, currentUrl);
                            BeanFactoryUtils.getBean(CommonPluginApi.class).sendLoginSuccessMsg(loginMessage);
                            TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, QRStatus.SUCCESS);
                            return;
                        }
                        if (!StringUtils.equals(currentLoginProcessId, processResult.getProcessId().toString())) {
                            //图片验证码已经刷新,这个线程可以关闭了;
                            logger.error("current login process will close,because new login process start,taskId={},websiteName={}", taskId,
                                    websiteName);
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

    private boolean isLoginSuccess(String url) {
        return StringUtils.startsWith(url, "https://mail.qq.com/cgi-bin/frame_html?sid=");
    }
}
