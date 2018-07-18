package com.datatrees.rawdatacentral.api;

import java.util.List;
import java.util.Map;

import com.datatrees.spider.operator.domain.model.WebsiteOperator;

/**
 * website接口
 */
public interface WebsiteOperatorServiceApi {

    /**
     * 启用/停用配置
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
     * 根据websiteName查找配置
     * @param websiteName
     * @return
     */
    WebsiteOperator getByWebsiteName(String websiteName);

    /**
     * 查询所有运营商站点
     * @return
     */
    List<WebsiteOperator> queryAll();

    /**
     * 启用/停用配置
     * @param websiteName 站点
     * @param enable      状态
     * @param auto        true:自动,false:手动,微信通知不一样
     * @return
     */
    Map<String, WebsiteOperator> updateWebsiteStatus(String websiteName, Boolean enable, Boolean auto);

}


