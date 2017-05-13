/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved. Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.actor;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.NodeNameUtil;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.common.util.ThreadInterruptedUtil;
import com.datatrees.common.zookeeper.ZooKeeperClient;
import com.datatrees.common.zookeeper.watcher.AbstractLockerWatcher;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.ProcessorResult;
import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.proxy.ProxyManagerWithScope;
import com.datatrees.crawler.plugin.login.LoginTimeOutException;
import com.datatrees.rawdatacentral.collector.worker.CollectorWorker;
import com.datatrees.rawdatacentral.collector.worker.CollectorWorkerFactory;
import com.datatrees.rawdatacentral.core.common.ActorLockEventWatcher;
import com.datatrees.rawdatacentral.core.common.ProxySharedManager;
import com.datatrees.rawdatacentral.core.common.SimpleProxyManager;
import com.datatrees.rawdatacentral.core.common.UnifiedSysTime;
import com.datatrees.rawdatacentral.core.dao.RedisDao;
import com.datatrees.rawdatacentral.core.message.MessageFactory;
import com.datatrees.rawdatacentral.core.model.message.SubTaskAble;
import com.datatrees.rawdatacentral.core.model.message.TaskRelated;
import com.datatrees.rawdatacentral.core.model.message.TemplteAble;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.core.model.message.impl.ResultMessage;
import com.datatrees.rawdatacentral.core.model.message.impl.SubTaskCollectorMessage;
import com.datatrees.rawdatacentral.core.service.MessageService;
import com.datatrees.rawdatacentral.core.service.TaskService;
import com.datatrees.rawdatacentral.core.service.WebsiteService;
import com.datatrees.rawdatacentral.domain.common.Task;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
import com.datatrees.rawdatacentral.submitter.common.RedisKeyUtils;
import com.datatrees.rawdatacentral.submitter.common.SubmitConstant;
import com.datatrees.rawdatacentral.submitter.common.SubmitFile;
import com.datatrees.rawdatacentral.submitter.common.ZipCompressUtils;
import com.datatrees.rawdatacentral.submitter.filestore.oss.OssServiceProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月22日 下午7:35:45
 */
@Service
public class Collector {

    private static final Logger    logger                     = LoggerFactory.getLogger(Collector.class);

    @Resource
    private WebsiteService         websiteService;

    @Resource
    private TaskService            taskService;

    @Resource
    private MessageFactory         messageFactory;

    @Resource
    private MQProducer             producer;

    @Resource
    private ZooKeeperClient        zookeeperClient;

    @Resource
    private CollectorWorkerFactory collectorWorkerFactory;

    @Resource
    private RedisDao               redisDao;

    @Resource
    private MessageService         messageService;

    private static String          duplicateRemovedResultKeys = PropertiesConfiguration.getInstance()
        .get("duplicate.removed.result.keys", "bankbill");

    private static String          mqStatusTags               = PropertiesConfiguration.getInstance()
        .get("core.mq.status.tags", "bankbill,ecommerce,operator");

    private static String          mqMessageSendTagPattern    = PropertiesConfiguration.getInstance()
        .get("core.mq.message.sendTag.pattern", "opinionDetect|webDetect|businessLicense");

    private TaskMessage taskMessageInit(CollectorMessage message) {
        SearchProcessorContext context = null;
        Task task = new Task();
        if (message instanceof SubTaskCollectorMessage) {
            task.setSubTask(true);
        }
        try {
            task.setTaskId(message.getTaskId());
            task.setNodeName(NodeNameUtil.INSTANCE.getNodeName());
            context = websiteService.getSearchProcessorContext(message.getWebsiteName());
            context.setLoginCheckIgnore(message.isLoginCheckIgnore());
            context.set(AttributeKey.TASK_ID, message.getTaskId());
            context.set(AttributeKey.ACCOUNT_KEY, message.getTaskId() + "");
            context.set(AttributeKey.ACCOUNT_NO, message.getAccountNo());
            task.setWebsiteId(context.getWebsite().getId());
            task.setStartedAt(UnifiedSysTime.INSTANCE.getSystemTime());
            // init cookie
            if (StringUtils.isNotBlank(message.getCookie())) {
                ProcessorContextUtil.setCookieString(context, message.getCookie());
            }
            context.set(AttributeKey.END_URL, message.getEndURL());
            // 历史状态清理
            this.clearStatus(message.getTaskId());
        } catch (Exception e) {
            logger.error("taskMessageInit error taskId={},message={} ", message.getTaskId(), GsonUtils.toJson(message),
                e);
            task.setErrorCode(ErrorCode.TASK_INIT_ERROR, "TASK_INIT_ERROR error with " + message);
        }
        // insert task
        task.setId(taskService.insertTask(task));
        // set task unique sign
        ProcessorContextUtil.setTaskUnique(context, task.getId());
        TaskMessage taskMessage = new TaskMessage(task, context);
        taskMessage.setCollectorMessage(message);

        this.collectorMessageComplement(taskMessage, message, context);
        ProcessorContextUtil.addValues(context, message.getProperty());
        return taskMessage;
    }

