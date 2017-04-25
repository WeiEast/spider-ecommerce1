package com.datatrees.rawdatacentral.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.core.dao.RedisDao;
import com.datatrees.rawdatacentral.core.service.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReidsServiceImpl implements RedisService {
    private static final Logger logger = LoggerFactory.getLogger(ReidsServiceImpl.class);


    @Resource
    private RedisDao redisDao;

    @Override
    public boolean saveString(String key, String value) {
        try {
            return redisDao.pushMessage(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean saveListString(String key, List<String> valueList) {
        try {
            if (CollectionUtils.isNotEmpty(valueList)) {
                return redisDao.saveListString(key, valueList);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

}
