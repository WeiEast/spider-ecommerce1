package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.*;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.MonitorService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MonitorServiceImpl implements MonitorService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);
    @Resource
    private MessageService         messageService;
    @Resource
    private WebsiteOperatorService websiteOperatorService;

    @Override
    public void initTask(Long taskId, String websiteName) {
        CheckUtils.checkNotPositiveNumber(taskId, ErrorCode.EMPTY_TASK_ID);
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.WEBSITE_NAME, websiteName);
        Boolean isNewOperator = StringUtils.startsWith(websiteName, RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.getPrefix());
        GroupEnum group = null;
        if (isNewOperator) {
            WebsiteOperator websiteOperator = websiteOperatorService
                    .getByWebsiteName(RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.parsePostfix(websiteName));
            map.put(AttributeKey.WEBSITE_TITLE, websiteOperator.getWebsiteTitle());
            group = GroupEnum.getByGroupCode(websiteOperator.getGroupCode());
        } else {
            group = GroupEnum.getByWebsiteName(websiteName);
        }
        map.put(AttributeKey.GROUP_CODE, group.getGroupCode());
        map.put(AttributeKey.GROUP_NAME, group.getGroupName());
        map.put(AttributeKey.WEBSITE_TYPE, group.getWebsiteType().getValue());
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis());
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
        messageService.sendMessage(TopicEnum.CRAWLER_TASK_MONITOR.getCode(), TopicTag.TASK_LOG.getTag(), map);
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
        messageService.sendMessage(TopicEnum.CRAWLER_TASK_MONITOR.getCode(), TopicTag.TASK_LOG.getTag(), msg);
    }
}
