package com.datatrees.rawdatacentral.core.service.impl;

import com.datatrees.common.util.StringUtils;
import com.datatrees.rawdatacentral.core.common.Constants;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ReidsServiceImpl implements RedisService {
    private static final Logger logger = LoggerFactory.getLogger(ReidsServiceImpl.class);

    @Resource
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public String getString(String key) {
        if (StringUtils.isBlank(key) || !redisTemplate.hasKey(key)) {
            logger.warn("invalid param key={}", key);
            return null;
        }
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean saveString(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            logger.error("save to redis error key={}", key, e);
            return false;
        }
    }

    @Override
    public boolean saveString(String key, String value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            logger.error("save to redis error key={}", key, e);
            return false;
        }
    }

    @Override
    public boolean saveListString(String key, List<String> valueList) {
        try {
            if (CollectionUtils.isNotEmpty(valueList)) {
                redisTemplate.opsForList().rightPushAll(key, valueList.toArray(new String[valueList.size()]));
                redisTemplate.expire(key, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
                return true;
            }
            return true;
        } catch (Exception e) {
            logger.error("save to redis error key={}", key, e);
            return false;
        }
    }

}
