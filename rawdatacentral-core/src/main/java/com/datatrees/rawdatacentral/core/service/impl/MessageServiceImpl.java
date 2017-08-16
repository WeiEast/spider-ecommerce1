package com.datatrees.rawdatacentral.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.common.util.StringUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.CookieUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
import com.datatrees.rawdatacentral.api.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/5/11.
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger               = LoggerFactory.getLogger(MessageServiceImpl.class);

    /**
     * 默认格式格式化成JSON后发送的字符编码
     */
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    @Resource
    private MQProducer          producer;

    @Resource
    private RedisService        redisService;

    @Override
    public boolean sendTaskLog(Long taskId, String msg, String errorDetail) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("taskId", taskId);
        map.put("timestamp", System.currentTimeMillis());
        map.put("msg", msg);
        map.put("errorDetail", errorDetail);
        return sendMessage(TopicEnum.TASK_LOG.getCode(), map, DEFAULT_CHARSET_NAME, 2);
    }

    @Override
    public String sendDirective(Long taskId, String directive, String remark) {
        if (null == taskId || StringUtils.isBlank(directive)) {
            logger.error("invalid param taskId={},directive={}", taskId, directive);
            return null;
        }
        String directiveId = redisService.createDirectiveId();
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put(AttributeKey.TASK_ID, taskId);
        msg.put(AttributeKey.DIRECTIVE_ID, directiveId);
        msg.put(AttributeKey.DIRECTIVE, directive);
        msg.put(AttributeKey.REMARK, remark);
        sendMessage(TopicEnum.TASK_NEXT_DIRECTIVE.getCode(), msg, DEFAULT_CHARSET_NAME, 3);
        return directiveId;
    }

    @Override
    public boolean sendTaskLog(Long taskId, String msg) {
        return sendTaskLog(taskId, msg, null);
    }

    @Override
    public boolean sendMessage(String topic, Object msg) {
        return sendMessage(topic, msg, DEFAULT_CHARSET_NAME, 2);
    }

    @Override
    public boolean sendMessage(String topic, Object msg, String charsetName, int maxRetry) {
        return sendMessage(topic, null, msg, charsetName, maxRetry);
    }

    private boolean sendMessage(String topic, String tags, Object msg, String charsetName, int maxRetry) {
        if (StringUtils.isBlank(topic) || null == msg) {
            logger.error("invalid param  topic={},msg={}", topic, msg);
            return false;
        }
        if (StringUtils.isBlank(charsetName)) {
            charsetName = DEFAULT_CHARSET_NAME;
        }
        String content = JSON.toJSONString(msg);
        int retry = 0;
        do {
            try {
                Message mqMessage = new Message();
                mqMessage.setTopic(topic);
                mqMessage.setBody(content.getBytes(charsetName));
                if (StringUtils.isNotBlank(tags)) {
                    mqMessage.setTags(tags);
                }
                SendResult sendResult = producer.send(mqMessage);
                if (sendResult != null && SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    logger.info("send message success topic={},content={},retry={},charsetName={}", topic,
                        content.length() > 100 ? content.substring(0, 100) : content, retry, charsetName);
                    return true;
                }
            } catch (Exception e) {
                logger.info("send message error topic={},content={},retry={},charsetName={}", topic, content, retry,
                    charsetName, e);
            }
        } while (retry++ <= maxRetry);
        logger.error("send message fail topic={},content={},retry={},maxRetry={},charsetName={}", topic, content, retry,
            maxRetry, charsetName);
        return false;
    }

    @Override
    public boolean sendLoginSuccessMessage(Long taskId, String websiteName) {
        CheckUtils.checkNotNull(taskId, "invalid taskId");
        CheckUtils.checkNotBlank(websiteName, "blank websiteName");
        LoginMessage loginMessage = new LoginMessage();
        loginMessage.setTaskId(taskId);
        loginMessage.setWebsiteName(websiteName);
        String cookieString = CookieUtils.getCookieString(taskId);
        loginMessage.setCookie(cookieString);
        sendMessage(TopicEnum.RAWDATA_INPUT.getCode(), "login_info", loginMessage, DEFAULT_CHARSET_NAME, 3);
        return true;
    }

}
