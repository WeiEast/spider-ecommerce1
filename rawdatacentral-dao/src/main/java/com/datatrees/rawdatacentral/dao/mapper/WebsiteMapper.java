package com.datatrees.rawdatacentral.dao.mapper;

import java.util.List;

import com.datatrees.rawdatacentral.domain.model2.Website;
import com.datatrees.rawdatacentral.domain.model2.example.WebsiteExample;

/** create by system from table t_website(website basic info) */
public interface WebsiteMapper {

    long countByExample(WebsiteExample example);

    int deleteByPrimaryKey(Integer websiteId);

    int insertSelective(Website record);

    List<Website> selectByExample(WebsiteExample example);

    Website selectByPrimaryKey(Integer websiteId);

    int updateByPrimaryKeySelective(Website record);
}