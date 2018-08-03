package com.datatrees.rawdatacentral.dao.mapper;

import java.util.List;

import com.datatrees.spider.share.domain.model.AppCrawlerConfig;
import com.datatrees.spider.share.domain.model.example.AppCrawlerConfigCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AppCrawlerConfigMapper {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    long countByExample(AppCrawlerConfigCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    int deleteByExample(AppCrawlerConfigCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    int insert(AppCrawlerConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    int insertSelective(AppCrawlerConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    List<AppCrawlerConfig> selectByExampleWithRowbounds(AppCrawlerConfigCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    List<AppCrawlerConfig> selectByExample(AppCrawlerConfigCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    AppCrawlerConfig selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") AppCrawlerConfig record, @Param("example") AppCrawlerConfigCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    int updateByExample(@Param("record") AppCrawlerConfig record, @Param("example") AppCrawlerConfigCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(AppCrawlerConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_crawler_config
     * @mbg.generated
     */
    int updateByPrimaryKey(AppCrawlerConfig record);
}