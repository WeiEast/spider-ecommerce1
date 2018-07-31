package com.datatrees.spider.operator.service.mq.handler;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.spider.share.service.MessageService;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.service.utils.operator.OperatorUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.directive.DirectiveEnum;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.domain.TopicTag;
import com.datatrees.spider.share.service.mq.MessageHandler;
import com.datatrees.spider.operator.api.OperatorApi;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.operator.service.plugin.OperatorLoginPostPlugin;
import com.datatrees.spider.operator.service.plugin.OperatorPlugin;
import com.datatrees.spider.operator.service.WebsiteOperatorService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 运营商登陆后处理
 * 登陆成功-->登陆后处理-->启动爬虫
 */
@Service
public class OperatorLoginPostMessageHandler implements MessageHandler {

    private static final Logger                 logger = LoggerFactory.getLogger(OperatorLoginPostMessageHandler.class);

    @Resource
    private              MonitorService         monitorService;

    @Resource
    private              OperatorApi            spiderOperatorApi;

    @Resource
    private              WebsiteOperatorService websiteOperatorService;

    @Resource
    private              MessageService         messageService;

    @Override
    public String getTag() {
        return TopicTag.OPERATOR_LOGIN_POST.getTag();
    }

    @Override
    public long getExpireTime() {
        return TimeUnit.MINUTES.toMillis(10);
    }

    @Override
    public String getTitle() {
        return "运营商登陆后,爬虫启动前预处理";
    }

    @Override
    public boolean consumeMessage(String msg) {
        JSONObject json = JSON.parseObject(msg);
        Long taskId = json.getLong(AttributeKey.TASK_ID);
        String websiteName = json.getString(AttributeKey.WEBSITE_NAME);
        logger.info("登陆后-->启动-->成功,taskId={},websiteName={}", taskId, websiteName);
        monitorService.sendTaskLog(taskId, websiteName, "登陆后-->启动-->成功");
        OperatorParam param = new OperatorParam();
        param.setTaskId(taskId);
        param.setWebsiteName(websiteName);
        param.setFormType(FormType.LOGIN_POST);
        HttpResult<Map<String, Object>> result = spiderOperatorApi.checkParams(param);
        if (!result.getStatus()) {
            logger.warn("登陆后-->处理-->失败,msg={},result={}", msg, JSON.toJSONString(result));
            monitorService.sendTaskLog(taskId, websiteName, "登陆后-->处理-->失败", result);
            String newRemark = null;
            try {
                newRemark = OperatorUtils.getRemarkForTaskFail(taskId);
            } catch (Exception e) {
                logger.error("更新remark失败，taskId={}", taskId, e);
            }
            messageService.sendDirective(taskId, DirectiveEnum.TASK_FAIL.getCode(), newRemark);
            monitorService.sendTaskCompleteMsg(taskId, websiteName, ErrorCode.LOGIN_FAIL.getErrorCode(), ErrorCode.LOGIN_FAIL.getErrorMsg());
            return true;
        }
        try {
            OperatorPlugin pluginService = websiteOperatorService.getOperatorPluginService(websiteName, taskId);
            OperatorLoginPostPlugin postService = (OperatorLoginPostPlugin) pluginService;
            result = postService.loginPost(param);
            if (null != result && result.getStatus()) {
                logger.info("登陆后-->处理-->成功,taskId={},websiteName={}", taskId, websiteName);
                monitorService.sendTaskLog(taskId, websiteName, "登陆后-->处理-->成功");
                websiteOperatorService.sendOperatorCrawlerStartMessage(param.getTaskId(), param.getWebsiteName());
                logger.info("发送消息,启动爬虫,taskId={},websiteName={}", param.getTaskId(), param.getWebsiteName());
            } else {
                logger.warn("登陆后-->处理-->失败,taskId={},websiteName={},result={}", taskId, websiteName, JSON.toJSONString(result));
                String newRemark = null;
                try {
                    newRemark = OperatorUtils.getRemarkForTaskFail(taskId);
                } catch (Exception e) {
                    logger.error("更新remark失败，taskId={}", taskId, e);
                }
                messageService.sendDirective(taskId, DirectiveEnum.TASK_FAIL.getCode(), newRemark);
                monitorService.sendTaskCompleteMsg(taskId, websiteName, ErrorCode.LOGIN_FAIL.getErrorCode(), ErrorCode.LOGIN_FAIL.getErrorMsg());
            }
        } catch (Throwable e) {
            logger.error("登陆后-->处理-->失败,taskId={},websiteName={}", taskId, websiteName, e);
            monitorService.sendTaskLog(taskId, websiteName, "登陆后-->处理-->失败");
            String newRemark = null;
            try {
                newRemark = OperatorUtils.getRemarkForTaskFail(taskId);
            } catch (Exception ee) {
                logger.error("更新remark失败，taskId={}", taskId, ee);
            }
            messageService.sendDirective(taskId, DirectiveEnum.TASK_FAIL.getCode(), newRemark);
            monitorService.sendTaskCompleteMsg(taskId, websiteName, ErrorCode.LOGIN_FAIL.getErrorCode(), ErrorCode.LOGIN_FAIL.getErrorMsg());
        }
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
