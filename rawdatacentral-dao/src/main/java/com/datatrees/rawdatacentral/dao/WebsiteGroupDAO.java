package com.datatrees.rawdatacentral.dao;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.dao.mapper.WebsiteGroupMapper;
import com.datatrees.rawdatacentral.domain.model.WebsiteGroup;

/** create by system from table operator_group(运营商分组) */
@Resource
public interface WebsiteGroupDAO extends WebsiteGroupMapper {

    /**
     * 获取最大权重运营商
     * @param groupCode
     * @return
     */
    WebsiteGroup queryMaxWeightWebsite(String groupCode);

}