package com.datatrees.rawdatacentral.dao.mapper;

import java.util.List;

import com.datatrees.rawdatacentral.domain.model.AppCrawlerConfig;
import com.datatrees.rawdatacentral.domain.model.example.AppCrawlerConfigExample;

/**
 * User: yand
 * Date: 2018/4/2
 * long countByExample(AppCrawlerConfigExample example);
 */
public interface AppCrawlerConfigMapper {

    int deleteByPrimaryKey(Integer id);

    int insertSelective(AppCrawlerConfig record);

    List<AppCrawlerConfig> selectByExample(AppCrawlerConfigExample example);

    AppCrawlerConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppCrawlerConfig record);

}
