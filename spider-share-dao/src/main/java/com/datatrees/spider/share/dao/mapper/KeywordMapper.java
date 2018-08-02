package com.datatrees.spider.share.dao.mapper;

import java.util.List;

import com.datatrees.spider.share.domain.model.Keyword;
import com.datatrees.spider.share.domain.model.example.KeywordExample;

/** create by system from table t_keyword(keyword) */
public interface KeywordMapper {

    long countByExample(KeywordExample example);

    int deleteByPrimaryKey(Integer id);

    int insertSelective(Keyword record);

    List<Keyword> selectByExample(KeywordExample example);

    Keyword selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Keyword record);
}