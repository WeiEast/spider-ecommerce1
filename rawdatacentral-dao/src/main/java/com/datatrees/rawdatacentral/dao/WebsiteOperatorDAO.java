package com.datatrees.rawdatacentral.dao;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.dao.mapper.WebsiteOperatorMapper;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;

/** create by system from table website_operator(运营商配置) */
@Resource
public interface WebsiteOperatorDAO extends WebsiteOperatorMapper {

    /**
     * 保存运营商和主键(不使用自增主键)
     * @param record
     * @return
     */
    int insertSelectiveWithPrimaryKey(WebsiteOperator record);

}