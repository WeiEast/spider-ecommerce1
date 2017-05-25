/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * <p>
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.rawdatacentral.api;

import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

import java.util.List;
import java.util.Map;

/**
 * 爬虫对外dubbo接口
 * Created by zhouxinghai on 2017/5/23
 */
public interface CrawlerService {

    // 未来可以增加每个网页显示什么字段，给什么提示，有多少tab，点击每个tab访问什么连接的配置
    WebsiteConf getWebsiteConf(String websiteName);

    // 未来可以增加每个网页显示什么字段，给什么提示，有多少tab，点击每个tab访问什么连接的配置
    List<WebsiteConf> getWebsiteConf(List<String> websiteNameList);

    // 修改配置
    boolean updateWebsiteConfig(String websiteName, String searchConfigSource, String extractConfigSource);

    /**
     * 获取,这里是登陆插件用,插件里的是通过指令发出去了,而且不支持刷新
     * 模拟登陆时,刷新图片验证码,发送短信验证码,抓取过程中不用(抓取过程中用指令传输信息,且不支持刷新)
     *  
     * @param taskId 网关任务id
     * @param type 0:发送短信验证码到手机 1:刷新图片验证码 2:刷新二维码 (目前只有短信验证码和图片验证码)
     * @param extra 附加信息,目前null
     * @return 
     */
    HttpResult<String> fetchLoginCode(long taskId, int type, Map<String, Object> extra);

    /**
     * 抓取过程中导入图片验证码和短信验证码,如果后端校验失败会重新发出指令附带图片验证码信息
     * 例如:运营商通话记录获取
     * 目前只有短信验证码在用(运营商)
     * @param taskId 网关任务id
     * @param type 0:短信验证码 1:图片验证码
     * @param code 验证码(图片或者短信)
     * @param extra 附加信息,目前null
     * @return
     */
    HttpResult<Boolean> importCrawlCode(long taskId, int type, String code, Map<String, Object> extra);

    /**
     * 爬取过程中,向APP端弹出二维码,前端扫描和确认,将这个动作告诉插件,后端调用相关接口校验是否是一件扫描或者确认
     * 这个一般支付宝或者淘宝用
     * @param taskId 网关任务id
     * @param extra 附加信息,目前null
     * @return
     */
    HttpResult<String> verifyQr(long taskId, Map<String, Object> extra);

    /**
     * 取消任务
     * @param taskId 网关任务id
     * @param extra 附加信息,目前null
     * @return
     */
    HttpResult<Boolean> cancel(long taskId, Map<String, Object> extra);

    /**
     * 导入前端爬取结果
     * 以后可能运营商或者电商用
     * 部分成果率比较低的url,让前端爬取后把cookie和html传给后端,后端搜索是单线程,一直等待前端结果
     * @param taskId 网关任务id
     * @param html 爬取后的网页内容
     * @param cookies 爬取完成后前端cookie
     * @param extra 附加信息,暂时没有
     * @return
     */
    HttpResult<Boolean> importAppCrawlResult(long taskId, String html, String cookies, Map<String, Object> extra);

}
