package com.datatrees.rawdatacentral.dao.mapper;

import com.datatrees.rawdatacentral.domain.model.Ecommerce;
import com.datatrees.rawdatacentral.domain.model.example.EcommerceExample;
import java.util.List;

 /** create by system from table t_ecommerce(ecommerce basic info)  */
public interface EcommerceMapper {
    long countByExample(EcommerceExample example);

    int deleteByPrimaryKey(Integer id);

    int insertSelective(Ecommerce record);

    List<Ecommerce> selectByExample(EcommerceExample example);

    Ecommerce selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Ecommerce record);
}