package com.datatrees.spider.operator.service;

import java.util.List;

import com.datatrees.spider.operator.domain.OperatorLoginConfig;
import com.datatrees.spider.operator.domain.model.WebsiteGroup;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import com.datatrees.spider.operator.service.plugin.OperatorPlugin;
import com.datatrees.spider.share.domain.website.WebsiteConfig;

/**
 * 运营商配置
 * Created by zhouxinghai on 2017/8/29
 */
public interface WebsiteOperatorService {

    /**
     * 根据环境和站点名获取运营商配置
     * env:服务器所在环境
     * @param websiteName
     * @return
     */
    WebsiteOperator getByWebsiteName(String websiteName);

    /**
     * 根据websiteName和env查询运营商
     * @param websiteName
     * @param env
     * @return
     */
    WebsiteOperator getByWebsiteNameAndEnv(String websiteName, String env);

    /**
     * 获取运营商配置
     * @param groupCode
     * @return
     */
    List<WebsiteOperator> queryByGroupCode(String groupCode);

    /**
     * 更新配置
     * @param config
     */
    void updateWebsite(WebsiteOperator config);

    /**
     * 从其他环境导入配置
     * @param websiteName
     * @param from        开发,测试,准生产,预发布
     */
    void importConfig(String websiteName, String from);

    /**
     * 从开发环境导入到其他环境
     * @param websiteName
     * @param to          开发,测试,准生产,预发布
     */
    void exportConfig(String websiteName, String to);

    /**
     * 导出运营商时的保存
     * @param config
     */
    void saveConfigForExport(WebsiteOperator config);

    /**
     * 启用/禁用
     * @param websiteName
     * @param enable
     */
    void updateEnable(String websiteName, Boolean enable);

    /**
     * 查询所有禁用版本
     * @return
     */
    List<WebsiteOperator> queryDisable();

    /**
     * 查询所有版本
     * @return
     */
    List<WebsiteOperator> queryAll();

    /**
     * 启用/停用配置
     * @param websiteName 站点
     * @param enable      状态
     * @param auto        true:自动,false:手动
     * @return from, to
     */
    List<WebsiteGroup> updateWebsiteStatus(String groupCode, String websiteName, boolean enable, boolean auto);

    /**
     * 获取登陆配置
     * @param websiteName
     * @return
     */
    OperatorLoginConfig getLoginConfig(String websiteName);

    /**
     * 获取运营商plugin
     * @param websiteName
     * @param taskId
     * @return
     */
    OperatorPlugin getOperatorPluginService(String websiteName, Long taskId);

    WebsiteConfig buildWebsiteConfig(WebsiteOperator websiteOperator);

    /**
     * 发送消息,启动爬虫
     * @return
     */
    boolean sendOperatorCrawlerStartMessage(Long taskId, String websiteName);

    /**
     * 发送消息,启动登陆后处理
     * @return
     */
    boolean sendOperatorLoginPostMessage(Long taskId, String websiteName);
}
