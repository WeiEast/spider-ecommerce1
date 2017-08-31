package com.datatrees.rawdatacentral.dao;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.dao.mapper.OperatorGroupMapper;
import com.datatrees.rawdatacentral.domain.model.OperatorGroup;

/** create by system from table operator_group(运营商分组) */
@Resource
public interface OperatorGroupDAO extends OperatorGroupMapper {

    /**
     * 获取最大权重运营商
     * @param groupCode
     * @return
     */
    OperatorGroup queryMaxWeightWebsite(String groupCode);

}