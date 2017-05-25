package com.datatrees.rawdatacentral.share;

import com.datatrees.rawdatacentral.domain.result.DirectiveResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis操作
 * Created by zhouxinghai on 2017/5/23
 */
public interface RedisService {

    /**
     * 是否包含key
     * @param key
     * @return
     */
    boolean hasKey(String key);

    /**
     * 删除key
     * @param key
     * @return
     */
    boolean deleteKey(String key);

    /**
     * redis加锁
     * @param key 
     * @param timeout
     * @param unit
     * @return
     */
    boolean lock(String key, long timeout, TimeUnit unit);

    /**
     * redis解锁
     * @param key
     * @return
     */
    boolean unlock(String key);

    /**
     * 获取
     * @param key
     * @return
     */
    String getString(String key);

    /**
     * 获取,有超时时间
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    String getString(String key, long timeout, TimeUnit timeUnit);

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

    /**
     * 保存交互指令
     * 保存到list,指令依次从最后一个读取
     * @param result
     * @return
     */
    boolean saveDirectiveResult(DirectiveResult result);

    /**
     * 获取还未执行的最后一条指令
     * @return
     */
    <T> DirectiveResult<T> getNextDirectiveResult(String key);

    /**
     * 获取,有超时时间
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    <T> DirectiveResult getDirectiveResult(String key, long timeout, TimeUnit timeUnit);

}
