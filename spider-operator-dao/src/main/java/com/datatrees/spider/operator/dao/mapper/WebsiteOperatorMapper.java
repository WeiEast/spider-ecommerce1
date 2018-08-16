/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.operator.dao.mapper;

import java.util.List;

import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import com.datatrees.spider.operator.domain.model.example.WebsiteOperatorExample;
import org.apache.ibatis.annotations.Param;

/** create by system from table website_operator(运营商配置) */
public interface WebsiteOperatorMapper {

    int countByExample(WebsiteOperatorExample example);

    int deleteByPrimaryKey(Integer websiteId);

    int insertSelective(WebsiteOperator record);

    List<WebsiteOperator> selectByExample(WebsiteOperatorExample example);

    WebsiteOperator selectByPrimaryKey(Integer websiteId);

    int updateByExampleSelective(@Param("record") WebsiteOperator record, @Param("example") WebsiteOperatorExample example);

    int updateByPrimaryKeySelective(WebsiteOperator record);
}