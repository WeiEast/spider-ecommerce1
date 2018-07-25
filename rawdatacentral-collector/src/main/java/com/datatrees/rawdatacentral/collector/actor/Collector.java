/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved. Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.actor;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.common.util.ThreadInterruptedUtil;
import com.datatrees.common.zookeeper.ZooKeeperClient;
import com.datatrees.common.zookeeper.watcher.AbstractLockerWatcher;
import com.datatrees.crawler.core.domain.config.login.LoginType;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.ProcessorResult;
import com.datatrees.crawler.plugin.login.LoginTimeOutException;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.collector.utils.OperatorUtils;
import com.datatrees.rawdatacentral.collector.worker.CollectorWorker;
import com.datatrees.rawdatacentral.collector.worker.CollectorWorkerFactory;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.IpUtils;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.service.utils.WebsiteUtils;
import com.datatrees.rawdatacentral.core.common.ActorLockEventWatcher;
import com.datatrees.rawdatacentral.core.common.SubmitConstant;
import com.datatrees.rawdatacentral.core.common.UnifiedSysTime;
import com.datatrees.rawdatacentral.core.dao.RedisDao;
import com.datatrees.rawdatacentral.core.message.MessageFactory;
import com.datatrees.rawdatacentral.core.model.message.SubTaskAble;
import com.datatrees.rawdatacentral.core.model.message.TaskRelated;
import com.datatrees.rawdatacentral.core.model.message.TemplteAble;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.core.model.message.impl.ResultMessage;
import com.datatrees.rawdatacentral.core.oss.OssServiceProvider;
import com.datatrees.rawdatacentral.core.oss.OssUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.directive.DirectiveEnum;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.website.WebsiteType;
import com.datatrees.rawdatacentral.domain.model.Task;
import com.datatrees.rawdatacentral.service.TaskService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.submitter.common.RedisKeyUtils;
import com.datatrees.rawdatacentral.submitter.common.SubmitFile;
import com.datatrees.rawdatacentral.submitter.common.ZipCompressUtils;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月22日 下午7:35:45
 */
@Service
public class Collector {

    private static final Logger                 logger                     = LoggerFactory.getLogger(Collector.class);

    private static       String                 duplicateRemovedResultKeys = PropertiesConfiguration.getInstance()
            .get("duplicate.removed.result.keys", "bankbill");

    private static       String                 mqStatusTags               = PropertiesConfiguration.getInstance()
            .get("core.mq.status.tags", "bankbill,ecommerce,operator");

    private static       String                 mqMessageSendTagPattern    = PropertiesConfiguration.getInstance()
            .get("core.mq.message.sendTag.pattern", "opinionDetect|webDetect|businessLicense");

    @Resource
    private              WebsiteConfigService   websiteConfigService;

    @Resource
    private              TaskService            taskService;

    @Resource
    private              MessageFactory         messageFactory;

    @Resource
    private              DefaultMQProducer      defaultMQProducer;

    @Resource
    private              ZooKeeperClient        zookeeperClient;

    @Resource
    private              CollectorWorkerFactory collectorWorkerFactory;

    @Resource
    private              RedisDao               redisDao;

    @Resource
    private              MessageService         messageService;

    @Resource
    private              MonitorService         monitorService;

