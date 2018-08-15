package com.treefinance.crawler.framework.process.search;

/**
 * @author Jerry
 * @since 17:28 2018/7/31
 */
public interface UrlFilterHandler {

    boolean filter(String url);
}
