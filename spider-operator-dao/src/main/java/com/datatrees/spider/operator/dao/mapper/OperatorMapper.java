package com.datatrees.spider.operator.dao.mapper;

import java.util.List;

import com.datatrees.spider.operator.domain.model.Operator;
import com.datatrees.spider.operator.domain.model.example.OperatorExample;

/** create by system from table t_operator(operator basic info) */
public interface OperatorMapper {

    long countByExample(OperatorExample example);

    int deleteByPrimaryKey(Integer id);

    int insertSelective(Operator record);

    List<Operator> selectByExample(OperatorExample example);

    Operator selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Operator record);
}