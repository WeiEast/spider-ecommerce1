package com.datatrees.rawdatacentral.dao.mapper;

import com.datatrees.rawdatacentral.domain.model2.Website;
import com.datatrees.rawdatacentral.domain.model2.example.WebsiteExample;
import java.util.List;

 /** create by system from table t_website(website basic info)  */
public interface WebsiteMapper {
    long countByExample(WebsiteExample example);

    int deleteByPrimaryKey(Integer websiteId);

    int insertSelective(Website record);

    List<Website> selectByExample(WebsiteExample example);

    Website selectByPrimaryKey(Integer websiteId);

    int updateByPrimaryKeySelective(Website record);
}