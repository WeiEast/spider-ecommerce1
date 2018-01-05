package com.datatrees.crawler.core.util;

import java.net.URL;
import java.util.Set;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeliniumUtils {

    private static final Logger logger = LoggerFactory.getLogger(SeliniumUtils.class);

    public static WebDriver createClient(Long taskId, String websiteName) throws Exception {
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
        String hubUrl = PropertiesConfiguration.getInstance().get("hub.url");
        return new RemoteWebDriver(new URL(hubUrl), capabilities);
    }

    public static String getCookieString(WebDriver driver) {
        Set<Cookie> cookies = driver.manage().getCookies();
        if (null != cookies && !cookies.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Cookie cookie : cookies) {
                sb.append(";").append(cookie.getName()).append("=").append(cookie.getValue());
            }
            return sb.substring(1);
        }
        return null;
    }

    public static void closeClient(WebDriver driver) {
        try {
            if (null != driver) {
                driver.close();
                driver.quit();
            }
        } catch (Exception e) {
            logger.error("web driver close error", e);
        }
    }

}
