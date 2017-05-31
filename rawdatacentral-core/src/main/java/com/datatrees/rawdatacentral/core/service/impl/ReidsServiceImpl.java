package com.datatrees.rawdatacentral.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.common.utils.DateUtils;
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
            logger.warn("hasKey error,invalid param key is blank key={}", key);
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
            logger.warn("deleteKey fail,invalid param key is blank key={}", key);
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
            logger.warn("lodk fail,invalid param key is blank key={}", key);
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
    public boolean unlock(String key) {
        if (StringUtils.isBlank(key)) {
            logger.warn("unlock fail, invalid param key is blank key={}", key);
            return false;
        }
        boolean b = deleteKey(key);
        logger.info("unlock {} key={}", b ? "success" : "fail", key);
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
            logger.error("getString error key={}", key, e);
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
            logger.info("getString wait {}s, key={}", timeUnit.toSeconds(timeout), key);
            do {
                TimeUnit.MILLISECONDS.sleep(sleeptime);
                if (redisTemplate.hasKey(key)) {
                    String value = redisTemplate.opsForValue().get(key);
                    logger.info("getString success,useTime={}, key={}", DateUtils.getUsedTime(startTime), key);
                    return value;
                }
            } while (System.currentTimeMillis() <= endTime);
            logger.warn("getString fail,useTime={}, key={}", DateUtils.getUsedTime(startTime), key);
        } catch (Exception e) {
            logger.error("getString error,useTime={}, key={}", DateUtils.getUsedTime(startTime), key, e);
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
            logger.error("rightPop error key={}", key, e);
            return null;
        }
    }

    @Override
    public boolean saveString(String key, Object value) {
        if (StringUtils.isBlank(key) || null == value) {
            throw new RuntimeException("saveString invalid param key or value");
        }
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(value), Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
            logger.info("saveString success key={}", key);
            return true;
        } catch (Exception e) {
            logger.error("saveString error key={}", key, e);
            return false;
        }
    }

    @Override
    public boolean saveString(String key, String value, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(key) || null == value) {
            throw new RuntimeException("saveString invalid param key or value");
        }
        if (timeout <= 0) {
            throw new RuntimeException("saveString invalid param timeout");
        }
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            logger.error("saveString error key={}", key, e);
            return false;
        }
    }

    @Override
    public boolean saveToList(String key, String value, long timeout, TimeUnit unit) {
        try {
            if (StringUtils.isAnyBlank(key, value)) {
                throw new RuntimeException("saveToList invalid param key or value");
            }
            redisTemplate.opsForList().rightPushAll(key, value);
            redisTemplate.expire(key, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
            logger.info("saveToList success key={}", key);
            return true;
        } catch (Exception e) {
            logger.error("saveToList error key={}", key, e);
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
            logger.info("saveListString success key={}", key);
            return true;
        } catch (Exception e) {
            logger.error("saveListString error key={}", key, e);
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
    public String saveDirectiveResult(DirectiveResult result) {
        if (null == result) {
            throw new RuntimeException("saveDirectiveResult error param is null");
        }
        String directiveId = createDirectiveId();
        result.setDirectiveId(directiveId);
        String json = JSON.toJSONString(result);
        //TODO加入事物控制
        saveToList(result.getGroupKey(), json, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
        saveString(result.getDirectiveKey(), json, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
        logger.info("saveDirectiveResult success,groupKey={},directiveKey={},directiveId={}", result.getGroupKey(),
            result.getDirectiveKey(), directiveId);
        return directiveId;
    }

    @Override
    public String saveDirectiveResult(String directiveId, DirectiveResult result) {
        if (null == result) {
            throw new RuntimeException("saveDirectiveResult error param is null");
        }
        result.setDirectiveId(directiveId);
        String json = JSON.toJSONString(result);
        //TODO加入事物控制
        //        saveToList(result.getGroupKey(), json, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
        saveString(directiveId, json, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
        logger.info("saveDirectiveResult success,directiveKey={},directiveId={}", directiveId, directiveId);
        return directiveId;
    }

    @Override
    public <T> DirectiveResult<T> getNextDirectiveResult(String groupKey) {
        if (StringUtils.isBlank(groupKey)) {
            throw new RuntimeException("getDirectiveResult error key is blank");
        }
        String value = rightPop(groupKey);
        if (StringUtils.isNotBlank(value)) {
            logger.info("getNextDirectiveResult success groupKey={}", groupKey);
            return JSON.parseObject(value, new TypeReference<DirectiveResult<T>>() {
            });
        }
        logger.info("getNextDirectiveResult fail groupKey={}", groupKey);
        return null;
    }

    @Override
    public <T> DirectiveResult<T> getDirectiveResult(String directiveKey, long timeout, TimeUnit timeUnit) {
        String value = getString(directiveKey, timeout, timeUnit);
        if (StringUtils.isNoneBlank(value)) {
            logger.info("getDirectiveResult success directiveKey={}", directiveKey);
            return JSON.parseObject(value, new TypeReference<DirectiveResult<T>>() {
            });
        }
        logger.info("getDirectiveResult fail directiveKey={}", directiveKey);
        return null;
    }

    @Override
    public String createDirectiveId(String appName) {
        if (StringUtils.isBlank(appName)) {
            throw new RuntimeException("createDirectiveId error appName is blank");
        }
        String key = "directive_id_" + appName;
        if (!redisTemplate.hasKey(appName)) {
            redisTemplate.opsForValue().setIfAbsent(key, "1");
        }
        return appName + "_" + redisTemplate.opsForValue().increment(key, 1);
    }

    @Override
    public String createDirectiveId() {
        return createDirectiveId(DirectiveResult.getAppName());
    }

}
