package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.notify.async.body.mail.MailBody;
import com.datatrees.notify.async.body.mail.MailEnum;
import com.datatrees.notify.async.body.sms.SmsEnum;
import com.datatrees.notify.async.body.wechat.WeChatBody;
import com.datatrees.notify.async.body.wechat.WeChatEnum;
import com.datatrees.notify.async.body.wechat.message.TXTMessage;
import com.datatrees.notify.async.check.PremiseChecker;
import com.datatrees.notify.async.util.BeanUtil;
import com.datatrees.notify.sms.bean.SmsResult;
import com.datatrees.notify.sms.newservice.SmsNewService;
import com.datatrees.notify.sms.newservice.entity.message.SmsMessage;
import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.common.utils.FormatUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.service.NotifyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotifyServiceImpl implements NotifyService {

    private static final Logger logger       = LoggerFactory.getLogger(NotifyServiceImpl.class);
    /**
     * 预警业务类型
     */
    private static final String BUSINESS     = "monitor";
    /**
     * 预警topic
     */
    private static final String TOPIC_NOTIFY = "mq_topic_notify";
    @Resource
    private SmsNewService     smsNewService;
    @Resource
    private DefaultMQProducer defaultMQProducer;

    @Override
    public Boolean sendMonitorEmail(String subject, String body) {
        String emailReceiver = PropertiesConfiguration.getInstance().get("monitor.email.receiver");
        if (StringUtils.isBlank(emailReceiver)) {
            logger.warn("monitor.email.receiver is blank");
            return false;
        }
        List<String> receiver = Arrays.asList(emailReceiver.split(","));
        MailBody msg = buildMailBody(receiver, subject, body);
        try {
            String msgId = sendNotifyMessage(msg);
            if (null != msgId) {
                logger.info("send email msg success receiver={},subject={},body={},msgId={}", JSON.toJSONString(receiver), subject, body, msgId);
                return true;
            } else {
                logger.warn("send email msg fail receiver={},subject={},body={}", JSON.toJSONString(receiver), subject, body);
                return false;
            }
        } catch (Throwable e) {
            logger.error("send email msg  error receiver={},subject={}", JSON.toJSONString(receiver), subject, e);
            return false;
        }
    }

    @Override
    public Boolean sendMonitorWeChat(String body) {
        String wechatReceiver = PropertiesConfiguration.getInstance().get("monitor.wechat.receiver");
        if (StringUtils.isBlank(wechatReceiver)) {
            logger.warn("monitor.wechat.receiver is blank");
            return false;
        }
        List<String> receiver = Arrays.asList(wechatReceiver.trim().split(","));
        WeChatBody msg = buildWeChatBody(receiver, body);
        try {
            String msgId = sendNotifyMessage(msg);
            if (null != msgId) {
                logger.info("send wechat msg success receiver={},subject={},body={},msgId={}", JSON.toJSONString(receiver), body, msgId);
                return true;
            } else {
                logger.warn("send wechat msg fail receiver={},subject={},body={}", JSON.toJSONString(receiver), body);
                return false;
            }
        } catch (Throwable e) {
            logger.error("send wechat msg  error receiver={},body={}", JSON.toJSONString(receiver), body, e);
            return false;
        }
    }

    @Override
    public Boolean sendMonitorSms(String body) {
        String smsReceiver = PropertiesConfiguration.getInstance().get("monitor.sms.receiver");
        if (StringUtils.isBlank(smsReceiver)) {
            logger.warn("monitor.sms.receiver is blank");
            return false;
        }
        SmsResult result = null;
        try {
            SmsMessage message = buildSmsMessage(Arrays.asList(smsReceiver.trim().split(",")), body);
            result = smsNewService.sendMessage(message);
            if (null == result || 1 != result.getCode()) {
                logger.error("sendSms fail,mobile={},sms={},result={},message={}", smsReceiver, body, JSON.toJSONString(result),
                        JSON.toJSONString(message));
                return false;
            }
            logger.info("resu sms success mobile={},sms={}", smsReceiver, body);
            return true;
        } catch (Throwable e) {
            logger.error("resu sms msg  error mobile={},sms={},result={}", smsReceiver, body, JSON.toJSONString(result), e);
            return false;
        }
    }

    @Override
    public Boolean sendMsgForOperatorStatusUpdate(WebsiteOperator change, WebsiteOperator from, WebsiteOperator to, Boolean enable, Boolean auto) {
        try {
            String saasEnv = System.getProperty(AttributeKey.SAAS_ENV, "none");
            Map<String, Object> map = new HashMap<>();
            map.put("changeWebsiteName", change.getWebsiteName());
            map.put("changeWebsiteTitle", change.getWebsiteTitle());
            map.put("env", saasEnv);
            map.put("enable", enable ? "启用" : "禁用");
            map.put("auto", auto ? " 自动" : "手动");
            map.put("fromWebsiteTitle", from.getWebsiteTitle());
            map.put("toWebsiteTitle", to.getWebsiteTitle());
            map.put("date", DateUtils.formatYmdhms(new Date()));
            String wechatTmpl
                    = "【运营商状态变更】\n环境:${env}\n配置:${changeWebsiteName}\n名称:${changeWebsiteTitle}\n操作:${enable}\n操作方式:${auto}\n时间:${date}\n操作前:${fromWebsiteTitle}\n操作后:${toWebsiteTitle}";
            String smsTmpl
                    = "<运营商状态变更>\n环境:${env}\n配置:${changeWebsiteName}\n名称:${changeWebsiteTitle}\n操作:${enable}\n操作方式:${auto}\n时间:${date}\n操作前:${fromWebsiteTitle}\n操作后:${toWebsiteTitle}";
            String smsMsg = FormatUtils.format(smsTmpl, map);
            sendMonitorSms(smsMsg);
            String wechatMsg = FormatUtils.format(wechatTmpl, map);
            sendMonitorWeChat(wechatMsg);
            return true;
        } catch (Throwable e) {
            logger.error("sendMsgForOperatorStatusUpdate ", e);
            return false;
        }
    }

    private String sendNotifyMessage(Object body) {
        try {
            PremiseChecker.check(body);
            Message msg = new Message(TOPIC_NOTIFY, null, null, BeanUtil.objectToByte(body));
            SendResult sendResult = defaultMQProducer.send(msg);
            if (null != sendResult && sendResult.getSendStatus() == SendStatus.SEND_OK) {
                return sendResult.getMsgId();
            } else {
                return null;
            }
        } catch (Throwable e) {
            logger.error("sendMonitorEmailUseMQ error body={}", JSON.toJSONString(body), e);
            return null;
        }
    }

    private SmsMessage buildSmsMessage(List<String> receiver, String body) {
        SmsMessage message = new SmsMessage();
        message.setContent(body);
        message.setMobileList(receiver);
        message.setSerialNumber("crawler.monitor.sms." + System.currentTimeMillis());
        String smsChannel = PropertiesConfiguration.getInstance().get("monitor.sms.channel");
        message.setSmsEnum(SmsEnum.get(smsChannel));
        return message;
    }

    private WeChatBody buildWeChatBody(List<String> receiver, String body) {
        TXTMessage message = new TXTMessage();
        message.setMessage(body);
        WeChatBody weChatBody = new WeChatBody();
        //爬虫:1000007 nginx监控:60
        weChatBody.setAgentId("1000007");
        weChatBody.setWeChatEnum(WeChatEnum.DASHU_AN_APP_TXT);
        weChatBody.setMessage(message);
        weChatBody.setWxUserList(receiver);
        return weChatBody;
    }

    private MailBody buildMailBody(List<String> receiver, String subject, String body) {
        MailBody mailBody = new MailBody();
        mailBody.setMailEnum(MailEnum.HTML_MAIL);
        mailBody.setBusiness(BUSINESS);
        mailBody.setToList(receiver);
        mailBody.setSubject(subject);
        mailBody.setBody(body);
        return mailBody;
    }
}
