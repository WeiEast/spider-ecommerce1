/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.crawler.plugin.alipay.ts;

import com.treefinance.crawler.framework.util.CookieFormater;
import com.treefinance.crawler.support.selenium.SeleniumHelper;
import com.treefinance.crawler.support.selenium.SeleniumOperation;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 交易记录页的搜索翻页相关操作
 * 
 * @author Jerry
 * @since 00:20 30/11/2017
 */
public class SearchPageAction extends SeleniumOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchPageAction.class);
    /**
     * 超时时间，单位：秒
     */
    private static final long TIMEOUT = 10;
    /**
     * 为了正确设置cookie，需要预加载一个资源，避免域冲突
     */
    private static final String PRE_LOAD_ALIPAY_URL = "https://www.alipay.com/favicon.ico";
    private static final String PRE_LOAD_TAOBAO_URL = "https://www.taobao.com/favicon.ico";
    private static final String STANDARD_URL = "https://consumeprod.alipay.com/record/standard.htm";
    private static final String ADVANCED_URL = "https://consumeprod.alipay.com/record/advanced.htm";
    private static final String MODEL_SWITCH_URL = "https://consumeprod.alipay.com/record/switchVersion.htm";
    private static final String FORM_ID = "topSearchForm";
    private static final boolean FORCE_ALIPAY = true;
    private final boolean loginByTaobao;
    private final String preLoadUrl;
    private final String domain;
    private volatile WebElement searchForm = null;
    private volatile PageState pageState = PageState.PREPARE;
    private volatile int pageNum = 1;
    private volatile boolean hasNext = false;

    public SearchPageAction(WebDriver webDriver, boolean loginByTaobao) {
        super(webDriver);
        this.loginByTaobao = !FORCE_ALIPAY && loginByTaobao;
        if (this.loginByTaobao) {
            preLoadUrl = PRE_LOAD_TAOBAO_URL;
            domain = "taobao.com";
        } else {
            preLoadUrl = PRE_LOAD_ALIPAY_URL;
            domain = "alipay.com";
        }
        LOGGER.info("preLoadUrl: {}", preLoadUrl);
    }

    public static String getSeedUrl() {
        return ADVANCED_URL;
    }

    private void mark(PageState state) {
        this.pageState = state;
    }

    /**
     * 第一次初始化，加载交易记录首页
     */
    public boolean initial(String cookie) {
        setCookies(cookie);

        return loadPage();
    }

    private boolean loadPage() {
        mark(PageState.PREPARE);

        navigateTo(getSeedUrl() + (loginByTaobao ? "?sign_from=3000" : ""));

        awaitPageRefresh(false);

        boolean flag = isNormal();
        if (flag) {
            LOGGER.info(">>>>>> 成功加载高级版查询页");
        }

        return flag;
    }

    private boolean isFormReady() {
        return searchForm != null;
    }

    /**
     * 设置cookie
     */
    private void setCookies(String cookies) {
        LOGGER.info("Cookies >>> {}", cookies);

        if (StringUtils.isBlank(cookies)) {
            throw new IllegalArgumentException("未正常设置cookies，异常退出搜索器.");
        }

        LOGGER.info(">>>>>> 开始设置cookies");

        getWebDriver().get(preLoadUrl);
        // 清空原有的cookies，避免cookie干扰
        WebDriver.Options manage = manage();
        manage.deleteAllCookies();
        // 添加最新的cookies
        Map<String, String> cookieMap = CookieFormater.INSTANCE.parserCookieToMap(cookies);
        for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
            manage.addCookie(new Cookie(entry.getKey(), entry.getValue(), domain, "/", null));
        }

        LOGGER.info(">>>>>> 完成cookies设置");
    }

    /**
     * 如果是标准版，就切换到高级版
     */
    public void switchAdvancedIfRedirect() {
        LOGGER.info(">>>>>> 检测是否是高级版查询页");
        if (getCurrentUrl().startsWith(STANDARD_URL)) {
            LOGGER.info(">>>>>> 检测到标准版查询页，开始切换到高级版");
            mark(PageState.PREPARE);
            navigateTo(MODEL_SWITCH_URL);
            awaitPageRefresh(false);
            if (isNormal()) {
                LOGGER.info(">>>>>> 成功切换到高级版查询页");
            }
        }
    }

    /**
     * 选择交易开始时间
     * 
     * @param beginDate 交易开始时间
     */
    public void selectBeginDate(String beginDate) {
        LOGGER.info(">>>>>> 选择交易开始时间：{}", beginDate);

        // 判断时间下拉框是否加载完成
        WebElement selector = awaitElementLocated(By.cssSelector("div[data-widget-cid='widget-5'] li[data-value='customDate']"), TIMEOUT);
        // 查找交易时间选择按钮
        WebElement element = searchForm.findElement(By.cssSelector("a.ui-select-trigger[seed='JDatetimeSelect-link']"));
        // 点击按钮并显示下拉选择框
        element.click();
        // 选择自定义时间选项
        selector.click();
        // 通过脚本修改开始时间
        eval("document.getElementById('beginDate').value=arguments[0];", beginDate);
    }

    /**
     * 选择交易类型
     * 
     * @param type 交易类型
     */
    public void selectTradeType(String type) {
        LOGGER.info(">>>>>> 选择交易类型：{}", type);

        // 判断交易类型下拉框是否加载完成
        WebElement selector = awaitElementLocated(By.cssSelector("div[data-widget-cid='widget-2'] li[data-value='" + type + "']"), TIMEOUT);
        // 查找交易类型选择按钮
        WebElement element = searchForm.findElement(By.id("tradeType"));
        // 点击按钮并显示下拉选择框
        element.click();
        // 选择对应的交易类型
        selector.click();
    }

    /**
     * submit form and wait page reloaded
     */
    public void search() {
        if (isTerminated()) {
            throw new IllegalStateException("不正确页面，搜索失败！");
        }

        this.pageNum = 1;

        LOGGER.info(">>>>>> 提交表单并搜索");
        mark(PageState.PREPARE);

        searchForm.submit();

        awaitPageRefresh(true);

        if (isNormal()) {
            LOGGER.info(">>>>>> 成功提交表单并完成搜索");
        }
    }

    /**
     * click to switch to next page.
     */
    public boolean nextPage() {
        if (isTerminated()) {
            throw new IllegalStateException("不正确页面，翻页失败！");
        }

        mark(PageState.PREPARE);
        hasNext = false;
        // 获取下一页的按钮
        WebElement nextPageEl;
        try {
            nextPageEl = findNextPageButton(); // findElementByClass("page-next");
            hasNext = true;
        } catch (NoSuchElementException e) {
            mark(PageState.LOADED);
            return hasNext;
        }

        try {
            String nextPageNum = nextPageEl.getAttribute("pagenum");
            this.pageNum = Integer.valueOf(nextPageNum);
        } catch (Exception e) {
            this.pageNum++;
            throw e;
        }
        LOGGER.info(">>>>>> 切换到下一页: {}", this.pageNum);
        // 点击下一页的按钮
        nextPageEl.click();
        // 等待下一页元素加载
        awaitPageRefresh(true);

        if (isNormal()) {
            LOGGER.info(">>>>>> 成功切换到下一页");
        }

        return true;
    }

    private WebElement findNextPageButton() {
        List<WebElement> elements = getWebDriver().findElements(By.cssSelector("div.amount-top div.page-link > a.page-trigger"));
        for (WebElement element : elements) {
            if (element.getText().contains("下一页")) {
                return element;
            }
        }
        throw new NoSuchElementException("Can not find next-page element!");
    }

    public boolean pageLoaded() {
        mark(PageState.PREPARE);
        awaitPageRefresh(false);

        return isNormal();
    }

    /**
     * 等待search form加载完成，并且页面window对象中生成json_ua值
     */
    private WebElement awaitFormReady() {
        awaitPageRefresh(false);

        return isNormal() ? searchForm : null;
    }

    /**
     * 等待页面刷新后重新加载完成
     */
    private void awaitPageRefresh(boolean strict) {
        if (awaitPageLoading()) {
            awaitPageLoaded(strict);
        }
    }

    /**
     * 等待新的页面开始加载
     */
    private boolean awaitPageLoading() {
        Boolean staleness;
        try {
            if (searchForm != null) {
                staleness = awaitElementStaleness(this.searchForm, TIMEOUT);
            } else {
                staleness = Boolean.TRUE;
            }
        } catch (TimeoutException e) {
            LOGGER.warn("Error awaiting for page refreshed", e);
            staleness = Boolean.FALSE;
        } catch (NoSuchElementException e) {
            LOGGER.warn("Can not find element when awaiting for page refreshed", e);
            staleness = Boolean.TRUE;
        }

        if (Boolean.TRUE.equals(staleness)) {
            return true;
        } else {
            this.searchForm = null;
            return false;
        }
    }

    private void awaitPageLoaded(boolean strict) {
        awaitPageLoaded(strict, true);
    }

    private void awaitPageLoaded(boolean strict, boolean checkVersion) {
        this.searchForm = null;

        try {
            waitUtil((ExpectedCondition<Boolean>)driver -> {
                String currentUrl = driver.getCurrentUrl();
                if (currentUrl == null) {
                    return false;
                } else if (checkVersion && currentUrl.startsWith(STANDARD_URL)) {
                    throw new IncorrectVersionException("检测到标准版查询页");
                } else if (!currentUrl.startsWith(ADVANCED_URL)) {
                    if (strict) {
                        throw new IncorrectPageException("Current page has switched to the incorrect url - " + currentUrl);
                    }
                    return false;
                }

                WebElement form = driver.findElement(By.id(FORM_ID));
                if (form == null) {
                    return false;
                }

                String jsonUa = getJsonUa(driver);
                LOGGER.info("Json_ua : {}", jsonUa);

                return StringUtils.isNotEmpty(jsonUa);
            }, TIMEOUT);

            this.searchForm = awaitElementLocated(By.id(FORM_ID), TIMEOUT);

            if (isFormReady()) {
                mark(PageState.LOADED);
            }
        } catch (IncorrectVersionException e) {
            LOGGER.warn(">>>>>> {}", e.getMessage());
            LOGGER.info(">>>>>> 开始切换到高级版");
            navigateTo(MODEL_SWITCH_URL);

            awaitPageLoaded(strict, false);
        } catch (IncorrectPageException e) {
            LOGGER.warn(e.getMessage());
        } catch (TimeoutException e) {
            LOGGER.warn("Locating search form in page was timeout.", e);
        }
    }

    private String getJsonUa(WebDriver driver) {
        String jsonUa = (String)SeleniumHelper.evalScript(driver, "return window.json_ua");

        LOGGER.debug("Json_ua : {}", jsonUa);

        return jsonUa;
    }

    /**
     * set new page number
     */
    public void setPageNum(int pageNum) {
        eval("document.getElementById('query-pageNum').value=arguments[0];", pageNum);
    }

    /**
     * 检查滑块限制页
     */
    public String checkLimitPage() {
        LOGGER.info(">>>>>> 检测是否是访问限制页");
        String pageContent = getPageSource();

        LOGGER.debug("页面内容 >>> \n{}", pageContent);

        if (isLimitPage(getCurrentUrl(), pageContent)) {
            LOGGER.info(">>>>>> 检测到访问限制页，开始等待滑块加载");

            WebElement element = awaitElementLocated(By.id("nc_1_n1z"), TIMEOUT);

            LOGGER.info(">>>>>> 开始移动滑块");

            actions().dragAndDropBy(element, 258, 0).perform();

            LOGGER.info(">>>>>> 等待网页跳转");

            WebElement formEl = awaitFormReady();
            if (formEl != null) {
                pageContent = getPageSource();
                LOGGER.info(">>>>>> 限制页跳转成功");
            } else {
                LOGGER.info(">>>>>> 限制页跳转失败");
            }
        }

        return pageContent;
    }

    private boolean isLimitPage(String url, String pageContent) {
        return (url != null && url.contains("checkcodev3.php")) || (pageContent.contains("亲，访问受限了") && pageContent.contains("nc-verify-form"));
    }

    public String getCurrentUrl() {
        return getWebDriver().getCurrentUrl();
    }

    public String getPageSource() {
        return getWebDriver().getPageSource();
    }

    public int getPageNum() {
        return pageNum;
    }

    public boolean hasNext() {
        return isNormal() && hasNext;
    }

    public boolean isNormal() {
        return PageState.LOADED.equals(pageState);
    }

    public boolean isTerminated() {
        return !isNormal();
    }

    public boolean reset() {
        return reset(0);
    }

    public boolean reset(int retries) {
        this.searchForm = null;
        this.pageState = PageState.PREPARE;
        this.pageNum = 1;
        this.hasNext = false;

        return refresh(retries);
    }

    public boolean refresh() {
        return refresh(0);
    }

    public boolean refresh(int retries) {
        int num = Math.max(0, retries);
        for (int i = 0; i <= num; i++) {
            if (loadPage()) {
                return true;
            }
        }
        return false;
    }

    public void available() {
        mark(PageState.PREPARE);
    }

    public void quit() {
        getWebDriver().quit();
    }

    private enum PageState {
        PREPARE, LOADED
    }
}
