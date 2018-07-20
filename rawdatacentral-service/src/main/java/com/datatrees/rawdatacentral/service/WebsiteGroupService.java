package com.datatrees.rawdatacentral.service;

import java.util.List;
import java.util.Map;

import com.datatrees.spider.operator.domain.model.WebsiteGroup;

/**
 * 运营商分组和权重
 * Created by zhouxinghai on 2017/8/29
 */
public interface WebsiteGroupService {

    /**
     * 删除分组配置
     * @param groupCode 分组
     * @return
     */
    void deleteByGroupCode(String groupCode);

    /**
     * 获取最大权重运营商
     * @param groupCode 分组
     * @return
     */
    WebsiteGroup queryMaxWeightWebsite(String groupCode);

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

    /**
     * 根据groupCode更新缓存
     * 计算MAX_WEIGHT_OPERATOR
     * weith>0
     * @param groupCode 分组
     * @return
     */
    void updateCacheByGroupCode(String groupCode);

    /**
     * 根据groupCode更新缓存
     * 计算MAX_WEIGHT_OPERATOR
     * weith>0
     * @return
     */
    void updateCache();

    /**
     * 更新配置状态
     * @param websiteName
     * @param enable
     */
    void updateEnable(String websiteName, Boolean enable);

    /**
     * 获取websitename
     * @param enable
     * @param operatorType
     * @param groupCode
     * @return
     */
    List<String> getWebsiteNameList(String enable, String operatorType, String groupCode);

    /**
     * 选择运营商
     * @param groupCode
     * @return
     */
    String selectOperator(String groupCode);

    /**
     * 清除运营商权重队列
     * @param groupCode
     */
    void clearOperatorQueueByGroupCode(String groupCode);

    /**
     * 清除运营商权重队列
     * @param websiteName
     */
    void clearOperatorQueueByWebsite(String websiteName);

    /**
     * 查询所有
     * @return
     */
    List<WebsiteGroup> queryAll();

    /**
     * 查询所有分组
     * @return
     */
    Map<String, String> queryAllGroupCode();

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

}
