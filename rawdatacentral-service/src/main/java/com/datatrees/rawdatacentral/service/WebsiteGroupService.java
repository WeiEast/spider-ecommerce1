package com.datatrees.rawdatacentral.service;

import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.domain.model.WebsiteGroup;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;

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
    List<OperatorCatalogue> updateCache();

    /**
     * 更新配置状态
     * @param websiteName
     * @param enable
     */
    void updateEnable(String websiteName, Boolean enable);

}
