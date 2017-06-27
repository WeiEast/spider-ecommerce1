package com.datatrees.rawdatacentral.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.domain.constant.CrawlConstant;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.rawdatacentral.share.RedisService;

@Service
public class ReidsServiceImpl implements RedisService {
    private static final Logger logger = LoggerFactory.getLogger(ReidsServiceImpl.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 默认超时时间(单位:秒),默认2分钟
     */
    @Value("${rawdatacentral.redisKey.timeout:120}")
    private long                defaultTimeOut;

    @Override
    public boolean hasKey(String key) {
        if (StringUtils.isBlank(key)) {
            logger.warn("hasKey error,invalid param key is blank key={}", key);
            return false;
        }
        try {
            return stringRedisTemplate.hasKey(key);
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
            if (stringRedisTemplate.hasKey(key)) {
                stringRedisTemplate.delete(key);
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
            boolean b = stringRedisTemplate.opsForValue().setIfAbsent(key, key);
            if (!b) {
                logger.warn("lock fail key={},timeout={},unit={}", key, timeout, unit);
                return false;
            }
            stringRedisTemplate.expire(key, timeout, unit);
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
            return stringRedisTemplate.opsForValue().get(key);
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
        long sleeptime = 300;
        try {
            logger.info("getString wait {}s, key={}", timeUnit.toSeconds(timeout), key);
            do {
                if (stringRedisTemplate.hasKey(key)) {
                    String value = stringRedisTemplate.opsForValue().get(key);
                    logger.info("getString success,useTime={}, key={}", DateUtils.getUsedTime(startTime), key);
                    return value;
                }
                TimeUnit.MILLISECONDS.sleep(sleeptime);
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
            return stringRedisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            logger.error("rightPop error key={}", key, e);
            return null;
        }
    }

    @Override
    public String rightPop(String key, long timeout, TimeUnit unit) {
        if (!hasKey(key)) {
            return null;
        }
        long startTime = System.currentTimeMillis();
        long endTime = startTime + unit.toMillis(timeout);
        long sleeptime = 300;
        try {
            logger.info("rightPop wait {}s, key={}", unit.toSeconds(timeout), key);
            do {
                if (stringRedisTemplate.hasKey(key)) {
                    String value = stringRedisTemplate.opsForList().rightPop(key);
                    if (StringUtils.isNoneBlank(value)) {
                        logger.info("getString success,useTime={}, key={}", DateUtils.getUsedTime(startTime), key);
                        return value;
                    }
                }
                TimeUnit.MILLISECONDS.sleep(sleeptime);
            } while (System.currentTimeMillis() <= endTime);
            logger.warn("rightPop fail,useTime={}, key={}", DateUtils.getUsedTime(startTime), key);
        } catch (Exception e) {
            logger.error("rightPop error,useTime={}, key={}", DateUtils.getUsedTime(startTime), key, e);
        }
        return null;
    }

    @Override
    public boolean saveString(String key, Object value) {
        if (StringUtils.isBlank(key) || null == value) {
            throw new RuntimeException("saveString invalid param key or value");
        }
        try {
            stringRedisTemplate.opsForValue().set(key, String.valueOf(value), defaultTimeOut, TimeUnit.SECONDS);
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
            stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
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
            stringRedisTemplate.opsForList().rightPushAll(key, value);
            stringRedisTemplate.expire(key, timeout, unit);
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
            if (!CollectionUtils.isEmpty(valueList)) {
                stringRedisTemplate.opsForList().rightPushAll(key, valueList.toArray(new String[valueList.size()]));
                stringRedisTemplate.expire(key, defaultTimeOut, TimeUnit.SECONDS);
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
        saveToList(result.getGroupKey(), json, defaultTimeOut, TimeUnit.SECONDS);
        saveString(result.getDirectiveKey(), json, defaultTimeOut, TimeUnit.SECONDS);
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
        saveString(directiveId, json, defaultTimeOut, TimeUnit.SECONDS);
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
    public <T> DirectiveResult<T> getNextDirectiveResult(String groupKey, long timeout, TimeUnit timeUnit) {
        if (StringUtils.isBlank(groupKey)) {
            throw new RuntimeException("getDirectiveResult error key is blank");
        }
        String value = rightPop(groupKey, timeout, timeUnit);
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
        if (!stringRedisTemplate.hasKey(appName)) {
            stringRedisTemplate.opsForValue().setIfAbsent(key, "1");
        }
        return appName + "_" + stringRedisTemplate.opsForValue().increment(key, 1);
    }

    @Override
    public String createDirectiveId() {
        return createDirectiveId(DirectiveResult.getAppName());
    }

    @Override
    public void cache(String key, Object value, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(key) || null == value) {
            logger.error("invalid param key={} or value={}", key, value);
            throw new RuntimeException("invalid param key or value");
        }
        String json = JSON.toJSONString(value);
        saveString(key, json, timeout, unit);
        logger.info("cache success key={}", key);
    }

    @Override
    public <T> T getCache(String key, Class<T> cls) {
        String json = getString(key);
        if (StringUtils.isNoneBlank(json)) {
            T result = JSON.parseObject(json, cls);
            logger.info("getCache success key={}", key);
            return result;
        }
        return null;
    }

}