    /**
     * 
     * @param taskId
     */
    private void clearStatus(long taskId) {
        String key = "verify_result_" + taskId;
        String getKey = "plugin_remark_" + taskId;
        redisDao.deleteKey(key);
        redisDao.deleteKey(getKey);
        logger.info("do verify_result and plugin_remark  data clear for taskId: {}", taskId);
    }

    private void collectorMessageComplement(TaskMessage taskMessage, CollectorMessage message,
                                            SearchProcessorContext context) {
        if (message instanceof TemplteAble) {
            taskMessage.setTemplateId(((TemplteAble) message).getTemplateId());
        }
        if (message instanceof TaskRelated) {
            taskMessage.setParentTaskID(((TaskRelated) message).getParentTaskID());
            ProcessorContextUtil.setValue(context, "parentTaskLogId", ((TaskRelated) message).getParentTaskID());
        }
        // set subtask parameter
        if (message instanceof SubTaskAble) {
            taskMessage.setMessageSend(!((SubTaskAble) message).isSynced());
            taskMessage.setStatusSend(!((SubTaskAble) message).noStatus());
            if (((SubTaskAble) message).getSubSeed() != null) {
                taskMessage.setUniqueSuffix(((SubTaskAble) message).getSubSeed().getUniqueSuffix());
                ProxyManager proxyManager = context.getProxyManager();
                if (((SubTaskAble) message).getSubSeed().getProxy() != null
                    && proxyManager instanceof ProxyManagerWithScope) {
                    ((ProxyManagerWithScope) proxyManager).setManager(new ProxySharedManager(
                        ((SubTaskAble) message).getSubSeed().getProxy(), new SimpleProxyManager()));
                }
            }
        }
    }

