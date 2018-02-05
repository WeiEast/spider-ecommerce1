package com.datatrees.rawdatacentral.common.utils;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

public class ProcessResultUtils {

    private static final org.slf4j.Logger logger         = LoggerFactory.getLogger(ProcessResultUtils.class);

    private static final String           KEY_PROCESS_ID = "process.id";

    public static Long createProcessId() {
        return RedisUtils.incr(KEY_PROCESS_ID);
    }

    public static <T> ProcessResult<T> createAndSaveProcessId() {
        ProcessResult result = new ProcessResult();
        result.processing(createProcessId());
        saveProcessResult(result);
        return result;
    }

    public static <T> ProcessResult<T> queryProcessResult(long processId) {
        return JSON.parseObject(RedisUtils.get(getRedisKey(processId)), new TypeReference<ProcessResult<T>>() {});
    }

    public static void saveProcessResult(ProcessResult result) {
        RedisUtils.set(getRedisKey(result.getProcessId()), JSON.toJSONString(result), (int) TimeUnit.HOURS.toSeconds(1));
    }

    public static String getRedisKey(Long processId) {
        return new StringBuilder("process.result.").append(processId).toString();
    }

    public static void setProcessExpire(long taskId, Long processId, long timeout, TimeUnit unit) {
        String redisKey = RedisKeyPrefixEnum.PROCESS_EXPIRE.getRedisKey(processId);
        long end = System.currentTimeMillis() + unit.toMillis(timeout);
        RedisUtils.setnx(redisKey, String.valueOf(end), (int) unit.toSeconds(timeout));
        logger.info("set process expire time,taskId={},processId={},end={}", taskId, processId, DateUtils.formatYmdhms(end));
    }

    public static boolean processExpire(long taskId, Long processId) {
        String redisKey = RedisKeyPrefixEnum.PROCESS_EXPIRE.getRedisKey(processId);
        String endStr = RedisUtils.get(redisKey);
        boolean expire = StringUtils.isBlank(endStr) || System.currentTimeMillis() < Long.valueOf(endStr);
        if (expire) {
            logger.warn("processId is timeout,taskId={},processId={}", taskId, processId);
        }
        return expire;
    }

}
