/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.bobj.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年11月11日 下午8:01:56
 */
public class EventListener implements WebDriverEventListener {
    private Logger logger = LoggerFactory.getLogger(EventListener.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#beforeNavigateTo(java.lang.String,
     * org.openqa.selenium.WebDriver)
     */
    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {

        System.out.println("跳转前：" + url + "," + driver.findElement(By.id("kw")).getText());

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#afterNavigateTo(java.lang.String,
     * org.openqa.selenium.WebDriver)
     */
    @Override
    public void afterNavigateTo(String url, WebDriver driver) {}

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#beforeNavigateBack(org.openqa.selenium
     * .WebDriver)
     */
    @Override
    public void beforeNavigateBack(WebDriver driver) {}

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#afterNavigateBack(org.openqa.selenium
     * .WebDriver)
     */
    @Override
    public void afterNavigateBack(WebDriver driver) {}

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#beforeNavigateForward(org.openqa
     * .selenium.WebDriver)
     */
    @Override
    public void beforeNavigateForward(WebDriver driver) {
        System.out.println("跳转前：" + driver.findElement(By.id("kw")).getText());

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#afterNavigateForward(org.openqa
     * .selenium.WebDriver)
     */
    @Override
    public void afterNavigateForward(WebDriver driver) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#beforeFindBy(org.openqa.selenium
     * .By, org.openqa.selenium.WebElement, org.openqa.selenium.WebDriver)
     */
    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {}

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#afterFindBy(org.openqa.selenium.By,
     * org.openqa.selenium.WebElement, org.openqa.selenium.WebDriver)
     */
    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver) {}

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#beforeClickOn(org.openqa.selenium
     * .WebElement, org.openqa.selenium.WebDriver)
     */
    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        logger.info("WebDriver beforeClickOn value:" + element.getAttribute("value"));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#afterClickOn(org.openqa.selenium
     * .WebElement, org.openqa.selenium.WebDriver)
     */
    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {

        logger.info("WebDriver beforeChangeValueOf value:" + element.getAttribute("value"));


    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#beforeChangeValueOf(org.openqa.
     * selenium.WebElement, org.openqa.selenium.WebDriver)
     */
    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("WebDriver beforeChangeValueOf value:" + element.getAttribute("value"));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#afterChangeValueOf(org.openqa.selenium
     * .WebElement, org.openqa.selenium.WebDriver)
     */
    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver) {
        logger.info("WebDriver beforeChangeValueOf value:" + element.getAttribute("value"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.support.events.WebDriverEventListener#beforeScript(java.lang.String,
     * org.openqa.selenium.WebDriver)
     */
    @Override
    public void beforeScript(String script, WebDriver driver) {
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.support.events.WebDriverEventListener#afterScript(java.lang.String,
     * org.openqa.selenium.WebDriver)
     */
    @Override
    public void afterScript(String script, WebDriver driver) {}

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openqa.selenium.support.events.WebDriverEventListener#onException(java.lang.Throwable,
     * org.openqa.selenium.WebDriver)
     */
    @Override
    public void onException(Throwable throwable, WebDriver driver) {}

}
