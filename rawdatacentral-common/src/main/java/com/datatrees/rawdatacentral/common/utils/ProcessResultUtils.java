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

    public static long setEndTime(long processId, long start, TimeUnit unit, int timeout) {
        long end = unit.toMillis(timeout) + start;
        return setEndTime(processId, start, end);
    }

    public static long setEndTime(long processId, long start, long end) {
        int timeout = (int) (TimeUnit.MILLISECONDS.toSeconds(end - start) + TimeUnit.MINUTES.toSeconds(1));
        RedisUtils.set(RedisKeyPrefixEnum.PROCESS_START_TIME.getRedisKey(processId), String.valueOf(start), timeout);
        RedisUtils.set(RedisKeyPrefixEnum.PROCESS_END_TIME.getRedisKey(processId), String.valueOf(end), timeout);
        logger.info("set end time for processId : {},start : {} ,end :{}", processId, DateUtils.formatYmdhms(start), DateUtils.formatYmdhms(end));
        return end;

    }

    public static boolean isTimeOut(long processId) {
        String startStr = RedisUtils.get(RedisKeyPrefixEnum.PROCESS_START_TIME.getRedisKey(processId));
        String endStr = RedisUtils.get(RedisKeyPrefixEnum.PROCESS_END_TIME.getRedisKey(processId));
        if (StringUtils.isAnyBlank(startStr, endStr)) {
            logger.info("process is time out,processId={},start={},end={}", processId, startStr, endStr);
            return true;
        }
        if (Long.valueOf(endStr) < System.currentTimeMillis()) {
            logger.info("process is time out,processId={},start={},end={}", processId, startStr, endStr);
            return true;
        }
        return false;
    }

}
