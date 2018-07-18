package com.datatrees.spider.operator.dao.mapper;

import com.datatrees.spider.operator.domain.model.WebsiteGroup;
import com.datatrees.spider.operator.domain.model.example.WebsiteGroupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

 /** create by system from table website_group(配置分组)  */
public interface WebsiteGroupMapper {
    int countByExample(WebsiteGroupExample example);

    int deleteByExample(WebsiteGroupExample example);

    int insertSelective(WebsiteGroup record);

    List<WebsiteGroup> selectByExample(WebsiteGroupExample example);

    int updateByExampleSelective(@Param("record") WebsiteGroup record, @Param("example") WebsiteGroupExample example);
}