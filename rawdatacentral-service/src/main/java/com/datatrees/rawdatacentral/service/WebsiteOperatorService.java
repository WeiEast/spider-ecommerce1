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
     *根据环境和站点名获取运营商配置
     * @param websiteName
     * @param env
     * @return
     */

    WebsiteOperator getByWebsiteName(String websiteName);
    WebsiteOperator getByWebsiteNameAndEnv(String websiteName,String env);

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
     * 保存运营商配置
     */
//    void saveConfig(WebsiteOperator websiteOperator);

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
    Map<String, WebsiteOperator> updateWebsiteStatus(String websiteName, boolean enable, boolean auto);

}
