package com.datatrees.rawdatacentral.submitter.common;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.spider.share.service.constants.SubmitConstant;
import com.datatrees.spider.share.domain.CollectorMessage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisKeyUtils {

    private static final Logger log          = LoggerFactory.getLogger(RedisKeyUtils.class);

    private static       String REDIS_PREFIX = PropertiesConfiguration.getInstance().get("core.redis.redis.prefix", "raw_res_");

    public static String genRedisKey(long taskId, long taskLogId, String resultClass) {
        if (StringUtils.isEmpty(resultClass)) {
            return null;
        }
        StringBuilder sb = new StringBuilder(resultClass).append(".").append(taskLogId).append(".").append(taskId);
        String redisKey = sb.toString();
        log.debug("generate redis key " + redisKey);
        return redisKey;
    }

    public static String genCollectorMessageRedisKey(CollectorMessage collectorMessage) {
        if (collectorMessage == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(REDIS_PREFIX).append(collectorMessage.getTaskId()).append(SubmitConstant.REDIS_KEY_SEPARATOR).append(collectorMessage.getMsgId());
        String redisKey = sb.toString();
        return redisKey;
    }
}
