package com.datatrees.spider.bank.dao.mapper;

import java.util.List;

import com.datatrees.spider.bank.domain.model.Bank;
import com.datatrees.spider.bank.domain.model.example.BankExample;

/** create by system from table t_bank(Bank basic info) */
public interface BankMapper {

    long countByExample(BankExample example);

    int deleteByPrimaryKey(Integer bankId);

    int insertSelective(Bank record);

    List<Bank> selectByExample(BankExample example);

    Bank selectByPrimaryKey(Integer bankId);

    int updateByPrimaryKeySelective(Bank record);
}