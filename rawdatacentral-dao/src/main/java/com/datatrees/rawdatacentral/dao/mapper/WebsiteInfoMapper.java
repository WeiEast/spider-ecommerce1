package com.datatrees.rawdatacentral.dao.mapper;

import com.datatrees.rawdatacentral.domain.model.WebsiteInfo;
import com.datatrees.rawdatacentral.domain.model.example.WebsiteInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WebsiteInfoMapper {

    int deleteByPrimaryKey(Integer websiteId);


    int insertSelective(WebsiteInfo record);

    List<WebsiteInfo> selectByExample(WebsiteInfoExample example);

    WebsiteInfo selectByPrimaryKey(Integer websiteId);

    int updateByExampleSelective(@Param("record") WebsiteInfo record, @Param("example") WebsiteInfoExample example);


    int updateByPrimaryKeySelective(WebsiteInfo record);

}