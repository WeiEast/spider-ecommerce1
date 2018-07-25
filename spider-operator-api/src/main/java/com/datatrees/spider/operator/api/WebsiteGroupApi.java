package com.datatrees.spider.operator.api;

import java.util.List;
import java.util.Map;

import com.datatrees.spider.operator.domain.model.WebsiteGroup;

/**
 * 运营商分组和负载
 */
public interface WebsiteGroupApi {

    /**
     * 查询可用版本数量
     */
    Integer queryEnableCount(String groupCode);

    /**
     * 查询可用版本
     * @param groupCode
     * @return
     */
    List<WebsiteGroup> queryEnable(String groupCode);

    /**
     * 查询不可用版本
     * @param groupCode
     * @return
     */
    List<WebsiteGroup> queryDisable(String groupCode);

    /**
     * 更新站点状态
     * @param websiteName
     * @param enable
     * @return
     */
    int updateEnable(String websiteName, Boolean enable);

    /**
     * 查询站点名称
     * @param enable
     * @param operatorType
     * @param groupCode
     * @return
     */
    List<String> getWebsiteNameList(String enable, String operatorType, String groupCode);

    /**
     * 根据运营商查询groupCode
     * @param groupCode 分组
     * @return
     */
    List<WebsiteGroup> queryByGroupCode(String groupCode);

    /**
     * 配置运营商分组和权重
     * @param groupCode 分组
     * @param config    运营商-->权重
     */
    List<WebsiteGroup> configGroup(String groupCode, Map<String, Integer> config);

}