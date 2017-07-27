/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved. Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.actor;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.producer.MQProducer;
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
import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.proxy.ProxyManagerWithScope;
import com.datatrees.crawler.plugin.login.LoginTimeOutException;
import com.datatrees.rawdatacentral.collector.chain.common.WebsiteType;
import com.datatrees.rawdatacentral.collector.worker.CollectorWorker;
import com.datatrees.rawdatacentral.collector.worker.CollectorWorkerFactory;
import com.datatrees.rawdatacentral.common.utils.IpUtils;
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
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.model.Task;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.TaskService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.share.MessageService;
import com.datatrees.rawdatacentral.submitter.common.RedisKeyUtils;
import com.datatrees.rawdatacentral.submitter.common.SubmitConstant;
import com.datatrees.rawdatacentral.submitter.common.SubmitFile;
import com.datatrees.rawdatacentral.submitter.common.ZipCompressUtils;
import com.datatrees.rawdatacentral.submitter.filestore.oss.OssServiceProvider;
import com.datatrees.rawdatacentral.submitter.filestore.oss.OssUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月22日 下午7:35:45
 */
@Service
public class Collector {

    private static final Logger    logger                     = LoggerFactory.getLogger(Collector.class);

    @Resource
    private WebsiteConfigService   websiteConfigService;

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

    @Resource
    private RedisService           redisService;

    private static String          duplicateRemovedResultKeys = PropertiesConfiguration.getInstance()
        .get("duplicate.removed.result.keys", "bankbill");

    private static String          mqStatusTags               = PropertiesConfiguration.getInstance()
        .get("core.mq.status.tags", "bankbill,ecommerce,operator");

    private static String          mqMessageSendTagPattern    = PropertiesConfiguration.getInstance()
        .get("core.mq.message.sendTag.pattern", "opinionDetect|webDetect|businessLicense");

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

        SearchProcessorContext context = websiteConfigService.getSearchProcessorContext(message.getWebsiteName());
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
        taskService.insertTask(task);

