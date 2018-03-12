package com.datatrees.rawdatacentral.domain.enums;

/**
 * 二维码状态
 * @author zhouxinghai
 * @date 2018/1/17
 */
public class QRStatus {

    public static final String WAITING         = "WAITING";//等待用户扫描
    public static final String SCANNED         = "SCANNED";//用户已扫描,等待确认
    public static final String CONFIRMED       = "CONFIRMED";//用户已确认,等待验证
    public static final String EXPIRE          = "EXPIRE";//二维码失效
    public static final String FAILED          = "FAILED";//验证失败
    public static final String SUCCESS         = "SUCCESS";//验证成功
    public static final String CRAWLER_FAILED  = "CRAWLER_FAILED";//任务结束,爬取失败
    public static final String CRAWLER_SUCCESS = "CRAWLER_SUCCESS";//任务结束,爬取成功
    public static final String REQUIRE_SECOND_PASSWORD = "REQUIRE_SECOND_PASSWORD";//请求qq邮箱独立密码

}