    private TaskMessage taskMessageInit(CollectorMessage message) {
        Task task = new Task();
        task.setTaskId(message.getTaskId());
        task.setWebsiteName(message.getWebsiteName());
        task.setNodeName(IpUtils.getLocalHostName());

        task.setOpenUrlCount(new AtomicInteger(0));
        task.setOpenPageCount(new AtomicInteger(0));
        task.setRequestFailedCount(new AtomicInteger(0));
        task.setRetryCount(new AtomicInteger(0));
        task.setFilteredCount(new AtomicInteger(0));
        task.setNetworkTraffic(new AtomicLong(0));
        task.setStatus(0);

        SearchProcessorContext context = websiteConfigService.getSearchProcessorContext(message.getTaskId(), message.getWebsiteName());
        context.setLoginCheckIgnore(message.isLoginCheckIgnore());
        context.set(AttributeKey.TASK_ID, message.getTaskId());
        context.set(AttributeKey.ACCOUNT_KEY, message.getTaskId() + "");
        //context.set(AttributeKey.ACCOUNT_NO, message.getAccountNo());

        task.setWebsiteId(context.getWebsite().getId());
        task.setStartedAt(UnifiedSysTime.INSTANCE.getSystemTime());
        task.setCreatedAt(UnifiedSysTime.INSTANCE.getSystemTime());
        // init cookie
        if (StringUtils.isNotBlank(message.getCookie())) {
            ProcessorContextUtil.setCookieString(context, message.getCookie());
        }
        context.set(AttributeKey.END_URL, message.getEndURL());
        // 历史状态清理
        this.clearStatus(message.getTaskId());
        taskService.insertTask(task);
        logger.info("task id is {}", task.getId());

        Map<String, String> shares = TaskUtils.getTaskShares(task.getTaskId());
        if (null != shares && !shares.isEmpty()) {
            for (Map.Entry<String, String> entry : shares.entrySet()) {
                context.set(entry.getKey(), entry.getValue());
            }
        }

        // set task unique sign
        ProcessorContextUtil.setTaskUnique(context, task.getId());

        TaskMessage taskMessage = new TaskMessage(task, context);
        taskMessage.setCollectorMessage(message);
        if (message instanceof TemplteAble) {
            taskMessage.setTemplateId(((TemplteAble) message).getTemplateId());
        }
        if (message instanceof TaskRelated) {
            taskMessage.setParentTaskID(((TaskRelated) message).getParentTaskID());
            ProcessorContextUtil.setValue(context, "parentTaskLogId", ((TaskRelated) message).getParentTaskID());
        }
        // set subtask parameter
        if (message instanceof SubTaskAble) {
            task.setSubTask(true);//标记子任务
            taskMessage.setMessageSend(!((SubTaskAble) message).isSynced());
            taskMessage.setStatusSend(!((SubTaskAble) message).noStatus());
            if (((SubTaskAble) message).getSubSeed() != null) {
                taskMessage.setUniqueSuffix(((SubTaskAble) message).getSubSeed().getUniqueSuffix());
                //                ProxyManager proxyManager = context.getProxyManager();
                //                if (((SubTaskAble) message).getSubSeed().getProxy() != null
                //                    && proxyManager instanceof ProxyManagerWithScope) {
                //                    ((ProxyManagerWithScope) proxyManager).setManager(new ProxySharedManager(
                //                        ((SubTaskAble) message).getSubSeed().getProxy(), new SimpleProxyManager()));
                //                }
            }
        }
        ProcessorContextUtil.addValues(context, message.getProperty());
        return taskMessage;
    }

    /**
     * @param taskId
     */
    private void clearStatus(long taskId) {
        //todo 这里会有问题,待处理
        String key = "verify_result_" + taskId;
        String getKey = "plugin_remark_" + taskId;
        redisDao.deleteKey(key);
        redisDao.deleteKey(getKey);
        logger.info("do verify_result and plugin_remark  data clear for taskId: {}", taskId);
    }

    private AbstractLockerWatcher actorLockWatchInit(TaskMessage taskMessage) {
        String templateId = taskMessage.getTemplateId();
        String uniqueSuffix = taskMessage.getUniqueSuffix();
        String serialNum = taskMessage.getCollectorMessage().getSerialNum();
        String taskId = taskMessage.getContext().getString(AttributeKey.TASK_ID);
        String path = taskId;
        path = StringUtils.isNotBlank(templateId) ? path + "_" + templateId : path;
        path = StringUtils.isNotBlank(uniqueSuffix) ? path + "_" + uniqueSuffix : path;
        path = StringUtils.isNotBlank(serialNum) ? path + "_" + serialNum : path;
        AbstractLockerWatcher watcher = new ActorLockEventWatcher("CollectorActor", path, Thread.currentThread(), zookeeperClient);
        zookeeperClient.registerWatcher(watcher);
        if (watcher.init()) {
            logger.info("Get actorLock begin to start ...");
        } else {
            logger.info("Lost actorLock,waiting for interrupt...");
        }
        return watcher;
    }

    private void actorLockWatchRelease(AbstractLockerWatcher watcher) {
        if (watcher != null) {
            zookeeperClient.unregisterWatcher(watcher);
            ThreadInterruptedUtil.clearInterrupted(Thread.currentThread());
            watcher.unLock();
        } else {
            logger.warn("watcher is empty ...");
        }
    }

