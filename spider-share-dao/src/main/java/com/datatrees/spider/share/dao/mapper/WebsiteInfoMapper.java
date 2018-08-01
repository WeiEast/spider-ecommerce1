package com.datatrees.spider.share.dao.mapper;

import com.datatrees.spider.share.domain.model.WebsiteInfo;
import com.datatrees.spider.share.domain.model.example.WebsiteInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

 /** create by system from table website_info(非运营商配置)  */
public interface WebsiteInfoMapper {
    long countByExample(WebsiteInfoExample example);

    int deleteByExample(WebsiteInfoExample example);

    int deleteByPrimaryKey(Integer websiteId);

    int insertSelective(WebsiteInfo record);

    List<WebsiteInfo> selectByExample(WebsiteInfoExample example);

    WebsiteInfo selectByPrimaryKey(Integer websiteId);

    int updateByExampleSelective(@Param("record") WebsiteInfo record, @Param("example") WebsiteInfoExample example);

    int updateByPrimaryKeySelective(WebsiteInfo record);

    int batchInsertSelective(List<WebsiteInfo> records);
}