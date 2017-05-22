package com.datatrees.rawdatacentral.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.StringUtils;
import com.datatrees.rawdatacentral.core.common.Constants;
import com.datatrees.rawdatacentral.domain.constant.CrawlConstant;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
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
        if (StringUtils.isBlank(key)) {
            logger.warn("invalid param key is blank key={}", key);
            return false;
        }
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("redis hasKey error key={}", key, e);
            return false;
        }
    }

    @Override
    public String getString(String key) {
        if (!hasKey(key)) {
            return null;
        }
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("redis getString error key={}", key, e);
            return null;
        }
    }

    @Override
    public String rightPop(String key) {
        if (!hasKey(key)) {
            return null;
        }
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            logger.error("redis getString error key={}", key, e);
            return null;
        }
    }

    @Override
    public boolean saveString(String key, Object value) {
        if (StringUtils.isBlank(key) || null == value) {
            throw new RuntimeException("invalid param key or value");
        }
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(value), Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            logger.error("save to redis error key={}", key, e);
            return false;
        }
    }

    @Override
    public boolean saveString(String key, String value, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(key) || null == value) {
            throw new RuntimeException("invalid param key or value");
        }
        if (timeout <= 0) {
            throw new RuntimeException("invalid param timeout");
        }
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            logger.error("save to redis error key={}", key, e);
            return false;
        }
    }

    @Override
    public boolean saveToList(String key, String value, long timeout, TimeUnit unit) {
        return false;
    }

    @Override
    public boolean saveListString(String key, List<String> valueList) {
        try {
            if (CollectionUtils.isNotEmpty(valueList)) {
                redisTemplate.opsForList().rightPushAll(key, valueList.toArray(new String[valueList.size()]));
                redisTemplate.expire(key, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("save to redis error key={}", key, e);
            return false;
        }
    }

    @Override
    public String getResultFromApp(Object taskId) {
        if (null == taskId) {
            throw new RuntimeException("getResultFromApp error taskId is null");
        }
        String key = CrawlConstant.VERIFY_RESULT_PREFIX + String.valueOf(taskId);
        return getString(key);
    }

    @Override
    public boolean saveDirectiveResult(DirectiveResult result) {
        if (null == result) {
            throw new RuntimeException("saveDirectiveResult error param is null");
        }
        return saveString(result.getRedisKey(), JSON.toJSONString(result), Constants.REDIS_KEY_TIMEOUT,
            TimeUnit.SECONDS);
    }

    @Override
    public DirectiveResult getDirectiveResult(String key) {
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("getDirectiveResult error key is blank");
        }
        String value = getString(key);
        if (StringUtils.isNotBlank(value)) {
            return JSON.parseObject(value, DirectiveResult.class);
        }
        return null;
    }

}
