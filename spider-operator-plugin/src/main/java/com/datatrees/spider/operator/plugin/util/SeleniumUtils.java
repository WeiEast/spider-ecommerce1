/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.operator.plugin.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.spider.share.common.http.ProxyUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumUtils {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumUtils.class);

    public static RemoteWebDriver createClient(Long taskId, String websiteName) throws Exception {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        if (ProxyUtils.getProxyEnable(taskId)) {
            com.treefinance.proxy.domain.Proxy proxy = ProxyUtils.getProxy(taskId, websiteName);
            if (null != proxy) {
                String proxyStr = proxy.getIp() + ":" + proxy.getPort();
                org.openqa.selenium.Proxy p = new org.openqa.selenium.Proxy();
                p.setSslProxy(proxyStr);
                p.setHttpProxy(proxyStr);
                capabilities.setCapability(CapabilityType.PROXY, p);
                logger.info("will user proxy:{}", proxyStr);
            }
        }
        String hubUrl = PropertiesConfiguration.getInstance().get("selenium.hub.url");
        return new RemoteWebDriver(new URL(hubUrl), capabilities);
    }

    public static String getCookieString(WebDriver driver) {
        Set<Cookie> cookies = driver.manage().getCookies();
        if (null != cookies && !cookies.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Cookie cookie : cookies) {
                sb.append("; ").append(cookie.getName()).append("=").append(cookie.getValue());
            }
            return sb.substring(2);
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
            logger.error("web driver close error,{}", e.getMessage());
        }
    }

    public static WebElement findElement(WebDriver driver, By by) {
        try {
            return driver.findElement(by);
        } catch (Exception e) {
            logger.error("selenium find element error,by:{},{}", by.toString(), e.getMessage());
            return null;
        }
    }

    /**
     * 从BasicClientCookie转HttpCookie
     * @param from
     * @return
     */
    public static com.datatrees.spider.share.domain.http.Cookie toHttpCookie(Cookie from) {
        String domain = from.getDomain();
        if (domain.startsWith(".")) {
            domain = domain.substring(1);
        }
        com.datatrees.spider.share.domain.http.Cookie to = new com.datatrees.spider.share.domain.http.Cookie();
        to.setDomain(domain);
        to.setPath(from.getPath());
        to.setName(from.getName());
        to.setValue(from.getValue());
        to.setSecure(from.isSecure());
        //to.setVersion(from.getVersion());
        to.setExpiryDate(from.getExpiry());
        //if (from.containsAttribute(HttpConstant.DOMAIN)) {
        //    to.getAttribs().put(HttpConstant.DOMAIN, domain);
        //}
        //if (from.containsAttribute(HttpConstant.PATH)) {
        //    to.getAttribs().put(HttpConstant.PATH, from.getAttribute(HttpConstant.PATH));
        //}
        //if (from.containsAttribute(HttpConstant.EXPIRES)) {
        //    to.getAttribs().put(HttpConstant.EXPIRES, from.getAttribute(HttpConstant.EXPIRES));
        //}
        //if (from.containsAttribute(HttpConstant.HTTP_ONLY)) {
        //    to.getAttribs().put(HttpConstant.HTTP_ONLY, from.getAttribute(HttpConstant.HTTP_ONLY));
        //}
        return to;
    }

    public static List<com.datatrees.spider.share.domain.http.Cookie> getCookies(RemoteWebDriver driver) {
        List<com.datatrees.spider.share.domain.http.Cookie> list = new ArrayList<>();
        Set<Cookie> cookies = driver.manage().getCookies();
        if (null == cookies || cookies.isEmpty()) {
            return list;
        }
        for (Cookie cookie : cookies) {
            list.add(toHttpCookie(cookie));
        }
        return list;
    }
}
