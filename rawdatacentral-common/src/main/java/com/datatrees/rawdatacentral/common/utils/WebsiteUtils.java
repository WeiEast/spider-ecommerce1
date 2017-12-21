package com.datatrees.rawdatacentral.common.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * website工具类
 */
public class WebsiteUtils {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteUtils.class);

    /**
     * 是否是运营商
     * @param websiteName
     * @return
     */
    public static boolean isOperator(String websiteName) {
        return StringUtils.containsAny(websiteName, "10086", "10000", "10010");
    }

    public static void updateWithTaskSuccess(String websiteName, long taskId, long timestamp) {
        Preconditions.checkNotNull(websiteName);
        String redisKey = getRedisKeyForWebsiteLastInfo(websiteName);
        String redisTimestamp = RedisUtils.hget(redisKey, AttributeKey.SUCCESS_TIMESTAMP);
        boolean update = StringUtils.isBlank(redisTimestamp) || timestamp > Long.valueOf(redisTimestamp);
        if (update) {
            RedisUtils.hset(redisKey, AttributeKey.SUCCESS_TIMESTAMP, String.valueOf(timestamp));
            RedisUtils.hset(redisKey, AttributeKey.SUCCESS_TASK_ID, String.valueOf(taskId));
            long successUserCount = RedisUtils.hincrBy(redisKey, AttributeKey.SUCCESS_USER_COUNT, 1);
            RedisUtils.hset(redisKey, AttributeKey.FAIL_USER_COUNT, "0");
            updateWebsiteDayList(websiteName, taskId, timestamp);
            logger.info("upate website last info with task success,websiteName={},taskId={},timestamp={},successUserCount={}", websiteName, taskId,
                    DateUtils.formatYmdhms(timestamp), successUserCount);
        }
    }

    public static void updateWithTaskFail(String websiteName, long taskId, long timestamp) {
        Preconditions.checkNotNull(websiteName);
        String redisKey = getRedisKeyForWebsiteLastInfo(websiteName);
        String redisTimestamp = RedisUtils.hget(redisKey, AttributeKey.FAIL_TIMESTAMP);
        boolean update = StringUtils.isBlank(redisTimestamp) || timestamp > Long.valueOf(redisTimestamp);
        if (update) {
            RedisUtils.hset(redisKey, AttributeKey.FAIL_TIMESTAMP, String.valueOf(timestamp));
            RedisUtils.hset(redisKey, AttributeKey.FAIL_TASK_ID, String.valueOf(taskId));
            long failUserCount = RedisUtils.hincrBy(redisKey, AttributeKey.FAIL_USER_COUNT, 1);
            RedisUtils.hset(redisKey, AttributeKey.SUCCESS_USER_COUNT, "0");
            updateWebsiteDayList(websiteName, taskId, timestamp);
            logger.info("upate website last info with task fail,websiteName={},taskId={},timestamp={},failUserCount={}", websiteName, taskId,
                    DateUtils.formatYmdhms(timestamp), failUserCount);
        }
    }

    public static void updateWarnTime(String websiteName, long timestamp) {
        Preconditions.checkNotNull(websiteName);
        String redisKey = getRedisKeyForWebsiteLastInfo(websiteName);
        String redisTimestamp = RedisUtils.hget(redisKey, AttributeKey.WARN_TIMESTAMP);
        boolean update = StringUtils.isBlank(redisTimestamp) || timestamp > Long.valueOf(redisTimestamp);
        if (update) {
            RedisUtils.hset(redisKey, AttributeKey.WARN_TIMESTAMP, String.valueOf(timestamp));
            logger.info("upate website last info with warnTimestamp,websiteName={},taskId={},timestamp={}", websiteName,
                    DateUtils.formatYmdhms(timestamp));
        }
    }

    public static long getWarnTimestamp(String websiteName) {
        Preconditions.checkNotNull(websiteName);
        String redisKey = getRedisKeyForWebsiteLastInfo(websiteName);
        String value = RedisUtils.hget(redisKey, AttributeKey.WARN_TIMESTAMP);
        if (StringUtils.isBlank(value)) {
            return 0L;
        }
        return Long.valueOf(value);
    }

    public static int getSuccessUserCount(String websiteName) {
        Preconditions.checkNotNull(websiteName);
        String redisKey = getRedisKeyForWebsiteLastInfo(websiteName);
        String value = RedisUtils.hget(redisKey, AttributeKey.SUCCESS_USER_COUNT);
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return Integer.valueOf(value);
    }

    public static int getFailUserCount(String websiteName) {
        Preconditions.checkNotNull(websiteName);
        String redisKey = getRedisKeyForWebsiteLastInfo(websiteName);
        String value = RedisUtils.hget(redisKey, AttributeKey.FAIL_USER_COUNT);
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return Integer.valueOf(value);
    }

    public static long getStatisticsTimestamp(String websiteName) {
        Preconditions.checkNotNull(websiteName);
        String redisKey = getRedisKeyForWebsiteLastInfo(websiteName);
        String value = RedisUtils.hget(redisKey, AttributeKey.STATISTICS_TIMESTAMP);
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return Long.valueOf(value);
    }

    public static long getSuccessTimestamp(String websiteName) {
        Preconditions.checkNotNull(websiteName);
        String redisKey = getRedisKeyForWebsiteLastInfo(websiteName);
        String value = RedisUtils.hget(redisKey, AttributeKey.SUCCESS_TIMESTAMP);
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return Long.valueOf(value);
    }

    public static Long getWebisteMonitorId(String websiteName, Long preId, Date orderDate) {
        return getWebisteMonitorId(websiteName, preId, DateUtils.formatYmd(orderDate));
    }

    public static Long getWebisteMonitorId(String websiteName, Long preId, String monitorDay) {
        String postfix = TaskUtils.getSassEnv(websiteName + "." + monitorDay);
        String redisKey = RedisKeyPrefixEnum.WEBSITE_MONITOR_ID.getRedisKey(postfix);
        String id = RedisUtils.get(redisKey);
        if (StringUtils.isNotBlank(id)) {
            return Long.valueOf(id);
        } else if (RedisUtils.setnx(redisKey, preId.toString(), RedisKeyPrefixEnum.WEBSITE_MONITOR_ID.toSeconds())) {
            return preId;
        } else {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                logger.error("getWebisteMonitorId error redisKey={}", redisKey, e);
                return Long.valueOf(RedisUtils.get(redisKey));
            }
            return Long.valueOf(RedisUtils.get(redisKey));
        }
    }

    public static void updateWebsiteDayList(String websiteName, Long taskId, long timestamp) {
        String postfix = TaskUtils.getSassEnv(DateUtils.formatYmd(new Date(timestamp)));
        String redisKey = RedisKeyPrefixEnum.WEBSITE_DAY_LIST.getRedisKey(postfix);
        RedisUtils.hset(redisKey, websiteName, taskId.toString());
    }

    public static void updateWithGroupSuccess(String groupCode, long taskId, long timestamp) {
        Preconditions.checkNotNull(groupCode);
        String redisKey = RedisKeyPrefixEnum.GROUP_LAST_INFO.getRedisKey(TaskUtils.getSassEnv(groupCode));
        String redisTimestamp = RedisUtils.hget(redisKey, AttributeKey.SUCCESS_TIMESTAMP);
        boolean update = StringUtils.isBlank(redisTimestamp) || timestamp > Long.valueOf(redisTimestamp);
        if (update) {
            RedisUtils.hset(redisKey, AttributeKey.SUCCESS_TIMESTAMP, String.valueOf(timestamp));
            RedisUtils.hset(redisKey, AttributeKey.SUCCESS_TASK_ID, String.valueOf(taskId));
            logger.info("upate group last info with task success,groupCode={},taskId={},timestamp={}", groupCode, taskId,
                    DateUtils.formatYmdhms(timestamp));
        }
    }

    public static long getGroupSuccessTimestamp(String groupCode) {
        Preconditions.checkNotNull(groupCode);
        String redisKey = RedisKeyPrefixEnum.GROUP_LAST_INFO.getRedisKey(TaskUtils.getSassEnv(groupCode));
        String value = RedisUtils.hget(redisKey, AttributeKey.SUCCESS_TIMESTAMP);
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return Long.valueOf(value);
    }

    public static String getRedisKeyForWebsiteLastInfo(String websiteName) {
        return RedisKeyPrefixEnum.WEBSITE_LAST_INFO.getRedisKey(TaskUtils.getSassEnv(websiteName));
    }

}
