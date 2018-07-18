package com.datatrees.spider.operator.dao;

import javax.annotation.Resource;

import com.datatrees.spider.operator.dao.mapper.WebsiteOperatorMapper;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import org.apache.ibatis.annotations.Param;

/** create by system from table website_operator(运营商配置) */
@Resource
public interface WebsiteOperatorDAO extends WebsiteOperatorMapper {

    /**
     * 保存运营商和主键(不使用自增主键)
     * @param record
     * @return
     */
    int insertSelectiveWithPrimaryKey(WebsiteOperator record);

    /**
     * 更新分组状态(启用/禁用)
     * @param websiteName
     * @param enable
     * @return
     */
    int updateEnable(@Param("websiteName") String websiteName, @Param("enable") Integer enable);

}