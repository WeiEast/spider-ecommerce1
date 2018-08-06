package com.datatrees.spider.share.service.submitter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.common.utils.BackRedisUtils;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.domain.SubmitMessage;
import com.datatrees.spider.share.service.domain.ParentTask;
import com.datatrees.spider.share.service.domain.SubSeed;
import com.datatrees.spider.share.service.domain.SubTask;
import com.datatrees.spider.share.service.normalizers.SubmitNormalizerFactory;
import com.datatrees.spider.share.service.extra.SubTaskManager;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.service.util.RedisKeyUtils;
import com.datatrees.spider.share.service.FileStoreService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultSubmitProcessor implements SubmitProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSubmitProcessor.class);

    @Resource
    RedisService            redisService;

    @Resource
    FileStoreService        fileStoreService;

    @Resource
    SubmitNormalizerFactory submitNormalizerFactory;

    @Resource
    SubTaskManager          subTaskManager;

    @Override
    public boolean process(Object message) {
        logger.debug("start process submit task: " + message.toString());
        try {
            boolean flag = false;
            if (message != null && message instanceof SubmitMessage) {
                SubmitMessage submitMessage = (SubmitMessage) message;
                flag = this.saveToRedis(submitMessage);
                this.submitSubTask(submitMessage);
                fileStoreService.storeEviFile(submitMessage);
            }
            return flag;
        } catch (Exception e) {
            logger.error("unknown exception!", e);
            return false;
        }
    }

    private SubSeed getSubSeed(Object obj) {
        if (obj instanceof Map) {
            SubSeed subSeed = new SubSeed();
            subSeed.putAll((Map) obj);
            return subSeed;
        } else {
            logger.warn("error format subseed from:" + obj);
            return null;
        }
    }

    private void submitSubTask(long taskId, ParentTask parentTask, Object seed) {
        SubSeed subSeed = this.getSubSeed(seed);
        if (subSeed != null) {
            logger.info("submit subseed " + subSeed);
            subTaskManager.submitSubTask(new SubTask(taskId, parentTask, subSeed));
            if (BooleanUtils.isTrue(subSeed.getProxyShared())) {
                try {
                    Proxy parentProxy = parentTask.getProcessorContext().getProxy();
                    if (parentProxy != null) {
                        parentProxy.getShareCount().incrementAndGet();
                    }
                    subSeed.setProxy(parentProxy);
                } catch (Exception e) {
                    logger.error("proxy Shared error " + e.getMessage(), e);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void submitSubTask(SubmitMessage submitMessage) {
        Map<String, Object> extractResultMap = submitMessage.getExtractResultMap();
        ExtractMessage extractMessage = submitMessage.getExtractMessage();
        Long taskId = submitMessage.getExtractMessage().getTask().getProcessorContext().getLong(AttributeKey.TASK_ID);
        ParentTask parentTask = extractMessage.getTask();
        if (MapUtils.isNotEmpty(extractResultMap)) {
            for (Entry<String, Object> entry : extractResultMap.entrySet()) {
                if ("subSeed".equals(entry.getKey())) {
                    if (entry.getValue() instanceof Collection) {
                        for (Object obj : (Collection) entry.getValue()) {
                            this.submitSubTask(taskId, parentTask, obj);
                        }
                    } else {
                        this.submitSubTask(taskId, parentTask, entry.getValue());
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private boolean saveToRedis(SubmitMessage submitMessage) {
        Map<String, Object> extractResultMap = submitMessage.getExtractResultMap();
        ExtractMessage extractMessage = submitMessage.getExtractMessage();
        int taskLogId = extractMessage.getTaskLogId();
        if (MapUtils.isEmpty(extractResultMap)) {
            logger.warn("no data save to redis by taskid: " + submitMessage);
            return true;
        }
        submitNormalizerFactory.normalize(submitMessage);
        for (Entry<String, Object> entry : extractResultMap.entrySet()) {
            if ("subSeed".equals(entry.getKey())) continue;// no need to save subSeed to redis
            String redisKey = RedisKeyUtils.genRedisKey(extractMessage.getTaskId(), extractMessage.getTaskLogId(), entry.getKey());
            String backKey = "monitor.back." + redisKey;
            try {
                BackRedisUtils.hset(RedisKeyPrefixEnum.TASK_RESULT.getRedisKey(extractMessage.getTaskId()), entry.getKey(), backKey,
                        RedisKeyPrefixEnum.TASK_RESULT.toSeconds());
            } catch (Throwable e) {
                logger.error("save to back redis error ", e);
            }
            if (entry.getValue() instanceof Collection) {
                List<String> jsonStringList = new ArrayList<String>();
                for (Object obj : (Collection) entry.getValue()) {
                    jsonStringList.add(GsonUtils.toJson(obj));
                }
                redisService.saveToList(redisKey, jsonStringList, 30, TimeUnit.MINUTES);
                try {
                    if (!jsonStringList.isEmpty()) {
                        BackRedisUtils.rpush(backKey, jsonStringList.toArray(new String[jsonStringList.size()]));
                    }
                } catch (Throwable e) {
                    logger.error("save to back redis error ", e);
                }
            } else if (entry.getValue() != null) {
                redisService.saveString(redisKey, GsonUtils.toJson(entry.getValue()), 30, TimeUnit.MINUTES);
                try {
                    BackRedisUtils.set(backKey, JSON.toJSONString(entry.getValue()));
                } catch (Throwable e) {
                    logger.error("save to back redis error ", e);
                }
            }
            try {
                BackRedisUtils.expire(backKey, RedisKeyPrefixEnum.TASK_RESULT.toSeconds());
            } catch (Throwable e) {
                logger.error("save to back redis error ", e);
            }
            submitMessage.getSubmitkeyResult().put(entry.getKey() + "Key", redisKey);
        }
        return true;
    }
}