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

package com.datatrees.spider.operator.api;

import java.util.List;

import com.datatrees.spider.operator.domain.model.WebsiteGroup;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;

/**
 * website接口
 */
public interface WebsiteOperatorApi {

    /**
     * 启用/停用配置
     * @param websiteName
     * @param enable
     */
    void updateEnable(String websiteName, Boolean enable);

    /**
     * 查询所有禁用版本
     * @return
     */
    List<WebsiteOperator> queryDisable();

    /**
     * 根据websiteName查找配置
     * @param websiteName
     * @return
     */
    WebsiteOperator getByWebsiteName(String websiteName);

    /**
     * 查询所有运营商站点
     * @return
     */
    List<WebsiteOperator> queryAll();

    /**
     * 启用/停用配置
     * @param websiteName 站点
     * @param enable      状态
     * @param auto        true:自动,false:手动,微信通知不一样
     * @return
     */
    List<WebsiteGroup> updateWebsiteStatus(String groupCode, String websiteName, Boolean enable, Boolean auto);

}


