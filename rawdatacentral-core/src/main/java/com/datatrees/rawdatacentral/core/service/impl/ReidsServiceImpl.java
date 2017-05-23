package com.datatrees.rawdatacentral.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.core.common.Constants;
import com.datatrees.rawdatacentral.domain.constant.CrawlConstant;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    public boolean deleteKey(String key) {
        if (StringUtils.isBlank(key)) {
            logger.warn("invalid param key is blank key={}", key);
            return false;
        }
        try {
            if (redisTemplate.hasKey(key)) {
                redisTemplate.delete(key);
                logger.info("deleteKey success key={}", key);
            }
            return true;
        } catch (Exception e) {
            logger.error("deleteKey error key={}", key, e);
            return false;
        }
    }

    @Override
    public boolean lock(String key, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(key)) {
            logger.warn("invalid param key is blank key={}", key);
            return false;
        }
        try {
            boolean b = redisTemplate.opsForValue().setIfAbsent(key, key);
            if (!b) {
                logger.warn("lock fail key={},timeout={},unit={}", key, timeout, unit);
                return false;
            }
            redisTemplate.expire(key, timeout, unit);
            logger.info("lock success key={},timeout={},unit={}", key, timeout, unit);
            return true;
        } catch (Exception e) {
            logger.error("lock error key={},timeout={},unit={}", key, timeout, unit, e);
            return false;
        }
    }

    @Override
    public boolean unlock(String key, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(key)) {
            logger.warn("invalid param key is blank key={}", key);
            return false;
        }
        boolean b = deleteKey(key);
        logger.info("unlock {} key={},timeout={},unit={}", b ? "success" : "fail", key, timeout, unit);
        return b;
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
    public String getString(String key, long timeout, TimeUnit timeUnit) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeUnit.toMillis(timeout);
        long sleeptime = 500;
        if (timeUnit.toMillis(timeout) <= 500) {
            sleeptime = timeUnit.toMillis(timeout);
        }
        try {
            do {
                TimeUnit.MILLISECONDS.sleep(sleeptime);
                if (redisTemplate.hasKey(key)) {
                    String value = redisTemplate.opsForValue().get(key);
                    logger.info("get data key={},useTime={}ms", key, System.currentTimeMillis() - startTime);
                    return value;
                }
            } while (System.currentTimeMillis() <= endTime);
        } catch (Exception e) {
            logger.error("redis error key={}", key);
        }
        return null;
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
            logger.info("save to redis success key={}", key);
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
        try {
            if (StringUtils.isAnyBlank(key, value)) {
                throw new RuntimeException("invalid param key or value");
            }
            redisTemplate.opsForList().rightPushAll(key, value);
            redisTemplate.expire(key, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
            logger.info("save to redis success key={}", key);
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
        return saveToList(result.getSendKey(), JSON.toJSONString(result), Constants.REDIS_KEY_TIMEOUT,
            TimeUnit.SECONDS);
    }

    @Override
    public DirectiveResult getNextDirectiveResult(String key) {
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("getDirectiveResult error key is blank");
        }
        String value = rightPop(key);
        if (StringUtils.isNotBlank(value)) {
            return JSON.parseObject(value, DirectiveResult.class);
        }
        return null;
    }

    @Override
    public DirectiveResult getDirectiveResult(String key, long timeout, TimeUnit timeUnit) {
        String result = getString(key, timeout, timeUnit);
        if (StringUtils.isNoneBlank(result)) {
            return JSON.parseObject(result, DirectiveResult.class);
        }
        return null;
    }

}
