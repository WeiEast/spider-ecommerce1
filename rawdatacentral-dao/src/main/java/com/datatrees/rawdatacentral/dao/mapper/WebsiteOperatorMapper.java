package com.datatrees.rawdatacentral.dao.mapper;

import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.model.example.WebsiteOperatorExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

 /** create by system from table website_operator(运营商配置)  */
public interface WebsiteOperatorMapper {
    int countByExample(WebsiteOperatorExample example);

    int deleteByPrimaryKey(Integer websiteId);

    int insertSelective(WebsiteOperator record);

    List<WebsiteOperator> selectByExample(WebsiteOperatorExample example);

    WebsiteOperator selectByPrimaryKey(Integer websiteId);

    int updateByExampleSelective(@Param("record") WebsiteOperator record, @Param("example") WebsiteOperatorExample example);

    int updateByPrimaryKeySelective(WebsiteOperator record);
}