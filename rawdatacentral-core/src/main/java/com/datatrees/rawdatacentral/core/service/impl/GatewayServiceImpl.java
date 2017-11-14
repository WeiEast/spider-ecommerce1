package com.datatrees.rawdatacentral.core.service.impl;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.common.resource.DataResource;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.core.dao.RedisDao;
import com.datatrees.rawdatacentral.core.message.MessageFactory;
import com.datatrees.rawdatacentral.core.model.message.impl.ResultMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GatewayServiceImpl implements DataResource {

    private static final Logger logger = LoggerFactory.getLogger(GatewayServiceImpl.class);
    @Resource
    private RedisDao          redisDao;
    @Resource
    private MessageFactory    messageFactory;
    @Resource
    private DefaultMQProducer defaultMQProducer;
    @Resource
    private MessageService    messageService;

    private String genRedisKey(Map<String, Object> parameters) {
        StringBuilder sb = new StringBuilder();
        String taskId = String.valueOf(parameters.get("taskId"));
        return sb.append("verify_result_").append(taskId).toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.crawler.core.processor.common.resource.DataResource#getData
     * (java.util.Map)
     */
    @Override
    public Object getData(Map<String, Object> parameters) {
        try {
            String result = redisDao.getStringFromList(genRedisKey(parameters));
            if (StringUtils.isNotEmpty(result)) {
                logger.debug("obtain result sucess!");
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.crawler.core.processor.common.resource.DataResource#setData
     * (java.util.Map, java.lang.Object)
     */
    @Override
    public boolean sendToQueue(Map<String, Object> parameters) {
        try {
            String taskId = String.valueOf(parameters.get("taskId"));
            String websiteName = String.valueOf(parameters.get("websiteName"));
            String status = String.valueOf(parameters.get("status"));
            String isEmpty = String.valueOf(parameters.get("isResultEmpty"));
            String remark = String.valueOf(parameters.get("remark"));
            String tag = String.valueOf(parameters.get("tag"));

            ResultMessage resultMessage = new ResultMessage();
            resultMessage.putAll(parameters);
            resultMessage.setRemark(remark);
            resultMessage.setTaskId(Long.valueOf(taskId));
            resultMessage.setWebsiteName(websiteName);
            resultMessage.setResultEmpty(Boolean.valueOf(isEmpty));
            resultMessage.setStatus(status);
            if (logger.isDebugEnabled()) {
                logger.debug("send to queue:" + GsonUtils.toJson(resultMessage));
            }
            Message mqMessage = messageFactory.getMessage("rawData_result_status", tag, GsonUtils.toJson(resultMessage), taskId);
            SendResult sendResult = defaultMQProducer.send(mqMessage);
            logger.info("send result message:" + mqMessage + "result:" + sendResult);
            if (sendResult != null && SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean ttlSave(String key, String value, long timeOut) {
        try {
            redisDao.getRedisTemplate().opsForValue().set(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        redisDao.getRedisTemplate().expire(key, timeOut, TimeUnit.SECONDS);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.common.resource.DataResource#ttlPush(java.lang.String,
     * java.lang.String, long)
     */
    @Override
    public boolean ttlPush(String key, String value, long timeOut) {
        try {
            Long result = redisDao.getRedisTemplate().opsForList().rightPushAll(key, value);
            redisDao.getRedisTemplate().expire(key, timeOut, TimeUnit.SECONDS);
            return result != null ? true : false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.common.resource.DataResource#deleteKey(java.lang.String)
     */
    @Override
    public boolean clearData(Map<String, Object> parameters) {
        try {
            String key = genRedisKey(parameters);
            logger.info("init clear redis data key :" + key);
            redisDao.deleteKey(key);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

}
