package com.datatrees.rawdatacentral.service.dubbo.mail.qq;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.share.service.MessageService;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.service.RedisService;
import com.datatrees.spider.share.service.utils.ProxyUtils;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.directive.DirectiveRedisCode;
import com.datatrees.spider.share.domain.directive.DirectiveType;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.TaskStatusEnum;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.domain.TopicTag;
import com.datatrees.spider.share.domain.directive.DirectiveResult;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPluginForQQ implements Runnable {

    private static final Logger         logger         = LoggerFactory.getLogger(LoginPluginForQQ.class);

    private              Long           taskId;

    private              String         websiteName;

    private              String         hubUrl;

    private              MonitorService monitorService = BeanFactoryUtils.getBean(MonitorService.class);

    private              RedisService   redisService   = BeanFactoryUtils.getBean(RedisService.class);

    private              MessageService messageService = BeanFactoryUtils.getBean(MessageService.class);

    public LoginPluginForQQ(Long taskId, String websiteName, String hubUrl) {
        this.taskId = taskId;
        this.websiteName = websiteName;
        this.hubUrl = hubUrl;
    }

    @Override
    public void run() {
        try {
            logger.info("start run Login plugin!taskId={},websiteName={}", taskId, websiteName);
            monitorService.sendTaskLog(taskId, "模拟登录-->启动-->成功");

            final String groupKey = DirectiveResult.getGroupKey(DirectiveType.PLUGIN_LOGIN, taskId);
            long maxInterval = TimeUnit.MINUTES.toMillis(15) + System.currentTimeMillis();

            while (System.currentTimeMillis() < maxInterval) {
                DirectiveResult<Map<String, Object>> directive = redisService.getNextDirectiveResult(groupKey, 500, TimeUnit.MILLISECONDS);
                if (null == directive) {
                    TimeUnit.MILLISECONDS.sleep(500);
                    continue;
                }
                String directiveType = directive.getStatus();
                String directiveId = directive.getDirectiveId();
                switch (directiveType) {
                    case DirectiveRedisCode.CANCEL:
                        logger.info("task is cancel,taskId={},websiteName={},directive={}", taskId, websiteName, JSON.toJSONString(directive));
                        return;
                    case DirectiveRedisCode.START_LOGIN:
                        HttpResult<Map<String, String>> result = login(directive);
                        RedisUtils.set(RedisKeyPrefixEnum.LOGIN_RESULT.getRedisKey(directiveId), JSON.toJSONString(result),
                                RedisKeyPrefixEnum.LOGIN_RESULT.toSeconds());
                        if (result.getStatus()) {
                            return;
                        }
                        continue;
                    default:
                        logger.info("un support directive,taskId={},websiteName={},directive={}", taskId, websiteName, JSON.toJSONString(directive));
                        continue;
                }
            }

        } catch (Throwable e) {
            logger.error("login for qq error,taskId={},websiteName={}", taskId, websiteName, e);
        }
    }

    private HttpResult<Map<String, String>> login(DirectiveResult<Map<String, Object>> directive) throws MalformedURLException {
        HttpResult<Map<String, String>> result = new HttpResult<>();

        String directiveId = directive.getDirectiveId();
        Map<String, Object> directiveData = directive.getData();

        Map<String, String> retuanData = new HashMap<>();
        retuanData.put(AttributeKey.DIRECTIVE_ID, directiveId);
        result.setData(retuanData);
        try {

            DesiredCapabilities capabilities = DesiredCapabilities.firefox();
            com.treefinance.proxy.domain.Proxy proxy = ProxyUtils.getProxy(taskId, websiteName);
            if (null != proxy) {
                String proxyStr = proxy.getIp() + ":" + proxy.getPort();
                org.openqa.selenium.Proxy p = new org.openqa.selenium.Proxy();
                p.setSslProxy(proxyStr);
                p.setHttpProxy(proxyStr);
                capabilities.setCapability(CapabilityType.PROXY, proxy);
                logger.info("will user proxy:{}", proxyStr);
            }
            WebDriver driver = new RemoteWebDriver(new URL(hubUrl), capabilities);
            String username = directiveData.getOrDefault(AttributeKey.USERNAME, "").toString();
            String password = directiveData.getOrDefault(AttributeKey.PASSWORD, "").toString();
            driver.get("http://w.mail.qq.com");
            TimeUnit.SECONDS.sleep(3);
            driver.findElement(By.xpath("//input[@id='u']")).sendKeys(username);
            driver.findElement(By.xpath("//input[@id='p']")).sendKeys(password);
            driver.findElement(By.xpath("//div[@id='go']")).click();
            TimeUnit.SECONDS.sleep(3);
            String currentUrl = driver.getCurrentUrl();
            logger.info("登陆后currentUrl={}", currentUrl);
            if (StringUtils.startsWith(currentUrl, "https://w.mail.qq.com/cgi-bin/today")) {
                Set<Cookie> cookies = driver.manage().getCookies();
                String cookieString = null;
                if (null != cookies && !cookies.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Cookie cookie : cookies) {
                        sb.append(";").append(cookie.getName()).append("=").append(cookie.getValue());
                    }
                    cookieString = sb.substring(1);
                }
                Map<String, String> loginData = new HashMap<>();
                loginData.put(AttributeKey.END_URL, currentUrl);
                loginData.put(AttributeKey.TASK_ID, taskId.toString());
                loginData.put(AttributeKey.WEBSITE_NAME, websiteName);
                loginData.put(AttributeKey.ACCOUNT_NO, username);
                loginData.put(AttributeKey.COOKIE, cookieString);
                logger.info("登陆成功,taskId={},websiteName={},cookieString={},endUrl={}", taskId, websiteName, cookieString, currentUrl);
                messageService.sendMessage(TopicEnum.RAWDATA_INPUT.getCode(), TopicTag.LOGIN_INFO.getTag(), loginData);

                retuanData.put(AttributeKey.STATUS, TaskStatusEnum.LOGIN_SUCCESS.getCode());
                return result.success(retuanData);
            }
            logger.warn("login by selinium fail,taskId={},websiteName={},endUrl={}", taskId, websiteName, currentUrl);
            retuanData.put(AttributeKey.STATUS, TaskStatusEnum.LOGIN_FAILED.getCode());

            return result.failure(ErrorCode.LOGIN_ERROR);
        } catch (Throwable e) {
            logger.error("login by selinium error,taskId={},websiteName={}", taskId, websiteName, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

}
