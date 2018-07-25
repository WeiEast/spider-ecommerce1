package com.datatrees.spider.share.domain.http;

/**
 * Created by zhouxinghai on 2017/7/17.
 */
public class HttpHeadKey {

    public static final String REFERER          = "Referer";      //引用页

    public static final String CONTENT_TYPE     = "Content-Type"; //数据类型

    public static final String CONNECTION       = "Connection"; //数据类型close,Keep-Alive

    public static final String USER_AGENT       = "User-Agent"; //数据类型close,Keep-Alive

    public static final String X_REQUESTED_WITH = "X-Requested-With";//ajax异步请求

    public static final String SET_COOKIE       = "Set-Cookie";//设置cookie

    public static final String LOCATION         = "Location";//重定向
}
