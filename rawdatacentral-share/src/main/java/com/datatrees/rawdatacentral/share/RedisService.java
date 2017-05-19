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
    boolean hasKey(String key);

    /**
     * 获取
     * @param key
     * @return
     */
    String getString(String key);

    /**
     * 从list取最后一个值
     * @param key 
     * @return
     */
    String rightPop(String key);

    /**
     * 保存
     * @param key
     * @param value
     * @return
     */
    boolean saveString(String key, Object value);

    /**
     * 保存
     * @param key
     * @param value
     * @param timeout 过期时间
     * @param unit 过期时间单位
     * @return
     */
    boolean saveString(String key, String value, long timeout, TimeUnit unit);

    /**
     * 保存到list
     * @param key
     * @param value
     * @param timeout 过期时间
     * @param unit 过期时间单位
     * @return
     */
    boolean saveToList(String key, String value, long timeout, TimeUnit unit);

    /**
     * 保存
     * @param key
     * @param value
     * @return
     */
    boolean saveListString(String key, List<String> value);

    /**
     * 从redis取app端交互的信息
     * @param taskId 任务ID
     * @return
     */
    String getResultFromApp(Object taskId);

}
