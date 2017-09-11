package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.dao.WebsiteGroupDAO;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.exception.CommonException;
import com.datatrees.rawdatacentral.domain.model.WebsiteGroup;
import com.datatrees.rawdatacentral.domain.model.example.WebsiteGroupExample;
import com.datatrees.rawdatacentral.service.WebsiteGroupService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebsiteGroupServiceImpl implements WebsiteGroupService {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteGroupServiceImpl.class);
    @Resource
    private WebsiteGroupDAO        websiteGroupDAO;
    @Resource
    private WebsiteOperatorService websiteOperatorService;
    @Resource
    private RedisService           redisService;

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
        return queryByGroupCode(groupCode);
    }

    @Override
    public void updateCacheByGroupCode(String groupCode) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is null");
        redisService.deleteKey(RedisKeyPrefixEnum.MAX_WEIGHT_OPERATOR.getRedisKey(groupCode));
        List<WebsiteGroup> list = queryByGroupCode(groupCode);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        WebsiteGroup maxWeight = null;
        for (WebsiteGroup group : list) {
            if (null == maxWeight) {
                maxWeight = group;
            } else if (group.getWeight() > maxWeight.getWeight()) {
                maxWeight = group;
            }
        }
        if (null != maxWeight && maxWeight.getWeight() > 0) {
            redisService.saveString(RedisKeyPrefixEnum.MAX_WEIGHT_OPERATOR, groupCode, maxWeight.getWebsiteName());
        }
        redisService.deleteKey(RedisKeyPrefixEnum.ALL_OPERATOR_CONFIG.getRedisKey());
    }

    @Override
    public void updateCache() {
        for (GroupEnum group : GroupEnum.values()) {
            updateCacheByGroupCode(group.getGroupCode());
        }
        redisService.deleteKey(RedisKeyPrefixEnum.ALL_OPERATOR_CONFIG.getRedisKey());
    }
}
