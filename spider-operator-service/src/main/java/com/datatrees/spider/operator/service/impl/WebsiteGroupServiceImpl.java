package com.datatrees.spider.operator.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.WeightUtils;
import com.datatrees.spider.share.domain.GroupEnum;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.exception.CommonException;
import com.datatrees.spider.operator.dao.WebsiteGroupDAO;
import com.datatrees.spider.operator.domain.model.WebsiteGroup;
import com.datatrees.spider.operator.domain.model.example.WebsiteGroupExample;
import com.datatrees.spider.operator.service.WebsiteGroupService;
import com.datatrees.spider.operator.service.WebsiteOperatorService;
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

    private              Random                 random = new Random();

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
        updateCacheByGroupCode(groupCode);
    }

    @Override
    public WebsiteGroup queryMaxWeightWebsite(String groupCode) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is blank");
        return websiteGroupDAO.queryMaxWeightWebsite(groupCode);
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
        updateCacheByGroupCode(groupCode);
        clearOperatorQueueByGroupCode(groupCode);
        return queryByGroupCode(groupCode);
    }

    @Override
    public void updateCacheByGroupCode(String groupCode) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is null");
        List<WebsiteGroup> list = queryByGroupCode(groupCode);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        WebsiteGroup maxWeight = null;
        if (list.size() == 1) {
            maxWeight = list.get(0);
        } else {
            List<WebsiteGroup> enables = list.stream().filter(group -> group.getEnable()).sorted((a, b) -> a.getWeight().compareTo(b.getWeight()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(enables)) {
                maxWeight = enables.get(enables.size() - 1);
            } else {
                maxWeight = list.get(random.nextInt(list.size()));
                logger.info("random selecet website groupCode={},websiteName={}", groupCode, maxWeight.getWebsiteName());
            }
        }
        RedisUtils.set(RedisKeyPrefixEnum.MAX_WEIGHT_OPERATOR.getRedisKey(groupCode), maxWeight.getWebsiteName());
    }

    @Override
    public void updateCache() {
        for (GroupEnum group : GroupEnum.values()) {
            updateCacheByGroupCode(group.getGroupCode());
        }
        logger.info("update operator group config success");
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
        criteria.andGroupCodeEqualTo(groupCode).andEnableEqualTo(true);
        return websiteGroupDAO.countByExample(example);
    }

    @Override
    public List<WebsiteGroup> queryEnable(String groupCode) {
        WebsiteGroupExample example = new WebsiteGroupExample();
        WebsiteGroupExample.Criteria criteria = example.createCriteria();
        criteria.andGroupCodeEqualTo(groupCode).andEnableEqualTo(true);
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
