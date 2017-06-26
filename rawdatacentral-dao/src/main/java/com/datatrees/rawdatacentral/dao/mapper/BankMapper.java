package com.datatrees.rawdatacentral.dao.mapper;

import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.domain.model.example.BankExample;
import java.util.List;

 /** create by system from table t_bank(Bank basic info)  */
public interface BankMapper {
    long countByExample(BankExample example);

    int deleteByPrimaryKey(Integer id);

    int insertSelective(Bank record);

    List<Bank> selectByExample(BankExample example);

    Bank selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Bank record);
}