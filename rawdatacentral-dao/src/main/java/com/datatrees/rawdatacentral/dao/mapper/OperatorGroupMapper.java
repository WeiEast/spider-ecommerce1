package com.datatrees.rawdatacentral.dao.mapper;

import com.datatrees.rawdatacentral.domain.model.OperatorGroup;
import com.datatrees.rawdatacentral.domain.model.example.OperatorGroupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

 /** create by system from table operator_group(运营商分组)  */
public interface OperatorGroupMapper {
    int countByExample(OperatorGroupExample example);

    int insertSelective(OperatorGroup record);

    List<OperatorGroup> selectByExample(OperatorGroupExample example);

    int updateByExampleSelective(@Param("record") OperatorGroup record, @Param("example") OperatorGroupExample example);
}