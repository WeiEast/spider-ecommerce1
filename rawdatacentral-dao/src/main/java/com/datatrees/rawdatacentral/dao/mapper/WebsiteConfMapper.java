package com.datatrees.rawdatacentral.dao.mapper;

import com.datatrees.rawdatacentral.domain.model2.WebsiteConf;
import com.datatrees.rawdatacentral.domain.model2.example.WebsiteConfExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

 /** create by system from table t_website_conf(website config,sopport search)  */
public interface WebsiteConfMapper {
    long countByExample(WebsiteConfExample example);

    int deleteByPrimaryKey(Integer websiteConfId);

    int insertSelective(WebsiteConf record);

    List<WebsiteConf> selectByExampleWithBLOBs(WebsiteConfExample example);

    List<WebsiteConf> selectByExample(WebsiteConfExample example);

    WebsiteConf selectByPrimaryKey(Integer websiteConfId);

    int updateByExampleWithBLOBs(@Param("record") WebsiteConf record, @Param("example") WebsiteConfExample example);

    int updateByPrimaryKeySelective(WebsiteConf record);

    int updateByPrimaryKeyWithBLOBs(WebsiteConf record);
}