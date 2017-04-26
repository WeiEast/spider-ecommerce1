/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.bobj.selenium;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.rawdatacentral.bobj.selenium.keys.VKMapping;
import com.datatrees.rawdatacentral.bobj.selenium.keys.VirtualKeyBoard;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年11月11日 下午5:52:19
 */
public class SeleniumTest {
    private Logger logger = LoggerFactory.getLogger(SeleniumTest.class);


    @Test
    public void StartFireFox() throws InterruptedException {
        System.out.println("start firefox browser...");
        FirefoxProfile profile = new FirefoxProfile();
        // profile.setPreference("services.sync.prefs.sync.browser.startup.homepage", false);
        profile.setPreference("startup.homepage_welcome_url.additional", "http://www.baidu.com/");
        // WebDriver driver = new EventFiringWebDriver(new FirefoxDriver(profile)).register(new
        // EventListener());
        WebDriver driver = new FirefoxDriver(profile);
        logger.info("current url:" + driver.getCurrentUrl());
        driver.manage().window().maximize();
        // WebDriver.Navigation navigation = driver.navigate();
        // driver.get("http://www.baidu.com/");
        WebElement mobile = driver.findElement(By.id("kw"));
        System.out.println(mobile.getLocation());
        mobile.clear();
        mobile.sendKeys("15068820568");
        System.out.println(driver.manage().getCookies());
        // WebElement waitButton = driver.findElement(By.id("su"));
        // waitButton.click();
        Map<String, String> map = new HashMap<String, String>();
        boolean flag = new WebDriverWait(driver, 100).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                map.put("value", driver.findElement(By.id("kw")).getAttribute("value"));
                logger.info("current value:" + driver.findElement(By.id("kw")).getAttribute("value"));
                return !driver.getCurrentUrl().equals("https://www.baidu.com/");
            }
        });
        if (flag) {
            logger.info("last input:" + map.get("value") + ",current url:" + driver.getCurrentUrl());
            ((JavascriptExecutor) driver).executeScript("alert('执行成功，你的关键字是:" + map.get("value") + ",当前url:" + driver.getCurrentUrl()
                    + "；本窗口将于3秒后关闭')");
        } else {
            ((JavascriptExecutor) driver).executeScript("alert('执行失败，本窗口将于3秒后关闭')");
        }
        Thread.sleep(5000);
        driver.quit();
    }


    @Test
    public void StartIE() throws InterruptedException {
        System.out.println("start IE browser...");
        String classPath = ClassLoader.getSystemClassLoader().getResource("").getPath();
        System.setProperty("webdriver.ie.driver", classPath + "IEDriverServer.exe");
        WebDriver driver = new InternetExplorerDriver();
        driver.get("http://www.baidu.com/");
        logger.info("current url:" + driver.getCurrentUrl());
        driver.manage().window().maximize();
        // WebDriver.Navigation navigation = driver.navigate();
        // driver.get("http://www.baidu.com/");
        // WebElement mobile = driver.findElement(By.id("kw"));
        // mobile.clear();
        // mobile.sendKeys("15068820568");
        Actions action = new Actions(driver);
        // action.sendKeys(Keys.valueOf("123424"));
        // action.sendKeys(Keys.SPACE);// 模拟按下并释放空格键
        action.sendKeys("6222081211004860168");

        action.sendKeys(Keys.SPACE);// 模拟按下并释放空格键
        action.sendKeys(Keys.SPACE);// 模拟按下并释放空格键
        action.sendKeys(Keys.SPACE);// 模拟按下并释放空格键
        action.perform();


        System.out.println(driver.manage().getCookies());
        // WebElement waitButton = driver.findElement(By.id("su"));
        // waitButton.click();
        Map<String, String> map = new HashMap<String, String>();
        boolean flag = new WebDriverWait(driver, 100).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                map.put("value", driver.findElement(By.id("kw")).getAttribute("value"));
                logger.info("current value:" + driver.findElement(By.id("kw")).getAttribute("value"));
                return !driver.getCurrentUrl().equals("https://www.baidu.com/");
            }
        });
        if (flag) {
            logger.info("last input:" + map.get("value") + ",current url:" + driver.getCurrentUrl());
            ((JavascriptExecutor) driver).executeScript("alert('执行成功，你的关键字是:" + map.get("value") + ",当前url:" + driver.getCurrentUrl()
                    + "；本窗口将于3秒后关闭')");
        } else {
            ((JavascriptExecutor) driver).executeScript("alert('执行失败，本窗口将于3秒后关闭')");
        }
        Thread.sleep(5000);
        driver.quit();
    }



    @Test
    public void StartICBC() throws Exception {
        System.out.println("start IE browser...");
        String classPath = ClassLoader.getSystemClassLoader().getResource("").getPath();
        System.setProperty("webdriver.ie.driver", classPath + "IEDriverServer.exe");
        WebDriver driver = new InternetExplorerDriver();
        logger.info("current url:" + driver.getCurrentUrl());
        driver.manage().window().maximize();
        driver.get("https://mybank.icbc.com.cn/icbc/newperbank/main/login.jsp");

        WebElement body = driver.findElement(By.tagName("body"));
        body.sendKeys(Keys.chord(Keys.CONTROL + "t"));
        driver.get("https://baidu.com");

        logger.info("cookie :" + driver.manage().getCookies());

        Actions action = new Actions(driver);
        // action.sendKeys(Keys.valueOf("123424"));
        // action.sendKeys(Keys.SPACE);// 模拟按下并释放空格键
        WebElement element = driver.findElement(By.id("logonCardNum"));
        action.sendKeys(element, "6222081211004860168");
        action.sendKeys(element, Keys.ENTER);
        action.perform();


        Robot robot = new Robot();
        // robot.setAutoWaitForIdle(false);
        // // Keyboard keyboard = new Keyboard(robot);
        // robot.keyPress(KeyEvent.VK_ENTER);
        // robot.keyRelease(KeyEvent.VK_ENTER);
        //
        // Keyboard keyboard = new DesktopKeyboard();
        // keyboard.type("abcf");


        // keyboard.type("00");
        // robot.keyPress(KeyEvent.VK_8);
        // robot.keyRelease(KeyEvent.VK_8);
        //
        // robot.keyPress(KeyEvent.VK_8);
        // robot.keyRelease(KeyEvent.VK_8);
        //
        // robot.keyPress(KeyEvent.VK_8);
        // robot.keyRelease(KeyEvent.VK_8);
        // robot.mouseMove(1360, 414);
        // robot.mousePress(InputEvent.BUTTON1_MASK);
        // robot.mouseRelease(InputEvent.BUTTON1_MASK);
        // Thread.sleep(1000);
        // robot.mouseMove(1360, 414);
        // robot.mousePress(InputEvent.BUTTON1_MASK);
        // Thread.sleep(1000);
        // robot.mouseRelease(InputEvent.BUTTON1_MASK);
        // Thread.sleep(5000);
        // robot = new Robot();
        // keyboard = new Keyboard(robot);
        // keyboard.type("3332");
        // Thread.sleep(40000);

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        logger.info("Dimension:" + d);

        element = driver.findElement(By.className("input-wrapper-pwd"));
        // element.clear();
        // element.sendKeys("ddd");
        logger.info("element Location:" + element.getLocation());
        Point point = element.getLocation();
        robot.mouseMove(point.x + 10, point.y + 60);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(1000);

        String s = "helloworld";
        for (int i = 0; i < s.length(); i++) {
            VirtualKeyBoard.KeyDown(VKMapping.toScanCode("" + s.charAt(i)));
            VirtualKeyBoard.KeyUp(VKMapping.toScanCode("" + s.charAt(i)));
        }

        // // action.sendKeys(Keys.TAB);
        // // action.perform();
        // // robot = new Robot();333233
        // // keyboard = new Keyboard(robot);
        // robot.keyPress(KeyEvent.VK_7);
        // robot.keyRelease(KeyEvent.VK_7);
        // robot.delay(1000);


        element = driver.findElement(By.className("input-wrapper-vcode"));
        logger.info("element Location:" + element.getLocation());
        point = element.getLocation();
        robot.mouseMove(point.x + 10, point.y + 60);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(1000);

        s = "1234";
        for (int i = 0; i < s.length(); i++) {
            VirtualKeyBoard.KeyDown(VKMapping.toScanCode("" + s.charAt(i)));
            VirtualKeyBoard.KeyUp(VKMapping.toScanCode("" + s.charAt(i)));
        }
        robot.delay(2000);



        // robot.keyPress(KeyEvent.VK_ENTER);
        // robot.keyRelease(KeyEvent.VK_ENTER);
        // Thread.sleep(300);
        //
        // // robot.keyPress(KeyEvent.VK_ENTER);
        // // robot.keyRelease(KeyEvent.VK_ENTER);
        // // robot.keyPress(KeyEvent.VK_ENTER);
        // // robot.keyRelease(KeyEvent.VK_ENTER);
        //

        // keyboard.type("1111");
        // Thread.sleep(100);

        // robot.keyPress(KeyEvent.VK_0);
        // robot.keyRelease(KeyEvent.VK_0);
        // keyboard.type("9999");
        robot.delay(5000);



    }

    @Test
    public void testCookie() throws InterruptedException {
        System.out.println("start IE browser...");
        String classPath = ClassLoader.getSystemClassLoader().getResource("").getPath();
        System.setProperty("webdriver.ie.driver", classPath + "IEDriverServer.exe");
        WebDriver driver = new InternetExplorerDriver();
        logger.info("driver1 init cookie:" + driver.manage().getCookies());
        driver.get("http://www.baidu.com/");
        logger.info("driver1 current url:" + driver.getCurrentUrl());
        driver.manage().window().maximize();
        logger.info("driver1 open page cookie:" + driver.manage().getCookies());

        WebDriver driver2 = new InternetExplorerDriver();
        logger.info("driver2 init cookie:" + driver2.manage().getCookies());
        driver2.get("http://blog.csdn.net/wanghantong/article/details/28893493");
        logger.info("driver2 current url:" + driver2.getCurrentUrl());
        driver2.manage().window().maximize();
        logger.info("driver2 open page cookie:" + driver2.manage().getCookies());
        Thread.sleep(1000);
        driver.quit();
        logger.info("driver1 quit cookie:" + driver.manage().getCookies());
        logger.info("driver2 quit cookie:" + driver2.manage().getCookies());

        Thread.sleep(5000);
    }



    @Test
    public void testCurrency() throws InterruptedException {
        System.out.println("start IE browser...");
        String classPath = ClassLoader.getSystemClassLoader().getResource("").getPath();
        System.setProperty("webdriver.ie.driver", classPath + "IEDriverServer.exe");
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 5; i++) {
            int j = i;
            service.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    String proxyIpAndPort = "localhost:808" + j;
                    DesiredCapabilities cap = new DesiredCapabilities();
                    org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
                    proxy.setHttpProxy(proxyIpAndPort).setFtpProxy(proxyIpAndPort).setSslProxy(proxyIpAndPort);
                    cap.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
                    cap.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
                    System.setProperty("http.nonProxyHosts", "localhost");
                    cap.setCapability(CapabilityType.PROXY, proxy);
                    WebDriver driver = new InternetExplorerDriver(cap);
                    logger.info("driver1 init cookie:" + driver.manage().getCookies());
                    driver.get("    https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&ch=&tn=baiduerr&bar=&wd=" + j);
                    logger.info("driver1 current url:" + driver.getCurrentUrl());
                    driver.manage().window().maximize();
                    logger.info("driver1 open page cookie:" + driver.manage().getCookies());
                    return null;
                }
            });
        }
        Thread.sleep(500000);
    }



    @Test
    public void StartAli() throws InterruptedException {
        System.out.println("start firefox browser...");
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("general.useragent.override",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
        WebDriver driver = new FirefoxDriver(profile);
        logger.info("current url:" + driver.getCurrentUrl());
        driver.manage().window().maximize();
        driver.get("https://auth.alipay.com/login/index.htm?loginScene=7&goto=https://auth.alipay.com/login/taobao_trust_login.htm?target=https://buyertrade.taobao.com/trade/itemlist/list_bought_items.htm");
        WebElement user = driver.findElement(By.id("J-input-user"));
        System.out.println(user.getLocation());
        user.clear();
        user.sendKeys("593237554@qq.com");
        Thread.sleep(2000);
        // password_input
        WebElement password = null;
        try {
            password = driver.findElement(By.id("password_rsainput"));
        } catch (Exception e) {
            password = driver.findElement(By.id("password_input"));
        }
        System.out.println(password.getLocation());
        password.clear();
        password.sendKeys("ABc2730834");
        System.out.println(driver.manage().getCookies());
        WebElement subButton = driver.findElement(By.id("J-login-btn"));
        subButton.click();
        Thread.sleep(50000);
        driver.quit();
    }

}