    private AbstractLockerWatcher actorLockWatchInit(TaskMessage taskMessage) {
        String websiteName = taskMessage.getWebsiteName();
        String templateId = taskMessage.getTemplateId();
        String uniqueSuffix = taskMessage.getUniqueSuffix();
        String serialNum = taskMessage.getCollectorMessage().getSerialNum();
        String taskId = taskMessage.getContext().getString(AttributeKey.TASK_ID);
        String accountNo = taskMessage.getContext().getString(AttributeKey.ACCOUNT_NO);
        //将同一网站的相同账号的线程kill
        if (StringUtils.isNotBlank(accountNo)) {
            String redisKey = websiteName + accountNo;
            String lastTaskId = redisDao.getRedisTemplate().opsForValue().getAndSet(redisKey, taskId);
            redisDao.getRedisTemplate().expire(redisKey, 10, TimeUnit.MINUTES);
            if (StringUtils.isNotBlank(lastTaskId)) {
                ActorLockEventWatcher watcher = new ActorLockEventWatcher("CollectorActor", lastTaskId,
                    Thread.currentThread(), zookeeperClient);
                watcher.cancel();
                logger.info("lastTaskId {} thread not needn't run", lastTaskId);
            }
        }
        String path = taskId;
        path = StringUtils.isNotBlank(templateId) ? path + "_" + templateId : path;
        path = StringUtils.isNotBlank(uniqueSuffix) ? path + "_" + uniqueSuffix : path;
        path = StringUtils.isNotBlank(serialNum) ? path + "_" + serialNum : path;
        AbstractLockerWatcher watcher = new ActorLockEventWatcher("CollectorActor", path, Thread.currentThread(),
            zookeeperClient);
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

    @SuppressWarnings("rawtypes")
    public ProcessorResult processMessage(CollectorMessage message) {
        logger.info("starting task worker for [" + message.toString() + "]");
        TaskMessage taskMessage = this.taskMessageInit((CollectorMessage) message);
        AbstractLockerWatcher watcher = null;
        // response to gateway already to process
        // this.responseReadyProcess(taskMessage, websiteTagSet);
        Map submitkeyResult = null;
        try {
            watcher = this.actorLockWatchInit(taskMessage);
            // init collectorWorker
            CollectorWorker collectorWorker = collectorWorkerFactory.getCollectorWorker(taskMessage);

            Set<String> websiteTagSet = new HashSet<String>(
                taskMessage.getContext().getWebsite().getSearchConfig().getResultTagList());

            // set status level 1 status
            taskMessage.getContext().getStatusContext().put(ResultMessage.LEVAL_1_STATUS,
                ((CollectorMessage) message).isLevel1Status());

            // do process
            submitkeyResult = collectorWorker.process(taskMessage);
            if (submitkeyResult == null && message instanceof TaskRelated) {
                logger.info("current task related to: " + ((TaskRelated) message).getParentTaskID()
                            + ", set empty submitkeyResult.");
                submitkeyResult = new HashMap();
            }
            Set<String> resultTagSet = collectorWorker.getResultTagSet();
            if (CollectionUtils.isEmpty(resultTagSet)) {
                if (CollectionUtils.isEmpty(message.getResultTagSet())) {
                    resultTagSet = websiteTagSet;
                } else {
                    resultTagSet = message.getResultTagSet();
                }
            }

            if (ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
                throw new InterruptedException(
                    "Thread interrupt check failed bafore result send to queue. threadId=" + Thread.currentThread().getId());
            } else {
                this.sendResult(taskMessage, submitkeyResult, resultTagSet);
            }
        } catch (Exception e) {
            logger.error("processMessage error taskId={}", taskMessage.getTask().getTaskId(), e);
            if (e instanceof LoginTimeOutException) {
                taskMessage.getTask().setErrorCode(ErrorCode.LOGIN_TIMEOUT_ERROR,
                    ErrorCode.LOGIN_TIMEOUT_ERROR.getErrorMessage() + " " + e.getMessage());
            } else if (e instanceof InterruptedException) {
                taskMessage.getTask().setErrorCode(ErrorCode.TASK_INTERRUPTED_ERROR,
                    ErrorCode.TASK_INTERRUPTED_ERROR.getErrorMessage() + " " + e.getMessage());
            } else {
                taskMessage.getTask().setErrorCode(ErrorCode.UNKNOWN_REASON, e.toString());
            }
        } finally {
            Task task = taskMessage.getTask();

            try {
                String logMsg = task.isSubTask() ? "子任务" : "";
                String redisKey = "run_count:" + task.getTaskId();
                long totalRun = 0;
                if (redisDao.getRedisTemplate().hasKey(redisKey)) {
                    totalRun = Long.valueOf(redisDao.getRedisTemplate().opsForValue().get(redisKey));
                }
                boolean isRepeatTask = totalRun > message.getTotalRun();
                logger.info("ready complete task taskId={},isSubTask={},newTotalRun={},oldTotalRun={},isRepeatTask={}",
                    task.getTaskId(), task.isSubTask(), totalRun, message.getTotalRun(), isRepeatTask);

                switch (taskMessage.getTask().getStatus()) {
                    case 0:
                        logMsg += "抓取成功";
                        break;
                    case 306:
                        logMsg = isRepeatTask ? "用户刷新任务或者重试," + logMsg + "抓取中断" : logMsg + "抓取中断";
                        break;
                    case 308:
                        logMsg += "抓取失败,登陆超时";
                        break;
                    default:
                        logMsg += "抓取失败";
                        break;
                }
                messageService.sendTaskLog(task.getTaskId(), logMsg);
                if (task.getStatus() != 0 && !isRepeatTask) {
                    Map<String, Object> directiveMap = new HashMap<String, Object>();
                    directiveMap.put("taskId", task.getTaskId());
                    directiveMap.put("directive", DirectiveEnum.TASK_FAIL.getCode());
                    Message directiveMsg = new Message();
                    directiveMsg.setTopic(TopicEnum.TASK_NEXT_DIRECTIVE.getCode());
                    directiveMsg.setBody(GsonUtils.toJson(directiveMap).getBytes());
                    producer.send(directiveMsg);
                }
            } catch (Exception e) {
                logger.error("send log status error", e);
            }
            this.actorLockWatchRelease(watcher);
            logger.info("task complete taskId={},isSubTask={},taskCode={},remark={},message={}", task.getTaskId(),
                task.isSubTask(), task.getStatus(), task.getRemark(), JSON.toJSONString(message));
        }
        this.messageComplement(taskMessage, (CollectorMessage) message);
        message.setFinish(true);// mark message finish
        taskService.updateTask(taskMessage.getTask());
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

            StringBuilder resultMessageBuilder = new StringBuilder();
            resultMessageBuilder.append("\"resultMsg\":").append(taskMessage.getTask().getResultMessage())
                .append(",\"processorLog\":").append(GsonUtils.toJson(taskMessage.getContext().getProcessorLog()));
            String startMsgJson = GsonUtils.toJson(message);

            if (startMsgJson.length() > PropertiesConfiguration.getInstance()
                .getInt("default.startMsgJson.length.threshold", 20000)) {
                String path = taskMessage.getTask().getTaskId() + "/" + taskMessage.getTask().getWebsiteId() + "/"
                              + taskMessage.getTask().getId();
                taskMessage.getTask().setResultMessage(resultMessageBuilder.toString() + ",startMsgOSSPath:" + path);
                SubmitFile file = new SubmitFile("startMsg.json", startMsgJson.getBytes());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    Map<String, SubmitFile> uploadMap = new HashMap<>();
                    uploadMap.put("startMsg.json", file);
                    ZipCompressUtils.compress(baos, uploadMap);
                    OssServiceProvider.getDefaultService().putObject(SubmitConstant.ALIYUN_OSS_DEFAULTBUCKET, path,
                        baos.toByteArray());
                } catch (Exception e) {
                    logger.error("upload startMsg.json error:" + e.getMessage(), e);
                } finally {
                    IOUtils.closeQuietly(baos);
                }
            } else {
                taskMessage.getTask()
                    .setResultMessage(resultMessageBuilder.toString() + ",\"startMsg\":" + startMsgJson);
            }
        } catch (Exception e) {
            logger.error("messageComplement error:" + e.getMessage(), e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void sendResult(TaskMessage taskMessage, Map submitkeyResult, Set<String> resultTagSet) {
        Task task = taskMessage.getTask();
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setRemark(GsonUtils.toJson(task));
        resultMessage.setTaskId(task.getTaskId());
        resultMessage.setWebsiteName(taskMessage.getWebsiteName());
        resultMessage.setWebsiteType(taskMessage.getContext().getWebsite().getWebsiteType());
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
                    result.put(key, RedisKeyUtils.genRedisKey(task.getTaskId(), key));
                }
            }
        }
        if (needSendToMQ) {
            if (submitkeyResult != null)
                result.putAll(submitkeyResult);
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
                        SendResult sendResult = producer.send(mqMessage);
                        logger.info("send message:" + mqMessage + "result:" + sendResult);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        task.setErrorCode(ErrorCode.RESULT_SEND_ERROR);
                    }
                }
            } else {
                logger.warn(taskMessage + "no need to submit result:" + submitkeyResult);
            }
            task.setResultMessage(GsonUtils.toJson(result));
        }

        this.sendTaskStatus(taskMessage, resultMessage, resultTagSet, notEmptyTag);
    }

    private void sendTaskStatus(TaskMessage taskMessage, ResultMessage resultMessage, Set<String> resultTagSet,
                                Set<String> notEmptyTag) {
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
                        Message mqMessage = messageFactory.getMessage("rawData_result_status", key,
                            GsonUtils.toJson(keyResult), "" + taskMessage.getTask().getId());
                        SendResult sendResult = producer.send(mqMessage);
                        logger.info("send result message:" + mqMessage + "result:" + sendResult);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        task.setErrorCode(ErrorCode.RESULT_SEND_ERROR);
                    }
                } else {
                    logger.warn(taskMessage + " no need to send status key:" + key + ",resultMessage:" + resultMessage);
                }
            }
        } else {
            logger.warn(taskMessage + " no need to send status:" + resultTagSet + ",resultMessage:" + resultMessage);
        }
    }

}
