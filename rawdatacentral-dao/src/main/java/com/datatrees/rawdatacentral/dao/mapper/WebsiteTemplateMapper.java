package com.datatrees.rawdatacentral.dao.mapper;

import com.datatrees.rawdatacentral.domain.model2.WebsiteTemplate;
import com.datatrees.rawdatacentral.domain.model2.example.WebsiteTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

 /** create by system from table t_website_template()  */
public interface WebsiteTemplateMapper {
    long countByExample(WebsiteTemplateExample example);

    int deleteByPrimaryKey(Integer templateId);

    int insertSelective(WebsiteTemplate record);

    List<WebsiteTemplate> selectByExampleWithBLOBs(WebsiteTemplateExample example);

    List<WebsiteTemplate> selectByExample(WebsiteTemplateExample example);

    WebsiteTemplate selectByPrimaryKey(Integer templateId);

    int updateByExampleWithBLOBs(@Param("record") WebsiteTemplate record, @Param("example") WebsiteTemplateExample example);

    int updateByPrimaryKeySelective(WebsiteTemplate record);

    int updateByPrimaryKeyWithBLOBs(WebsiteTemplate record);
}