package com.datatrees.rawdatacentral.dao.mapper;

import com.datatrees.rawdatacentral.domain.model.BankMail;
import com.datatrees.rawdatacentral.domain.model.example.BankMailExample;
import java.util.List;

 /** create by system from table t_bank_email(Bank email info)  */
public interface BankMailMapper {
    long countByExample(BankMailExample example);

    int deleteByPrimaryKey(Integer id);

    int insertSelective(BankMail record);

    List<BankMail> selectByExample(BankMailExample example);

    BankMail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BankMail record);
}