package com.datatrees.rawdatacentral.dao;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.dao.mapper.WebsiteGroupMapper;
import com.datatrees.rawdatacentral.domain.model.WebsiteGroup;
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
}