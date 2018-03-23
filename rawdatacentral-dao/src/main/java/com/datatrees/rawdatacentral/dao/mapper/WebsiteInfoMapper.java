package com.datatrees.rawdatacentral.dao.mapper;

import com.datatrees.rawdatacentral.domain.model.WebsiteInfo;
import com.datatrees.rawdatacentral.domain.model.WebsiteInfoCriteria;
import com.datatrees.rawdatacentral.domain.model.WebsiteInfoWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface WebsiteInfoMapper {
    long countByExample(WebsiteInfoCriteria example);

    int deleteByExample(WebsiteInfoCriteria example);

    int deleteByPrimaryKey(Integer websiteId);

    int insert(WebsiteInfoWithBLOBs record);

    int insertSelective(WebsiteInfoWithBLOBs record);

    List<WebsiteInfoWithBLOBs> selectByExampleWithBLOBsWithRowbounds(WebsiteInfoCriteria example, RowBounds rowBounds);

    List<WebsiteInfoWithBLOBs> selectByExampleWithBLOBs(WebsiteInfoCriteria example);

    List<WebsiteInfo> selectByExampleWithRowbounds(WebsiteInfoCriteria example, RowBounds rowBounds);

    List<WebsiteInfo> selectByExample(WebsiteInfoCriteria example);

    WebsiteInfoWithBLOBs selectByPrimaryKey(Integer websiteId);

    int updateByExampleSelective(@Param("record") WebsiteInfoWithBLOBs record, @Param("example") WebsiteInfoCriteria example);

    int updateByExampleWithBLOBs(@Param("record") WebsiteInfoWithBLOBs record, @Param("example") WebsiteInfoCriteria example);

    int updateByExample(@Param("record") WebsiteInfo record, @Param("example") WebsiteInfoCriteria example);

    int updateByPrimaryKeySelective(WebsiteInfoWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(WebsiteInfoWithBLOBs record);

    int updateByPrimaryKey(WebsiteInfo record);
}