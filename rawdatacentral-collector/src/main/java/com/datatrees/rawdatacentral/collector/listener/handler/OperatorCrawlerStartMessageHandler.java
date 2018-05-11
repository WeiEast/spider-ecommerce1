package com.datatrees.rawdatacentral.collector.listener.handler;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.domain.enums.TopicTag;
import com.datatrees.rawdatacentral.domain.mq.message.LoginMessage;
import org.springframework.stereotype.Service;

@Service
public class OperatorCrawlerStartMessageHandler extends LoginStartMessageHandler {

    @Resource
    private Collector      collector;
    @Resource
    private MonitorService monitorService;

    @Override
    public String getTag() {
        return TopicTag.OPERATOR_CRAWLER_START.getTag();
    }

    @Override
    public String getBizType() {
        return "处理爬虫启动消息";
    }

    @Override
    public boolean consumeMessage(String msg) {
        LoginMessage loginInfo = JSON.parseObject(msg, LoginMessage.class);
        Long taskId = loginInfo.getTaskId();
        //如果是登录成功消息就启动爬虫
        monitorService.sendTaskLog(taskId, loginInfo.getWebsiteName(), "爬虫-->启动-->成功");
        //任务10分钟超时
        //monitorService.sendTaskTimeOutMsg(taskId, 14);
        collector.processMessage(buildCollectorMessage(loginInfo));
        return true;
    }

}
