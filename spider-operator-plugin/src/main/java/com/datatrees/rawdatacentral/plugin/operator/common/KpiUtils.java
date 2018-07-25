package com.datatrees.rawdatacentral.plugin.operator.common;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.spider.share.common.TaskUtils;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KpiUtils {

    private static final Logger logger               = LoggerFactory.getLogger(KpiUtils.class);

    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    public static void sendKpi(OperatorParam param, String kpiType, String kpiName, String kpiValue, String kpiDescription) {
        sendKpi(param.getTaskId(), param.getWebsiteName(), kpiType, kpiName, kpiValue, kpiDescription, null);
    }

    public static void sendKpi(long taskId, String websiteName, String kpiType, String kpiName, String kpiValue, String kpiDescription,
            String extra) {
        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.WEBSITE_NAME, websiteName);
        map.put(AttributeKey.SAAS_ENV, TaskUtils.getSassEnv());
        map.put("kpiType", kpiType);
        map.put("kpiName", kpiName);
        map.put("kpiValue", kpiValue);
        map.put("extra", extra);
        map.put("kpiDescription", kpiDescription);
        sendMessage(TopicEnum.CRAWLER_MONITOR.getCode(), "kpi", taskId, map);
        logger.info("send kpi data:{}", JSON.toJSONString(map));
    }

    public static boolean sendMessage(String topic, String tags, Long taskId, Object msg) {

        if (StringUtils.isBlank(topic) || null == msg) {
            logger.error("invalid param  topic={},msg={}", topic, msg);
            return false;
        }
        String content = JSON.toJSONString(msg);
        try {
            DefaultMQProducer defaultMQProducer = BeanFactoryUtils.getBean(DefaultMQProducer.class);
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

}
