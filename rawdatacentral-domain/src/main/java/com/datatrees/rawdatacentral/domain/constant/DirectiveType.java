package com.datatrees.rawdatacentral.domain.constant;

/**
 * 交互指令类型
 * Created by zhouxinghai on 2017/5/22.
 */
public class DirectiveType {

    /**
     * 将后端失败率高的页面交给APP抓取
     */
    public static final String GRAB_URL     = "grab_url";
    /**
     * 模拟登陆
     */
    public static final String PLUGIN_LOGIN = "plugin_login";
    /**
     * 抓取过程中,图片验证码
     */
    public static final String CRAWL_CODE   = "crawl_code";
    /**
     * 抓取过程中,短信验证码
     */
    public static final String CRAWL_SMS    = "crawl_sms";
    /**
     * 抓取过程中,二维码
     */
    public static final String CRAWL_QR     = "crawl_qr";
}
