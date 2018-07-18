package com.datatrees.spider.operator.dao;

import javax.annotation.Resource;
import java.util.List;

import com.datatrees.spider.operator.dao.mapper.WebsiteGroupMapper;
import com.datatrees.spider.operator.domain.model.WebsiteGroup;
import org.apache.ibatis.annotations.Param;
/** create by system from table website_group(运营商分组) */
@Resource
public interface WebsiteGroupDAO extends WebsiteGroupMapper {

    /**
     * 获取最大权重运营商
     * @param groupCode
     * @return
     */
    WebsiteGroup queryMaxWeightWebsite(String groupCode);

    /**
     * 更新分组状态(启用/禁用)
     * @param websiteName
     * @param enable
     * @return
     */
    int updateEnable(@Param("websiteName") String websiteName, @Param("enable") Integer enable);

    /**
     * @param enable
     * @param operatorType
     * @param groupCode
     * @return
     */
    List<String> queryWebsiteNameList(@Param("enable") String enable, @Param("operatorType") String operatorType,
            @Param("groupCode") String groupCode);
}