    public ProcessorResult processMessage(CollectorMessage message) {
        TaskMessage taskMessage = null;
        Task task = null;
        AbstractLockerWatcher watcher = null;
        try {
            //初始化,生成上下文,保存task
            taskMessage = this.taskMessageInit(message);
            task = taskMessage.getTask();
            SearchProcessorContext context = taskMessage.getContext();
            //zookeeper做去重处理,后来的线程活着,ThreadInterruptedUtil.isInterrupted(Thread.currentThread())手动判断,并停止
            watcher = this.actorLockWatchInit(taskMessage);
            CollectorWorker collectorWorker = collectorWorkerFactory.getCollectorWorker(taskMessage);

            // set status level 1 status
            taskMessage.getContext().addStatusAttr(ResultMessage.LEVAL_1_STATUS, message.isLevel1Status());

            boolean needLogin = context.needLogin();
            LoginType loginType = context.getLoginConfig() != null ? context.getLoginConfig().getType() : null;
            logger.info("start process taskId={},needLogin={},loginType={},websiteName={}", task.getTaskId(), needLogin, loginType,
                    context.getWebsiteName());

            HttpResult<Boolean> loginResult = new HttpResult<>();
            if (needLogin) {
                loginResult = collectorWorker.doLogin(taskMessage);
                if (!loginResult.getStatus()) {
                    task.setStatus(loginResult.getResponseCode());
                    task.setRemark(loginResult.getMessage());
                }
            }

            boolean loginStatus = !needLogin || (null != loginResult && loginResult.getStatus());
            if (!task.isSubTask() && !context.needInteractive()) {
                messageService.sendTaskLog(task.getTaskId(), loginStatus ? "登陆成功" : "登陆失败");
            }

            if (loginStatus) {
                if (!task.isSubTask()) {
                    messageService.sendTaskLog(task.getTaskId(), "开始抓取");
                }
                HttpResult<Map<String, Object>> searchResult = collectorWorker.doSearch(taskMessage);
                if (!searchResult.getStatus()) {
                    task.setStatus(searchResult.getResponseCode());
                    task.setRemark(searchResult.getMessage());
                } else {
                    Map submitkeyResult = searchResult.getData();
                    if (submitkeyResult == null && message instanceof TaskRelated) {
                        logger.info("current task related to:{},set empty submitkeyResult.", ((TaskRelated) message).getParentTaskID());
                        submitkeyResult = new HashMap();
                    }
                    Set<String> resultTagSet = collectorWorker.getResultTagSet();

                    if (CollectionUtils.isEmpty(resultTagSet)) {
                        if (CollectionUtils.isEmpty(message.getResultTagSet())) {
                            resultTagSet = new HashSet<>(taskMessage.getContext().getWebsite().getSearchConfig().getResultTagList());
                        } else {
                            resultTagSet = message.getResultTagSet();
                        }
                    }
                    if (task.isSubTask() && MapUtils.isEmpty(submitkeyResult)) {
                        logger.info("skip send mq message threadId={},taskId={},websiteName={}", Thread.currentThread().getId(), task.getTaskId(),
                                task.getWebsiteName());
                    } else if (ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
                        logger.warn("Thread interrupt before result send to queue. threadId={},taskId={},websiteName={}",
                                Thread.currentThread().getId(), task.getTaskId(), task.getWebsiteName());
                        task.setStatus(ErrorCode.TASK_INTERRUPTED_ERROR.getErrorCode());
                        task.setRemark(ErrorCode.TASK_INTERRUPTED_ERROR.getErrorMsg());
                    } else {
                        this.sendResult(taskMessage, submitkeyResult, resultTagSet);
                    }

                }
            }
        } catch (Throwable e) {
            logger.error("processMessage error taskId={}", message.getTaskId(), e);
            if (null != taskMessage && null != taskMessage.getTask()) {
                if (e instanceof LoginTimeOutException) {
                    taskMessage.setErrorCode(ErrorCode.LOGIN_TIMEOUT_ERROR, ErrorCode.LOGIN_TIMEOUT_ERROR.getErrorMsg() + " " + e.getMessage());
                } else if (e instanceof InterruptedException) {
                    taskMessage.setErrorCode(ErrorCode.TASK_INTERRUPTED_ERROR, ErrorCode.TASK_INTERRUPTED_ERROR.getErrorMsg() + " " + e.getMessage());
                } else {
                    taskMessage.setErrorCode(ErrorCode.UNKNOWN_REASON, e.toString());
                }
            }
        } finally {
            if (null != watcher) {
                this.actorLockWatchRelease(watcher);
            }
            if (null != task) {
                if (!task.isSubTask()) {
                    String logMsg;
                    switch (taskMessage.getTask().getStatus()) {
                        case 0:
                            logMsg = "抓取成功";
                            break;
                        case 306:
                            logMsg = "抓取中断";
                            break;
                        case 308:
                            logMsg = "登陆超时";
                            break;
                        default:
                            logMsg = "抓取失败";
                            break;
                    }
                    messageService.sendTaskLog(task.getTaskId(), logMsg, task.getRemark());
                    if (task.getStatus() != 0) {
                        String newRemark = null;
                        try {
                            if (WebsiteUtils.isOperator(task.getWebsiteName())) {
                                newRemark = OperatorUtils.getRemarkForTaskFail(task.getTaskId());
                            }
                        } catch (Exception e) {
                            logger.error("更新remark失败，taskId={}", task.getTaskId(), e);
                        }
                        messageService.sendDirective(task.getTaskId(), DirectiveEnum.TASK_FAIL.getCode(), StringUtils.defaultString(newRemark));
                    }
                }
                logger.info("task complete taskId={},isSubTask={},taskId={},remark={},websiteName={},status={}", task.getTaskId(), task.isSubTask(),
                        task.getStatus(), task.getRemark(), task.getWebsiteName(), task.getStatus());
            }
        }
        this.messageComplement(taskMessage, message);
        message.setFinish(true);
        taskService.updateTask(task);
        if (null != task && !task.isSubTask()) {
            TaskUtils.addTaskShare(task.getTaskId(), RedisKeyPrefixEnum.FINISH_TIMESTAMP.getRedisKey(AttributeKey.CRAWLER),
                    System.currentTimeMillis() + "");
            monitorService.sendTaskCompleteMsg(task.getTaskId(), task.getWebsiteName(), task.getStatus(), task.getRemark());
        }
        return taskMessage.getContext().getProcessorResult();
    }

