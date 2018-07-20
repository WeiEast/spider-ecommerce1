package com.datatrees.spider.operator.service.mq.handler;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.collector.utils.OperatorUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.spider.operator.domain.model.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
import com.datatrees.rawdatacentral.domain.enums.TopicTag;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.HttpResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorPluginPostService;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.rawdatacentral.service.mq.MessageHandler;
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
    private              CrawlerOperatorService crawlerOperatorService;

    @Resource
    private              ClassLoaderService     classLoaderService;

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
        HttpResult<Map<String, Object>> result = crawlerOperatorService.checkParams(param);
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
            OperatorPluginService pluginService = classLoaderService.getOperatorPluginService(websiteName, taskId);
            OperatorPluginPostService postService = (OperatorPluginPostService) pluginService;
            result = postService.loginPost(param);
            if (null != result && result.getStatus()) {
                logger.info("登陆后-->处理-->成功,taskId={},websiteName={}", taskId, websiteName);
                monitorService.sendTaskLog(taskId, websiteName, "登陆后-->处理-->成功");
                messageService.sendOperatorCrawlerStartMessage(param.getTaskId(), param.getWebsiteName());
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
        return TopicEnum.RAWDATA_INPUT.getCode();
    }

}
