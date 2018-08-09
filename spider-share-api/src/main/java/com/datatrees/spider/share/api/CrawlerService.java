/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * <p>
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.spider.share.api;

import java.util.List;
import java.util.Map;

import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.model.WebsiteConf;

/**
 * 爬虫对外dubbo接口
 * Created by zhouxinghai on 2017/5/23
 */
@Deprecated
public interface CrawlerService {

    // 未来可以增加每个网页显示什么字段，给什么提示，有多少tab，点击每个tab访问什么连接的配置
    WebsiteConf getWebsiteConf(String websiteName);

    // 未来可以增加每个网页显示什么字段，给什么提示，有多少tab，点击每个tab访问什么连接的配置
    List<WebsiteConf> getWebsiteConf(List<String> websiteNameList);

    /**
     * 模拟登陆,一般是运营商
     * @param taskId         任务ID
     * @param username       登陆名,例如:手机号
     * @param password       登陆密码,例如:查询密码/服务密码
     * @param code           图片验证码
     * @param randomPassword 短信验证码
     * @param extra          附加信息
     * @return
     */
    HttpResult<String> login(long taskId, String username, String password, String code, String randomPassword, Map<String, String> extra);

    /**
     * 爬取过程中,向APP端弹出二维码,前端扫描和确认,将这个动作告诉插件,后端调用相关接口校验是否是一件扫描或者确认
     * 这个一般支付宝或者淘宝用
     * APP弹出二维码后就不断verifyQr,APP是不知道用户是否扫码过,所以要不断轮询
     * 二维码状态:
     * WAITTING:继续verifyQr
     * SCANNED:已经扫码,继续verifyQr
     * FAILED:验证失败,结束verifyQr,等待任务结束或者下一条指令
     * SUCCESS:验证失败,结束verifyQr,等待任务结束或者下一条指令
     * @param directiveId 指令ID
     * @param taskId      网关任务id
     * @param extra       附加信息,目前null
     * @return
     */
    HttpResult<String> verifyQr(String directiveId, long taskId, Map<String, String> extra);

}
