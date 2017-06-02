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
    public boolean hasKey(String key);

    /**
     * 删除key
     * @param key
     * @return
     */
    public boolean deleteKey(String key);

    /**
     * redis加锁
     * @param key 
     * @param timeout
     * @param unit
     * @return
     */
    public boolean lock(String key, long timeout, TimeUnit unit);

    /**
     * redis解锁
     * @param key
     * @return
     */
    public boolean unlock(String key);

    /**
     * 获取
     * @param key
     * @return
     */
    public String getString(String key);

    /**
     * 获取,有超时时间
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    public String getString(String key, long timeout, TimeUnit timeUnit);

    /**
     * 从list取最后一个值
     * @param key 
     * @return
     */
    public String rightPop(String key);

    /**
     *  从list取最后一个值
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public String rightPop(String key, long timeout, TimeUnit unit);

    /**
     * 保存
     * @param key
     * @param value
     * @return
     */
    public boolean saveString(String key, Object value);

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
     * 保存到list
     * @param key
     * @param value
     * @param timeout 过期时间
     * @param unit 过期时间单位
     * @return
     */
    public boolean saveToList(String key, String value, long timeout, TimeUnit unit);

    /**
     * 保存
     * @param key
     * @param value
     * @return
     */
    public boolean saveListString(String key, List<String> value);

    /**
     * 从redis取app端交互的信息
     * @param taskId 任务ID
     * @return
     */
    public String getResultFromApp(Object taskId);

    /**
     * 保存交互指令
     * 保存到指令池和单条指令
     * @param result
     * @return
     */
    public String saveDirectiveResult(DirectiveResult result);

    /**
     * 保存交互指令
     * 保存到指令池和单条指令
     * @param directiveId 指令ID,作为redisKey
     * @param result
     * @return
     */
    public String saveDirectiveResult(String directiveId, DirectiveResult result);

    /**
     * 获取还未执行的最后一条指令
     * @param groupKey 指令池key
     * @return
     */
    public <T> DirectiveResult<T> getNextDirectiveResult(String groupKey);

    /**
     * 获取还未执行的最后一条指令
     * @param groupKey 指令池key
     * @return
     */
    public <T> DirectiveResult<T> getNextDirectiveResult(String groupKey, long timeout, TimeUnit timeUnit);

    /**
     * 获取,有超时时间
     * @param directiveKey 单条指令key
     * @param timeout
     * @param timeUnit
     * @return
     */
    public <T> DirectiveResult getDirectiveResult(String directiveKey, long timeout, TimeUnit timeUnit);

    /**
     * 创建指令ID
     * @param appName 项目名称
     * @return 指令ID
     */
    public String createDirectiveId(String appName);

    /**
     * 创建指令ID
     * @return 指令ID
     */
    public String createDirectiveId();

}
