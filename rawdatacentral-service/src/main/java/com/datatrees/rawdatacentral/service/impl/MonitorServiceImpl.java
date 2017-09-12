package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.rawdatacentral.api.CrawlerTaskService;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
import com.datatrees.rawdatacentral.domain.enums.TopicTag;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MonitorServiceImpl implements MonitorService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);
    @Resource
    private MessageService     messageService;
    @Resource
    private CrawlerTaskService crawlerTaskService;

    @Override
    public void initTask(Long taskId, String websiteName) {
        Map<String, String> map = crawlerTaskService.getTaskBaseInfo(taskId);
        messageService.sendMessage(TopicEnum.CRAWLER_TASK_MONITOR.getCode(), TopicTag.TASK_INIT.getTag(), map);
    }

    @Override
    public void sendTaskCompleteMsg(Long taskId, Integer errorCode, String errorMsg) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.ERROR_CODE, errorCode);
        map.put(AttributeKey.ERROR_MSG, errorMsg);
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        messageService.sendMessage(TopicEnum.CRAWLER_TASK_MONITOR.getCode(), TopicTag.TASK_COMPLETE.getTag(), map);
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
        messageService.sendMessage(TopicEnum.CRAWLER_TASK_LOG.getCode(), TopicTag.TASK_LOG.getTag(), map);
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
    public void sendTaskLog(Long taskId, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        map.put(AttributeKey.MSG, msg);
        messageService.sendMessage(TopicEnum.CRAWLER_TASK_LOG.getCode(), TopicTag.TASK_LOG.getTag(), map);
    }
}
