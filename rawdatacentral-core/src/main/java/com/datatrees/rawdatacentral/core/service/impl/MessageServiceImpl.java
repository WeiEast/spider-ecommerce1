package com.datatrees.rawdatacentral.core.service.impl;

import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.rawdatacentral.core.service.MessageService;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
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

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Resource
    private MQProducer          producer;

    @Override
    public boolean sendTaskLog(Long taskId, String msg, String errorDetail) {
        AbstractProcessorContext context = PluginFactory.getProcessorContext();
        String websiteName = context.getWebsiteName();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("taskId", taskId);
        map.put("websiteName", websiteName);
        map.put("timestamp", System.currentTimeMillis());
        map.put("msg", msg);
        map.put("errorDetail", errorDetail);

        Message mqMessage = new Message();
        mqMessage.setTopic(TopicEnum.TASK_LOG.getCode());
        mqMessage.setBody(GsonUtils.toJson(map).getBytes());

        int retry = 0;
        while (retry++ <= 3) {
            try {
                SendResult sendResult = producer.send(mqMessage);
                logger.info("send result message={},status={}", GsonUtils.toJson(map), sendResult.getSendStatus());
                if (sendResult != null && SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    return true;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return false;
    }

    @Override
    public boolean sendTaskLog(Long taskId, String msg) {
        return sendTaskLog(taskId, msg, null);
    }
}