        Map<String, String> shares = redisService.getTaskShares(task.getTaskId());
        if(null != shares && ! shares.isEmpty()){
            for(Map.Entry<String,String> entry : shares.entrySet()){
                context.set(entry.getKey(),entry.getValue());
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
                ProxyManager proxyManager = context.getProxyManager();
                if (((SubTaskAble) message).getSubSeed().getProxy() != null
                    && proxyManager instanceof ProxyManagerWithScope) {
                    ((ProxyManagerWithScope) proxyManager).setManager(new ProxySharedManager(
                        ((SubTaskAble) message).getSubSeed().getProxy(), new SimpleProxyManager()));
                }
            }
        }
        ProcessorContextUtil.addValues(context, message.getProperty());
        return taskMessage;
    }

    /**
     * 
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
        String websiteName = taskMessage.getWebsiteName();
        String templateId = taskMessage.getTemplateId();
        String uniqueSuffix = taskMessage.getUniqueSuffix();
        String serialNum = taskMessage.getCollectorMessage().getSerialNum();
        String taskId = taskMessage.getContext().getString(AttributeKey.TASK_ID);
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

    public ProcessorResult processMessage(CollectorMessage message) {
        TaskMessage taskMessage = null;
        Task task = null;
        SearchProcessorContext context = null;
        AbstractLockerWatcher watcher = null;
        try {
            //初始化,生成上下文,保存task
            taskMessage = this.taskMessageInit(message);
            task = taskMessage.getTask();
            context = taskMessage.getContext();
            //zookepper做去重处理,后来的线程活着,ThreadInterruptedUtil.isInterrupted(Thread.currentThread())手动判断,并停止
            watcher = this.actorLockWatchInit(taskMessage);
            CollectorWorker collectorWorker = collectorWorkerFactory.getCollectorWorker(taskMessage);

            // set status level 1 status
            taskMessage.getContext().getStatusContext().put(ResultMessage.LEVAL_1_STATUS, message.isLevel1Status());

            boolean needLogin = context.needLogin();
            LoginType loginType = context.getLoginConfig() != null ? context.getLoginConfig().getType() : null;
            logger.info("start process taskId={},needLogin={},loginType={},websiteName={}", task.getTaskId(), needLogin,
                loginType, context.getWebsiteName());

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
                        logger.info("current task related to:{},set empty submitkeyResult.",
                            ((TaskRelated) message).getParentTaskID());
                        submitkeyResult = new HashMap();
                    }
                    Set<String> resultTagSet = collectorWorker.getResultTagSet();

                    if (CollectionUtils.isEmpty(resultTagSet)) {
                        if (CollectionUtils.isEmpty(message.getResultTagSet())) {
                            resultTagSet = new HashSet<String>(
                                taskMessage.getContext().getWebsite().getSearchConfig().getResultTagList());
                        } else {
                            resultTagSet = message.getResultTagSet();
                        }
                    }
                    if (ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
                        logger.error(
                            "Thread interrupt bafore result send to queue. threadId={},taskId={},websiteName={}",
                            Thread.currentThread().getId(), task.getTaskId(), task.getWebsiteName());
                        task.setStatus(ErrorCode.TASK_INTERRUPTED_ERROR.getErrorCode());
                        task.setRemark(ErrorCode.TASK_INTERRUPTED_ERROR.getErrorMessage());
                    } else {
                        this.sendResult(taskMessage, submitkeyResult, resultTagSet);
                    }

                }
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
            if (!task.isSubTask()) {
                String redisKey = "run_count_" + task.getTaskId();
                long totalRun = 0;
                if (redisDao.getRedisTemplate().hasKey(redisKey)) {
                    totalRun = Long.valueOf(redisDao.getRedisTemplate().opsForValue().get(redisKey));
                }
                boolean isRepeatTask = totalRun > message.getTotalRun();
                logger.info("ready complete task taskId={},newTotalRun={},oldTotalRun={},isRepeatTask={}",
                    task.getTaskId(), totalRun, message.getTotalRun(), isRepeatTask);

                String logMsg = null;
                switch (taskMessage.getTask().getStatus()) {
                    case 0:
                        logMsg = "抓取成功";
                        break;
                    case 306:
                        logMsg = isRepeatTask ? "用户刷新任务或者重试,抓取中断" : "抓取中断";
                        break;
                    case 308:
                        logMsg = "登陆超时";
                        break;
                    default:
                        logMsg = "抓取失败";
                        break;
                }
                messageService.sendTaskLog(task.getTaskId(), logMsg, task.getRemark());
                if (task.getStatus() != 0 && !isRepeatTask) {
                    messageService.sendDirective(task.getTaskId(), DirectiveEnum.TASK_FAIL.getCode(), null);
                }
            }
            this.actorLockWatchRelease(watcher);
            logger.info("task complete taskId={},isSubTask={},taskId={},remark={},websiteName={},status={}",
                task.getTaskId(), task.isSubTask(), task.getStatus(), task.getRemark(), task.getWebsiteName(),
                task.getStatus());
        }
        this.messageComplement(taskMessage, message);
        message.setFinish(true);
        taskService.updateTask(task);
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
                String path = "task/" + taskMessage.getTask().getTaskId() + "/" + taskMessage.getTask().getWebsiteId()
                              + "/" + taskMessage.getTask().getId();
                taskMessage.getTask().setResultMessage(resultMessageBuilder.toString() + ",startMsgOSSPath:" + path);
                SubmitFile file = new SubmitFile("startMsg.json", startMsgJson.getBytes());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    Map<String, SubmitFile> uploadMap = new HashMap<>();
                    uploadMap.put("startMsg.json", file);
                    ZipCompressUtils.compress(baos, uploadMap);
                    OssServiceProvider.getDefaultService().putObject(SubmitConstant.ALIYUN_OSS_DEFAULTBUCKET,
                        OssUtils.getObjectKey(path), baos.toByteArray());
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
        resultMessage.setWebsiteType(
            WebsiteType.getWebsiteType(taskMessage.getContext().getWebsite().getWebsiteType()).getType());
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
                    result.put(key, RedisKeyUtils.genRedisKey(task.getId(), key));
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
