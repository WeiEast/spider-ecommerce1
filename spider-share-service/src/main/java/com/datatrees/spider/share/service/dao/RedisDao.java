package com.datatrees.spider.share.service.dao;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;

@Deprecated
public interface RedisDao {

    boolean saveListString(final String key, final List<String> valueList);

    boolean saveString2List(final String key, final String value);

    String getStringFromList(final String key);

    boolean pushMessage(String submitRedisKey, String messageType);

    boolean pushMessage(String submitRedisKey, String messageType, int ttlSeconds);

    String pullResult(String obtainRedisKey);

    RedisTemplate<String, String> getRedisTemplate();

    void deleteKey(String key);

    /**
     * key的值+1,并返回增加后的值
     * @param key 如果没有,设置为1
     * @return
     */
    Long increaseAndGet(String key);
}
