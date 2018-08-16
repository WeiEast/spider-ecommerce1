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

package com.datatrees.spider.operator.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.spider.operator.dao.WebsiteGroupDAO;
import com.datatrees.spider.operator.domain.model.WebsiteGroup;
import com.datatrees.spider.operator.domain.model.example.WebsiteGroupExample;
import com.datatrees.spider.operator.service.WebsiteGroupService;
import com.datatrees.spider.operator.service.WebsiteOperatorService;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.WeightUtils;
import com.datatrees.spider.share.domain.GroupEnum;
import com.datatrees.spider.share.domain.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebsiteGroupServiceImpl implements WebsiteGroupService, InitializingBean {

    private static final Logger                 logger = LoggerFactory.getLogger(WebsiteGroupServiceImpl.class);

    private static       WeightUtils            weightUtils;

    @Resource
    private              WebsiteGroupDAO        websiteGroupDAO;

    @Resource
    private              WebsiteOperatorService websiteOperatorService;

    @Value("${core.redis.hostName}")
    private              String                 redisIp;

    @Value("${core.redis.password}")
    private              String                 redisPassword;

    @Override
    public List<WebsiteGroup> queryByGroupCode(String groupCode) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is blank");
        WebsiteGroupExample example = new WebsiteGroupExample();
        example.createCriteria().andGroupCodeEqualTo(groupCode);
        return websiteGroupDAO.selectByExample(example);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        WebsiteGroupExample example = new WebsiteGroupExample();
        example.createCriteria().andGroupCodeEqualTo(groupCode);
        websiteGroupDAO.deleteByExample(example);
        clearOperatorQueueByGroupCode(groupCode);
    }

    @Override
    public List<WebsiteGroup> configGroup(String groupCode, Map<String, Integer> config) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is null");
        if (null == config || config.isEmpty()) {
            throw new CommonException("config is empty");
        }
        deleteByGroupCode(groupCode);
        GroupEnum groupEnum = GroupEnum.getByGroupCode(groupCode);
        CheckUtils.checkNotNull(groupEnum, "groupCode not found");
        for (Map.Entry<String, Integer> entry : config.entrySet()) {
            WebsiteGroup operatorGroup = new WebsiteGroup();
            operatorGroup.setGroupCode(groupCode);
            operatorGroup.setGroupName(groupEnum.getGroupName());
            operatorGroup.setWebsiteType(groupEnum.getWebsiteType().getValue());
            operatorGroup.setWebsiteName(entry.getKey());
            operatorGroup.setWeight(entry.getValue());
            operatorGroup.setWebsiteTitle(websiteOperatorService.getByWebsiteName(entry.getKey()).getWebsiteTitle());
            websiteGroupDAO.insertSelective(operatorGroup);
        }
        clearOperatorQueueByGroupCode(groupCode);
        return queryByGroupCode(groupCode);
    }

    @Override
    public void updateEnable(String websiteName, Boolean enable) {
        if (null != websiteName && null != enable) {
            websiteGroupDAO.updateEnable(websiteName, enable ? 1 : 0);
            clearOperatorQueueByWebsite(websiteName);
        }
    }

    @Override
    public List<String> getWebsiteNameList(String enable, String operatorType, String groupCode) {
        return websiteGroupDAO.queryWebsiteNameList(enable, operatorType, groupCode);
    }

    @Override
    public String selectOperator(String groupCode) {
        return weightUtils.poll(groupCode);
    }

    @Override
    public void clearOperatorQueueByGroupCode(String groupCode) {
        weightUtils.clear(groupCode);
    }

    @Override
    public void clearOperatorQueueByWebsite(String websiteName) {
        List<WebsiteGroup> websites = queryAll();
        websites.stream().filter(f -> f.getWebsiteName().equals(websiteName)).forEach(e -> {
            weightUtils.clear(e.getGroupCode());
        });
    }

    @Override
    public List<WebsiteGroup> queryAll() {
        WebsiteGroupExample example = new WebsiteGroupExample();
        return websiteGroupDAO.selectByExample(example);
    }

    @Override
    public Map<String, String> queryAllGroupCode() {
        Map<String, String> map = new HashMap<>();
        List<WebsiteGroup> websites = queryAll();
        websites.forEach(e -> {
            map.put(e.getGroupCode(), e.getGroupName());
        });
        return map;
    }

    @Override
    public Integer queryEnableCount(String groupCode) {
        WebsiteGroupExample example = new WebsiteGroupExample();
        WebsiteGroupExample.Criteria criteria = example.createCriteria();
        criteria.andGroupCodeEqualTo(groupCode).andEnableEqualTo(true).andWeightGreaterThan(0);
        return websiteGroupDAO.countByExample(example);
    }

    @Override
    public List<WebsiteGroup> queryEnable(String groupCode) {
        WebsiteGroupExample example = new WebsiteGroupExample();
        WebsiteGroupExample.Criteria criteria = example.createCriteria();
        criteria.andGroupCodeEqualTo(groupCode).andEnableEqualTo(true).andWeightGreaterThan(0);
        example.setOrderByClause("weight desc");
        return websiteGroupDAO.selectByExample(example);
    }

    @Override
    public List<WebsiteGroup> queryDisable(String groupCode) {
        WebsiteGroupExample example = new WebsiteGroupExample();
        WebsiteGroupExample.Criteria criteria = example.createCriteria();
        criteria.andGroupCodeEqualTo(groupCode).andEnableEqualTo(false);
        example.setOrderByClause("weight desc");
        return websiteGroupDAO.selectByExample(example);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == weightUtils) {
            weightUtils = new WeightUtils(redisIp, redisPassword, new WeightUtils.WeightQueueConfig() {
                @Override
                public Map<String, Integer> getWeights(String groupCode) {
                    List<WebsiteGroup> groups = queryByGroupCode(groupCode);
                    Map<String, Integer> map = new HashMap<>();
                    groups.stream().filter(s -> s.getEnable() && s.getWeight() > 0).forEach(e -> {
                        map.put(e.getWebsiteName(), e.getWeight());
                    });
                    if (map.isEmpty()) {
                        groups.stream().forEach(e -> {
                            map.put(e.getWebsiteName(), 1);
                        });
                    }
                    return map;
                }

                @Override
                public int getQueueSize() {
                    return 50;
                }
            });
        }
    }
}
