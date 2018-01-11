package com.datatrees.rawdatacentral.common.utils;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;

public class ProcessResultUtils {

    private static final String KEY_PROCESS_ID = "process.id";

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

}
