package com.datatrees.rawdatacentral.collector.listener.handler;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.*;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.service.WebsiteHolderService;
import com.datatrees.spider.share.service.mq.MessageHandler;
import com.treefinance.crawler.exception.UnsupportedWebsiteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.datatrees.rawdatacentral.collector.listener.handler.CollectorMessageUtils.buildCollectorMessage;

@Service
public class LoginInfoMessageHandler implements MessageHandler {

    private static final Logger               logger = LoggerFactory.getLogger(LoginInfoMessageHandler.class);

    @Resource
    private              Collector            collector;

    @Resource
    private              RedisService         redisService;

    @Resource
    private              MonitorService       monitorService;

    @Resource
    private              WebsiteHolderService websiteHolderService;

    @Override
    public String getTag() {
        return TopicTag.LOGIN_INFO.getTag();
    }

    @Override
    public long getExpireTime() {
        return TimeUnit.MINUTES.toMillis(10);
    }

    @Override
    public String getTitle() {
        return "准备";
    }

    @Override
    public boolean consumeMessage(String msg) {
        LoginMessage loginInfo = JSON.parseObject(msg, LoginMessage.class);
        Long taskId = loginInfo.getTaskId();
        TaskUtils.addStep(taskId, StepEnum.REC_INIT_MSG);
        Boolean initStatus = TaskUtils.isDev() ||
                RedisUtils.setnx(RedisKeyPrefixEnum.TASK_RUN_COUNT.getRedisKey(taskId), "0", RedisKeyPrefixEnum.TASK_RUN_COUNT.toSeconds());
        //第一次收到启动消息
        if (initStatus) {
            TaskUtils.addStep(taskId, StepEnum.INIT);
            String websiteName = loginInfo.getWebsiteName();
            //这里电商,邮箱,老运营商
            Website website = websiteHolderService.getWebsite(websiteName);
            if (website == null) {
                throw new UnsupportedWebsiteException("Unsupported website: " + websiteName);
            }
            redisService.cache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, website);
            //缓存task基本信息
            TaskUtils.initTaskShare(taskId, loginInfo.getWebsiteName());
            TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, loginInfo.getAccountNo());
            TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_CODE, website.getGroupCode());
            TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_NAME, website.getGroupName());
            TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TITLE, website.getWebsiteTitle());
            TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TYPE, website.getWebsiteType());

            //初始化监控信息
            monitorService.initTask(taskId, websiteName, loginInfo.getAccountNo());
            TaskUtils.addStep(taskId, StepEnum.INIT_SUCCESS);
            monitorService.sendTaskLog(taskId, websiteName, "爬虫-->启动-->成功");
            //启动爬虫
            collector.processMessage(buildCollectorMessage(loginInfo));
            return true;
        }
        logger.warn("重复消息,不处理,taskId={},websiteName={}", taskId, loginInfo.getWebsiteName());
        return true;
    }

    @Override
    public int getMaxRetry() {
        return 0;
    }

    @Override
    public String getTopic() {
        return TopicEnum.RAWDATA_INPUT.getCode();
    }

}
