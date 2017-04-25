package com.datatrees.rawdatacentral.domain.enums;

/**
 * 任务状态
 * Created by zhouxinghai on 2017/4/25.
 */
public enum TaskLogStatusEnum {

    PROCESSING("processing", "处理中"),
    CRAWLER("crawler", "进入爬取阶段"),
    PREPARE_FOR_CRAWL("prepare_for_crawl", "初始化完成"),
    REFRESH_LOGIN_RANDOMPASSWORD("refresh_login_randompassword", "刷新登录短信验证码"),
    REFRESH_LOGIN_CODE("refresh_login_code", "刷新登录图片验证码"),
    REFRESH_LOGIN_QR_CODE("refresh_login_qr_code", "刷新登录二维码"),
    START_LOGIN("start_login", "开始登录"),
    WAITING_FOR_SMS_VERIFY("waiting_for_sms_verify", "等待短信验证码"),
    WAITING_FOR_PICTURE_VERIFY("waiting_for_picture_verify", "等待图片验证码"),
    WAITING_FOR_QR_VERIFY("waiting_for_qr_verify", "等待二维码"),
    WAITING_FOR_URL_VERIFY("waiting_for_url_verify", "等待前端页面返回"),
    LOGIN_SUCCESS("login_success", "登录成功"),
    LOGIN_FAILED("login_failed", "登录失败"),
    CRAWLER_FAILED("crawler_failed", "爬取失败"),
    CRAWLER_SUCCESS("crawler_success", "爬取成功"),
    TASK_TIMEOUT("task_timeout", "任务超时"),
    TASK_SUCCESS("task_success", "任务成功"),
    TASK_FAIL("task_fail", "任务失败"),;


    /**
     * 状态码
     */
    private String code;

    /**
     * 描述
     */
    private String name;

    TaskLogStatusEnum(String statusCode, String remark) {
        this.code = statusCode;
        this.name = remark;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TaskLogStatusEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

