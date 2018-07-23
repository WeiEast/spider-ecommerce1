package com.datatrees.spider.operator.dao.mapper;

import java.util.List;

import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import com.datatrees.spider.operator.domain.model.example.WebsiteOperatorExample;
import org.apache.ibatis.annotations.Param;

/** create by system from table website_operator(运营商配置) */
public interface WebsiteOperatorMapper {

    int countByExample(WebsiteOperatorExample example);

    int deleteByPrimaryKey(Integer websiteId);

    int insertSelective(WebsiteOperator record);

    List<WebsiteOperator> selectByExample(WebsiteOperatorExample example);

    WebsiteOperator selectByPrimaryKey(Integer websiteId);

    int updateByExampleSelective(@Param("record") WebsiteOperator record, @Param("example") WebsiteOperatorExample example);

    int updateByPrimaryKeySelective(WebsiteOperator record);
}