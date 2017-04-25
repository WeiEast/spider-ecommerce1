package com.datatrees.rawdatacentral.submitter.common;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;

public class RedisKeyUtils {
    private static final Logger log = LoggerFactory.getLogger(RedisKeyUtils.class);
    private static String REDIS_PREFIX = PropertiesConfiguration.getInstance().get("core.redis.redis.prefix", "rawdata_");

    public static String genRedisKey(int userId, int taskId, String resultClass) {
        if (StringUtils.isEmpty(resultClass)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(REDIS_PREFIX).append(userId).append(SubmitConstant.REDIS_KEY_SEPARATOR).append(taskId).append(SubmitConstant.REDIS_KEY_SEPARATOR)
                .append(resultClass);
        String redisKey = sb.toString();
        log.debug("generate redis key " + redisKey);
        return redisKey;
    }

    public static String genCollectorMessageRedisKey(CollectorMessage collectorMessage) {
        if (collectorMessage == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(REDIS_PREFIX).append(collectorMessage.getUserId()).append(SubmitConstant.REDIS_KEY_SEPARATOR).append(collectorMessage.getMsgId());
        String redisKey = sb.toString();
        return redisKey;
    }
}
