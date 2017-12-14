package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.rawdatacentral.api.CrawlerTaskService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
import com.datatrees.rawdatacentral.domain.enums.TopicTag;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MonitorServiceImpl implements MonitorService {

    private static final Logger logger               = LoggerFactory.getLogger(MonitorServiceImpl.class);
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";
    @Resource
    private CrawlerTaskService crawlerTaskService;
    @Resource
    private DefaultMQProducer  defaultMQProducer;

    @Override
    public void initTask(Long taskId, String websiteName, Object userName) {
        Map<String, String> map = crawlerTaskService.getTaskBaseInfo(taskId, websiteName);
        if (null != userName) {
            map.put(AttributeKey.USERNAME, String.valueOf(userName));
        }
        sendMessage(TopicEnum.CRAWLER_MONITOR.getCode(), TopicTag.TASK_INIT.getTag(), taskId, map);
    }

    @Override
    public void sendTaskCompleteMsg(Long taskId, String websiteName, Integer errorCode, String errorMsg) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.WEBSITE_NAME, websiteName);
        map.put(AttributeKey.ERROR_CODE, errorCode);
        map.put(AttributeKey.ERROR_MSG, errorMsg);
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        sendMessage(TopicEnum.CRAWLER_MONITOR.getCode(), TopicTag.TASK_COMPLETE.getTag(), taskId, map);
    }

    @Override
    public void sendTaskLog(Long taskId, String websiteName, String msg, Integer errorCode, String errorMsg, String errorDetail) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.WEBSITE_NAME, websiteName);
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        map.put(AttributeKey.MSG, msg);
        map.put(AttributeKey.ERROR_CODE, errorCode);
        map.put(AttributeKey.ERROR_MSG, errorMsg);
        map.put(AttributeKey.ERROR_DETAIL, errorDetail);
        saveTaskLog(taskId, map);
    }

    @Override
    public void sendTaskLog(Long taskId, String websiteName, String msg, HttpResult result) {
        if (result.getStatus()) {
            sendTaskLog(taskId, websiteName, msg);
        } else {
            sendTaskLog(taskId, websiteName, msg, result.getResponseCode(), result.getMessage(), result.getErrorDetail());
        }
    }

    @Override
    public void sendTaskLog(Long taskId, String websiteName, String msg, ErrorCode errorCode) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.WEBSITE_NAME, websiteName);
        map.put(AttributeKey.MSG, msg);
        map.put(AttributeKey.ERROR_CODE, errorCode.getErrorCode());
        map.put(AttributeKey.ERROR_MSG, errorCode.getErrorMsg());
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        saveTaskLog(taskId, map);
    }

    @Override
    public void sendTaskLog(Long taskId, String websiteName, String msg, ErrorCode errorCode, String errorDetail) {
        sendTaskLog(taskId, websiteName, msg, errorCode.getErrorCode(), errorCode.getErrorMsg(), errorDetail);
    }

    @Override
    public void sendTaskLog(Long taskId, String websiteName, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.WEBSITE_NAME, websiteName);
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        map.put(AttributeKey.MSG, msg);
        saveTaskLog(taskId, map);
    }

    @Override
    public void sendTaskLog(Long taskId, String msg) {
        sendTaskLog(taskId, null, msg);
    }

    @Override
    public void sendTaskLog(Long taskId, String msg, ErrorCode errorCode, String errorDetail) {
        sendTaskLog(taskId, null, msg, errorCode, errorDetail);
    }

    @Override
    public void sendTaskLog(Long taskId, String msg, ErrorCode errorCode) {
        sendTaskLog(taskId, null, msg, errorCode);
    }

    @Override
    public void sendMethodUseTime(Long taskId, String websiteName, String key, String className, String methodName, List<Object> param, Object result,
            long startTime, long endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.WEBSITE_NAME, websiteName);
        map.put(AttributeKey.KEY, key);
        map.put(AttributeKey.CLASS_NAME, className);
        map.put(AttributeKey.METHOD_NAME, methodName);
        map.put(AttributeKey.PARAM, param);
        map.put(AttributeKey.RESULT, result);
        map.put(AttributeKey.START_TIME, startTime);
        map.put(AttributeKey.END_TIME, endTime);
        map.put(AttributeKey.REMARK, DateUtils.getUsedTime(startTime, endTime));
        if (null != result) {
            map.put(AttributeKey.RESULT_CLASS, result.getClass().getName());
        }
        String redisKey = RedisKeyPrefixEnum.TASK_METHOD_USE_TIME.getRedisKey(taskId);
        RedisUtils.hset(redisKey, String.valueOf(System.currentTimeMillis()), JSON.toJSONString(map), RedisKeyPrefixEnum.TASK_LOG.toSeconds());
    }

    public boolean sendMessage(String topic, String tags, Long taskId, Object msg) {
        if (StringUtils.isBlank(topic) || null == msg) {
            logger.error("invalid param  topic={},msg={}", topic, msg);
            return false;
        }
        String content = JSON.toJSONString(msg);
        try {
            Message mqMessage = new Message();
            mqMessage.setTopic(topic);
            mqMessage.setBody(content.getBytes(DEFAULT_CHARSET_NAME));
            String key = TemplateUtils.format("{}.{}.{}", topic, StringUtils.isNotBlank(tags) ? tags : "", taskId);
            mqMessage.setKeys(key);
            if (StringUtils.isNotBlank(tags)) {
                mqMessage.setTags(tags);
            }
            SendResult sendResult = defaultMQProducer.send(mqMessage);
            if (sendResult != null && SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                logger.info("send message success topic={},tags={},content={},charsetName={},msgId={}", topic, tags,
                        content.length() > 100 ? content.substring(0, 100) : content, DEFAULT_CHARSET_NAME, sendResult.getMsgId());
                return true;
            }
        } catch (Exception e) {
            logger.error("send message error topic={},content={},charsetName={}", topic, content, DEFAULT_CHARSET_NAME, e);
        }
        logger.error("send message fail topic={},content={},charsetName={}", topic, content, DEFAULT_CHARSET_NAME);
        return false;
    }

    private void saveTaskLog(Long taskId, Map<String, Object> map) {
        if (null == taskId || null == map || map.isEmpty()) {
            return;
        }
        String key = RedisKeyPrefixEnum.TASK_LOG.getRedisKey(taskId);
        RedisUtils.hset(key, String.valueOf(System.currentTimeMillis()), JSON.toJSONString(map), RedisKeyPrefixEnum.TASK_LOG.toSeconds());
    }
}
