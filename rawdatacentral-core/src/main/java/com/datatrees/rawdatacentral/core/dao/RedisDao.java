package com.datatrees.rawdatacentral.core.dao;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;

public interface RedisDao {
    public boolean saveListString(final String key, final List<String> valueList);

    public boolean saveString2List(final String key, final String value);

    public String getStringFromList(final String key);

    public boolean pushMessage(String submitRedisKey, String messageType);

    public boolean pushMessage(String submitRedisKey, String messageType, int ttlSeconds);

    public String pullResult(String obtainRedisKey);

    public RedisTemplate<String, String> getRedisTemplate();

    public void deleteKey(String key);
}
