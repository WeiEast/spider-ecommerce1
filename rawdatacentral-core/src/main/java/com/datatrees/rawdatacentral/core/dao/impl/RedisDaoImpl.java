package com.datatrees.rawdatacentral.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.core.common.Constants;
import com.datatrees.rawdatacentral.core.dao.RedisDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisDaoImpl implements RedisDao {

    private static final Logger logger = LoggerFactory.getLogger(RedisDaoImpl.class);


    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean saveString2List(final String key, final String value) {
        List list = new ArrayList<String>();
        list.add(value);
        return saveListString(key, list);
    }

    @Override
    public boolean saveListString(final String key, final List<String> valueList) {
        Long result = redisTemplate.opsForList().rightPushAll(key, valueList.toArray(new String[valueList.size()]));
        redisTemplate.expire(key, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
        return result != null ? true : false;
    }

    @Override
    public String getStringFromList(final String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public String pullResult(final String obtainRedisKey) {
        try {
            return redisTemplate.opsForValue().get(obtainRedisKey);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean pushMessage(final String submitRedisKey, final String messageType) {
        return this.pushMessage(submitRedisKey, messageType, Constants.REDIS_KEY_TIMEOUT);
    }

    @Override
    public boolean pushMessage(String submitRedisKey, String messageType, int ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(submitRedisKey, messageType);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        redisTemplate.expire(submitRedisKey, ttlSeconds, TimeUnit.SECONDS);
        return true;
    }

    /**
     * @return the redisTemplate
     */
    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see RedisDao#deleteKey(java.lang.String)
     */
    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }


}
