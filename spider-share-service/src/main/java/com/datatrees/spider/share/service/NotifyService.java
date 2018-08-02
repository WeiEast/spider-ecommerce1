package com.datatrees.spider.share.service;

/**
 * 通知消息
 * Created by zhouxinghai on 2017/9/29
 */
public interface NotifyService {

    /**
     * 发送预警邮件
     * @param subject 主题
     * @param body    内容
     */
    Boolean sendMonitorEmail(String subject, String body);

    /**
     * 通过微信企业号发送预警信息
     * @param body 内容
     */
    Boolean sendMonitorWeChat(String body);

    /**
     * 通过短信发送预警信息
     * @param body 内容
     */
    Boolean sendMonitorSms(String body);

}
