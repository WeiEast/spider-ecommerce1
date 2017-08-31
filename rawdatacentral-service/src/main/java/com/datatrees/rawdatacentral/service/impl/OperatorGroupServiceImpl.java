package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.dao.OperatorGroupDAO;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.exception.CommonException;
import com.datatrees.rawdatacentral.domain.model.OperatorGroup;
import com.datatrees.rawdatacentral.domain.model.example.OperatorGroupExample;
import com.datatrees.rawdatacentral.domain.operator.GroupEnum;
import com.datatrees.rawdatacentral.service.OperatorGroupService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OperatorGroupServiceImpl implements OperatorGroupService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorGroupServiceImpl.class);
    @Resource
    private OperatorGroupDAO       operatorGroupDAO;
    @Resource
    private WebsiteOperatorService websiteOperatorService;
    @Resource
    private RedisService           redisService;

    @Override
    public List<OperatorGroup> queryByGroupCode(String groupCode) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is blank");
        OperatorGroupExample example = new OperatorGroupExample();
        example.createCriteria().andGroupCodeEqualTo(groupCode);
        return operatorGroupDAO.selectByExample(example);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        OperatorGroupExample example = new OperatorGroupExample();
        example.createCriteria().andGroupCodeEqualTo(groupCode);
        operatorGroupDAO.deleteByExample(example);
        updateCacheByGroupCode(groupCode);
    }

    @Override
    public OperatorGroup queryMaxWeightWebsite(String groupCode) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is blank");
        return operatorGroupDAO.queryMaxWeightWebsite(groupCode);
    }

    @Override
    public List<OperatorGroup> configGroup(String groupCode, Map<String, Integer> config) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is null");
        if (null == config || config.isEmpty()) {
            throw new CommonException("config is empty");
        }
        deleteByGroupCode(groupCode);
        for (Map.Entry<String, Integer> entry : config.entrySet()) {
            OperatorGroup operatorGroup = new OperatorGroup();
            operatorGroup.setGroupCode(groupCode);
            operatorGroup.setWebsiteName(entry.getKey());
            operatorGroup.setWeight(entry.getValue());
            operatorGroup.setWebsiteTitle(websiteOperatorService.getByWebsiteName(entry.getKey()).getWebsiteTitle());
            operatorGroupDAO.insertSelective(operatorGroup);
        }
        updateCacheByGroupCode(groupCode);
        return queryByGroupCode(groupCode);
    }

    @Override
    public void updateCacheByGroupCode(String groupCode) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is null");
        redisService.deleteKey(RedisKeyPrefixEnum.MAX_WEIGHT_OPERATOR.getRedisKey(groupCode));
        List<OperatorGroup> list = queryByGroupCode(groupCode);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        OperatorGroup maxWeight = null;
        for (OperatorGroup group : list) {
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
