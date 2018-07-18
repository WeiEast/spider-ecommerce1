package com.datatrees.rawdatacentral.service;

import com.datatrees.spider.operator.domain.model.WebsiteOperator;

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

    /**
     * 运营商状态变更
     * @param change 变更的运营商
     * @param from   变更前
     * @param to     变更后
     * @param auto   操作方式:自动/手动
     * @return
     */
    Boolean sendMsgForOperatorStatusUpdate(WebsiteOperator change, WebsiteOperator from, WebsiteOperator to, Boolean enable, Boolean auto);

}
