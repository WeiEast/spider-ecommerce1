package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.common.util.StringUtils;
import com.datatrees.rawdatacentral.api.CrawlerTaskService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.common.utils.RegexpUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
import com.datatrees.rawdatacentral.domain.enums.TopicTag;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MonitorServiceImpl implements MonitorService, InitializingBean {

    private static final Logger logger               = LoggerFactory.getLogger(MonitorServiceImpl.class);
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";
    @Resource
    private CrawlerTaskService crawlerTaskService;
    private DefaultMQProducer  monitorProducer;
    @Value("${saas.assistant.monitor.listener.namesrvAddr:share-mq.dashu.ds:9876}")
    private String             namesrvAddr;

    @Override
    public void initTask(Long taskId) {
        Map<String, String> map = crawlerTaskService.getTaskBaseInfo(taskId);
        sendMessage(TopicEnum.CRAWLER_TASK_MONITOR.getCode(), TopicTag.TASK_INIT.getTag(), map);
    }

    @Override
    public void sendTaskCompleteMsg(Long taskId, Integer errorCode, String errorMsg) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.ERROR_CODE, errorCode);
        map.put(AttributeKey.ERROR_MSG, errorMsg);
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        sendMessage(TopicEnum.CRAWLER_TASK_MONITOR.getCode(), TopicTag.TASK_COMPLETE.getTag(), map);
    }

    @Override
    public void sendTaskLog(Long taskId, String msg, Integer errorCode, String errorMsg, String errorDetail) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        map.put(AttributeKey.MSG, msg);
        map.put(AttributeKey.ERROR_CODE, errorCode);
        map.put(AttributeKey.ERROR_MSG, errorMsg);
        map.put(AttributeKey.ERROR_DETAIL, errorDetail);
        sendMessage(TopicEnum.CRAWLER_TASK_LOG.getCode(), TopicTag.TASK_LOG.getTag(), map);
    }

    @Override
    public void sendTaskLog(Long taskId, String msg, HttpResult result) {
        if (result.getStatus()) {
            sendTaskLog(taskId, msg);
        } else {
            sendTaskLog(taskId, msg, result.getResponseCode(), result.getMessage(), result.getErrorDetail());
        }
    }

    @Override
    public void sendTaskLog(Long taskId, String msg, ErrorCode errorCode) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.MSG, msg);
        map.put(AttributeKey.ERROR_CODE, errorCode.getErrorCode());
        map.put(AttributeKey.ERROR_MSG, errorCode.getErrorMsg());
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        sendMessage(TopicEnum.CRAWLER_TASK_LOG.getCode(), TopicTag.TASK_LOG.getTag(), map);
    }

    @Override
    public void sendTaskLog(Long taskId, String msg, ErrorCode errorCode, String errorDetail) {
        sendTaskLog(taskId, msg, errorCode.getErrorCode(), errorCode.getErrorMsg(), errorDetail);
    }

    @Override
    public void sendTaskLog(Long taskId, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        map.put(AttributeKey.MSG, msg);
        sendMessage(TopicEnum.CRAWLER_TASK_LOG.getCode(), TopicTag.TASK_LOG.getTag(), map);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        monitorProducer = new DefaultMQProducer("crawler_rawdata");
        monitorProducer.setNamesrvAddr(namesrvAddr);
        monitorProducer.setRetryTimesWhenSendFailed(3);
        monitorProducer.setMaxMessageSize(1024 * 1024 * 2);
        monitorProducer.start();

    }

    public boolean sendMessage(String topic, String tags, Object msg) {
        if (StringUtils.isBlank(topic) || null == msg) {
            logger.error("invalid param  topic={},msg={}", topic, msg);
            return false;
        }
        String content = JSON.toJSONString(msg);
        try {
            Message mqMessage = new Message();
            mqMessage.setTopic(topic);
            mqMessage.setBody(content.getBytes(DEFAULT_CHARSET_NAME));
            StringBuilder key = new StringBuilder(topic);
            if (StringUtils.isNotBlank(tags)) {
                key.append("_").append(tags);
            }
            String taskId = RegexpUtils.select(content, "taskId\":(\\d+)", 1);
            if (StringUtils.isNotBlank(taskId)) {
                key.append("_").append(taskId);
            } else {
                key.append("_").append(System.currentTimeMillis());
            }
            mqMessage.setKeys(key.toString());
            if (StringUtils.isNotBlank(tags)) {
                mqMessage.setTags(tags);
            }
            SendResult sendResult = monitorProducer.send(mqMessage);
            if (sendResult != null && SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                logger.info("send message success topic={},tags={},content={},charsetName={}", topic, tags,
                        content.length() > 100 ? content.substring(0, 100) : content, DEFAULT_CHARSET_NAME);
                return true;
            }
        } catch (Exception e) {
            logger.info("send message error topic={},content={},charsetName={}", topic, content, DEFAULT_CHARSET_NAME, e);
        }
        logger.error("send message fail topic={},content={},charsetName={}", topic, content, DEFAULT_CHARSET_NAME);
        return false;
    }
}
