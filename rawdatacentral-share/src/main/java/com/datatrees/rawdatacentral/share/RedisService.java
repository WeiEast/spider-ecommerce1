package com.datatrees.rawdatacentral.share;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis操作
 */
public interface RedisService {

    /**
     * 是否包含key
     * @param key
     * @return
     */
    public boolean hasKey(String key);

    /**
     * 获取
     * @param key
     * @return
     */
    public String getString(String key);

    /**
     * 保存
     * @param key
     * @param value
     * @return
     */
    public boolean saveString(String key, String value);

    /**
     * 保存
     * @param key
     * @param value
     * @param timeout 过期时间
     * @param unit 过期时间单位
     * @return
     */
    public boolean saveString(String key, String value, long timeout, TimeUnit unit);

    /**
     * 保存
     * @param key
     * @param value
     * @return
     */
    public boolean saveListString(String key, List<String> value);
}