    private void messageComplement(TaskMessage taskMessage, CollectorMessage message) {
        try {
            if (StringUtils.isBlank(taskMessage.getTask().getResultMessage())) {
                taskMessage.getTask().setResultMessage(GsonUtils.toJson(taskMessage.getContext().getProcessorResult()));
            }
            // reset message result cookie
            if (StringUtils.isNotBlank(ProcessorContextUtil.getCookieString(taskMessage.getContext()))) {
                message.setCookie(ProcessorContextUtil.getCookieString(taskMessage.getContext()));
            }
            Map<String, Object> map = new HashMap<>();
            map.put("resultMsg", RedisUtils.hgetAll(RedisKeyPrefixEnum.TASK_RESULT.getRedisKey(taskMessage.getTaskId())));
            map.put("startMsg", message);

            String startMsgJson = GsonUtils.toJson(message);

            if (startMsgJson.length() > PropertiesConfiguration.getInstance().getInt("default.startMsgJson.length.threshold", 20000)) {
                String path = "task/" + taskMessage.getTask().getTaskId() + "/" + taskMessage.getTask().getWebsiteId() + "/" +
                        taskMessage.getTask().getId();
                map.put("startMsgOSSPath", path);
                SubmitFile file = new SubmitFile("startMsg.json", startMsgJson.getBytes());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    Map<String, SubmitFile> uploadMap = new HashMap<>();
                    uploadMap.put("startMsg.json", file);
                    ZipCompressUtils.compress(baos, uploadMap);
                    OssServiceProvider.getDefaultService()
                            .putObject(SubmitConstant.ALIYUN_OSS_DEFAULTBUCKET, OssUtils.getObjectKey(path), baos.toByteArray());
                } catch (Exception e) {
                    logger.error("upload startMsg.json error: {}", e.getMessage(), e);
                } finally {
                    IOUtils.closeQuietly(baos);
                }
            }
            taskMessage.getTask().setResultMessage(JSON.toJSONString(map, SerializerFeature.WriteDateUseDateFormat));
        } catch (Exception e) {
            logger.error("messageComplement error: {}", e.getMessage(), e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void sendResult(TaskMessage taskMessage, Map submitkeyResult, Set<String> resultTagSet) {
        logger.info("Starting send result. submit-key: {}, result-tag: {}", submitkeyResult, resultTagSet);
        Task task = taskMessage.getTask();
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setRemark(GsonUtils.toJson(task));
        resultMessage.setTaskId(task.getTaskId());
        resultMessage.setWebsiteName(taskMessage.getWebsiteName());
        resultMessage.setWebsiteType(WebsiteType.getWebsiteType(taskMessage.getContext().getWebsite().getWebsiteType()).getType());
        Set<String> notEmptyTag = new HashSet<String>();
        if (submitkeyResult != null) {
            resultMessage.setStatus("SUCCESS");
        } else {
            resultMessage.setStatus("FAIL");
        }

        boolean needSendToMQ = false;
        // build result message
        ProcessorResult result = taskMessage.getContext().getProcessorResult();
        // the same rule as redis key ,init all the possible key
        for (String tag : resultTagSet) {
            if (submitkeyResult != null || PatternUtils.match(mqMessageSendTagPattern, tag)) {
                needSendToMQ = true;
            }
            String keys = PropertiesConfiguration.getInstance().get("core.mq.tag." + tag + ".keys");
            if (StringUtils.isNotEmpty(keys)) {
                for (String key : keys.split(",")) {
                    result.put(key, RedisKeyUtils.genRedisKey(task.getTaskId(), task.getId(), key));
                }
            }
        }
        if (needSendToMQ) {
            if (submitkeyResult != null) result.putAll(submitkeyResult);
            result.putAll(taskMessage.getCollectorMessage().getSendBack());
            result.put("taskId", task.getTaskId());
            result.put("websiteName", resultMessage.getWebsiteName());
            result.put("websiteType", resultMessage.getWebsiteType());
            result.put("status", resultMessage.getStatus());
            result.put("statusCode", task.getStatus());

            if (taskMessage.getMessageSend()) {
                List<Message> mqMessages = messageFactory.getMessage(result, "" + taskMessage.getTask().getId());
                for (Message mqMessage : mqMessages) {
                    try {
                        notEmptyTag.add(mqMessage.getTags());
                        SendResult sendResult = defaultMQProducer.send(mqMessage);
                        logger.info("send message: {}, result: {}", mqMessage, sendResult);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        task.setErrorCode(ErrorCode.RESULT_SEND_ERROR);
                    }
                }
            } else {
                logger.warn("{} no need to submit result: {}", taskMessage, submitkeyResult);
            }
            task.setResultMessage(GsonUtils.toJson(result));
        }

        this.sendTaskStatus(taskMessage, resultMessage, resultTagSet, notEmptyTag);
    }

    private void sendTaskStatus(TaskMessage taskMessage, ResultMessage resultMessage, Set<String> resultTagSet, Set<String> notEmptyTag) {
        if (taskMessage.getMessageSend() && taskMessage.getStatusSend()) {
            Task task = taskMessage.getTask();
            for (String key : resultTagSet) {
                if (mqStatusTags.contains(key)) {
                    ResultMessage keyResult = new ResultMessage();
                    keyResult.putAll(resultMessage);
                    if (duplicateRemovedResultKeys.contains(key)) {
                        keyResult.setResultEmpty(!task.isDuplicateRemoved() && !notEmptyTag.contains(key));
                    } else {
                        keyResult.setResultEmpty(!notEmptyTag.contains(key));
                    }
                    try {
                        Message mqMessage = messageFactory
                                .getMessage("rawData_result_status", key, GsonUtils.toJson(keyResult), "" + taskMessage.getTask().getId());
                        SendResult sendResult = defaultMQProducer.send(mqMessage);
                        logger.info("send result message: {}, result: {}", mqMessage, sendResult);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        task.setErrorCode(ErrorCode.RESULT_SEND_ERROR);
                    }
                } else {
                    logger.warn("{} no need to send status key: {}, resultMessage: {}", taskMessage, key, resultMessage);
                }
            }
        } else {
            logger.warn("{} no need to send status: {}, resultMessage: {}", taskMessage, resultTagSet, resultMessage);
        }
    }

}
