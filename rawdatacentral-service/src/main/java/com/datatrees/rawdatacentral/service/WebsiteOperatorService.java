package com.datatrees.rawdatacentral.service;

import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;

/**
 * 运营商配置
 * Created by zhouxinghai on 2017/8/29
 */
public interface WebsiteOperatorService {

    /**
     * 获取运营商配置
     * @param websiteName
     * @return
     */
    WebsiteOperator getByWebsiteName(String websiteName);

    /**
     * 获取运营商配置
     * @param groupCode
     * @return
     */
    List<WebsiteOperator> queryByGroupCode(String groupCode);

    /**
     * 从老配置导入配置信息
     * @param config 自定义信息
     */
    void importWebsite(WebsiteOperator config);

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
     * 从其他环境导入配置
     * @param websiteName
     * @param to          开发,测试,准生产,预发布
     */
    void exportConfig(String websiteName, String to);

    /**
     * 保存运营商配置
     */
    void saveConfig(WebsiteOperator websiteOperator);

    /**
     * 启用/禁用
     * @param websiteName
     * @param enable
     */
    void updateEnable(String websiteName, Boolean enable);

    /**
     * 启用/禁用
     * @param websiteName
     * @param enable
     * @param auto
     */
    String updateEnable(String websiteName, Boolean enable, Boolean auto);

    /**
     * 查询所有禁用版本
     * @return
     */
    List<WebsiteOperator> queryDisable();

    List<WebsiteOperator> queryAll();

    /**
     * 启用/停用配置
     * @param websiteName 站点
     * @param enable      状态
     * @param auto        true:自动,false:手动
     * @return
     */
    Map<String, WebsiteOperator> updateWebsiteStatus(String websiteName, Boolean enable, Boolean auto);

}
