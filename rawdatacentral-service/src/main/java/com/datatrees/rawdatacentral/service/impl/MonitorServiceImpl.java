package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
import com.datatrees.rawdatacentral.domain.enums.TopicTag;
import com.datatrees.rawdatacentral.service.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MonitorServiceImpl implements MonitorService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);
    @Resource
    private MessageService messageService;

    @Override
    public void sendTaskCompleteMsg(Long taskId, boolean status, Integer errorCode, String errorMsg) {
        Map<String, Object> msg = new HashMap<>();
        msg.put(AttributeKey.TASK_ID, taskId);
        msg.put(AttributeKey.STATUS, status);
        msg.put(AttributeKey.ERROR_CODE, errorCode);
        msg.put(AttributeKey.ERROR_MSG, errorMsg);
        msg.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        messageService.sendMessage(TopicEnum.CRAWLER_TASK_MONITOR.getCode(), TopicTag.TASK_COMPLETE.getTag(), msg);
    }

    @Override
    public void sendTaskErrorMsg(Long taskId, Integer errorCode, String errorMsg, String errorDetail) {
        Map<String, Object> msg = new HashMap<>();
        msg.put(AttributeKey.TASK_ID, taskId);
        msg.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
        msg.put(AttributeKey.ERROR_CODE, errorCode);
        msg.put(AttributeKey.ERROR_MSG, errorMsg);
        msg.put(AttributeKey.ERROR_DETAIL, errorDetail);
        messageService.sendMessage(TopicEnum.CRAWLER_TASK_MONITOR.getCode(), TopicTag.TASK_ERROR.getTag(), msg);
    }
}
