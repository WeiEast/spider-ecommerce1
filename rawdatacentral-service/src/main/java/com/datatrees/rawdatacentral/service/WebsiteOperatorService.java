package com.datatrees.rawdatacentral.service;

import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.domain.model.OperatorGroup;
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
     * 获取最大权重运营商
     * @param websiteName
     * @return
     */
    WebsiteOperator queryMaxWeightWebsite(String websiteName);

    /**
     * 根据运营商查询groupCode
     * @param groupCode
     * @return
     */
    List<OperatorGroup> queryByGroupCode(String groupCode);

    /**
     * 从老配置导入配置信息
     * @param config 自定义信息
     */
    void importWebsite(WebsiteOperator config);

    /**
     * 配置运营商分组和权重
     * @param groupCode 分组
     * @param config    运营商-->权重
     */
    List<OperatorGroup> configOperatorGroup(String groupCode, Map<String, Integer> config);

}
