package com.datatrees.spider.operator.service.mq.handler;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.collector.listener.handler.CollectorMessageUtils;
import com.datatrees.spider.share.domain.LoginMessage;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.domain.TopicTag;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.service.mq.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OperatorCrawlerStartMessageHandler implements MessageHandler {

    private static final Logger         logger = LoggerFactory.getLogger(OperatorCrawlerStartMessageHandler.class);

    @Resource
    private              Collector      collector;

    @Resource
    private              MonitorService monitorService;

    @Override
    public String getTag() {
        return TopicTag.OPERATOR_CRAWLER_START.getTag();
    }

    @Override
    public long getExpireTime() {
        return TimeUnit.MINUTES.toMillis(10);
    }

    @Override
    public String getTitle() {
        return "运营商登陆成功,启动爬虫";
    }

    @Override
    public boolean consumeMessage(String msg) {
        LoginMessage loginInfo = JSON.parseObject(msg, LoginMessage.class);
        Long taskId = loginInfo.getTaskId();
        monitorService.sendTaskLog(taskId, loginInfo.getWebsiteName(), "爬虫-->启动-->成功");
        collector.processMessage(CollectorMessageUtils.buildCollectorMessage(loginInfo));
        return true;
    }

    @Override
    public int getMaxRetry() {
        return 0;
    }

    @Override
    public String getTopic() {
        return TopicEnum.SPIDER_OPERATOR.getCode();
    }

}
