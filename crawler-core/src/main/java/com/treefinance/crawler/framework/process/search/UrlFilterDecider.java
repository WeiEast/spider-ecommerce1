package com.treefinance.crawler.framework.process.search;

/**
 * @author Jerry
 * @since 17:33 2018/7/31
 */
interface UrlFilterDecider {

    boolean deny(String url);
}
