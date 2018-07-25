package com.datatrees.spider.share.common.utils;

/**
 * 自定义重试
 * @param <T>
 */
public interface RetryHandler<T> {

    /**
     * 执行代码
     * @return
     */
    T execute();

    /**
     * 执行条件
     * @return
     */
    boolean check();

}
