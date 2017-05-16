/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.worker;

import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.util.Timeout;
import com.datatrees.common.actor.WrappedActorRef;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.login.LoginType;
import com.datatrees.crawler.core.domain.config.search.Request;
import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.SourceUtil;
import com.datatrees.crawler.core.processor.common.exception.ResponseCheckException;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.login.Login;
import com.datatrees.rawdatacentral.collector.actor.TaskMessage;
import com.datatrees.rawdatacentral.collector.common.CollectorConstants;
import com.datatrees.rawdatacentral.collector.search.CrawlExcutorHandler;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.collector.worker.deduplicate.DuplicateChecker;
import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.core.common.UnifiedSysTime;
import com.datatrees.rawdatacentral.core.dao.RedisDao;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.core.service.MessageService;
import com.datatrees.rawdatacentral.core.subtask.SubTaskManager;
import com.datatrees.rawdatacentral.domain.common.Task;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.ExtractCode;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.submitter.common.RedisKeyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午9:16:04
 */
public class CollectorWorker {

    private static final Logger LOGGER                    = LoggerFactory.getLogger(CollectorWorker.class);

    private Integer             maxExecuteMinutes         = PropertiesConfiguration.getInstance()
        .getInt("default.collector.execute.minutes", 7);

    private Integer             interactiveTimeoutSeconds = PropertiesConfiguration.getInstance()
        .getInt("interactive.timeout.seconds", 300);

    private CrawlExcutorHandler crawlExcutorHandler;

    private ResultDataHandler   resultDataHandler;

    private WrappedActorRef     extractorActorRef;

    private DuplicateChecker    duplicateChecker;

    private SubTaskManager      subTaskManager;

    private Set<String>         resultTagSet              = new HashSet<String>();

    private List<LinkNode>      initLinkNodeList;

    private RedisDao            redisDao;

    private final long          maxLiveTime               = TimeUnit.SECONDS
        .toMillis(PropertiesConfiguration.getInstance().getInt("max.live.seconds", 30));

    private MessageService      messageService;

    public Map<String, Object> process(TaskMessage taskMessage) throws InterruptedException {
        Task task = taskMessage.getTask();
        SearchProcessorContext context = taskMessage.getContext();
        try {
            boolean needLogin = context.needLogin();
            LoginType loginType = context.getLoginConfig() != null ? context.getLoginConfig().getType() : null;

            LOGGER.info("start process taskId={},needLogin={},loginType={},websiteName={}", task.getTaskId(), needLogin,
                loginType, context.getWebsiteName());
            if (needLogin) {
                boolean loginStatus = doLogin(taskMessage);
                if (!task.isSubTask()) {
                    messageService.sendTaskLog(task.getTaskId(), loginStatus ? "登陆成功" : "登陆失败");
                }
                if (!loginStatus) {
                    return null;
                }
            } else {
                if (!task.isSubTask()) {
                    messageService.sendTaskLog(task.getTaskId(), "接收到登陆成功信息");
                }
            }
            //
            // // set accout,just use email account for temporary
            // task.setAccount(ReplaceUtils.replaceMapWithCheck(context.getContext(),
            // "${emailAccount}"));

            // task begin
            messageService.sendTaskLog(task.getTaskId(), task.isSubTask() ? "子任务开始抓取" : "开始抓取");
            List<Future<Object>> futureList = new ArrayList<Future<Object>>();
            if (!doSearch(taskMessage, futureList)) {
                return null;
            }

            Map<String, Object> resultMap = this.futureResultHander(futureList, taskMessage);
            return mergeSubTaskResult(task.getId(), resultMap);
        } catch (Exception e) {
            LOGGER.error("process error websiteName={}", context.getWebsiteName(), e);
            if (e instanceof InterruptedException) {
                throw (InterruptedException) e;
            }
        } finally {
            LOGGER.info("task " + task.getId() + " collect finish ...");
            task.setFinishedAt(UnifiedSysTime.INSTANCE.getSystemTime());
            task.setDuration((task.getFinishedAt().getTime() - task.getStartedAt().getTime()) / 1000);
            context.release();
        }
        return null;
    }

    /**
     * 登录
     * 
     * @param taskMessage
     * @return
     * @throws InterruptedException
     */
    private boolean doLogin(TaskMessage taskMessage) throws InterruptedException {
        Task task = taskMessage.getTask();
        SearchProcessorContext context = taskMessage.getContext();
        try {
            if (context.needInteractive()) {
                String keyString = RedisKeyUtils.genCollectorMessageRedisKey(taskMessage.getCollectorMessage());
                String result = redisDao.pullResult(keyString);
                if (StringUtils.isNotBlank(result)) {
                    LOGGER.warn("Give up retry for " + taskMessage + " , " + taskMessage.getCollectorMessage());
                    task.setErrorCode(ErrorCode.GIVE_UP_RETRY);
                    return false;
                }
                if ((System.currentTimeMillis() - taskMessage.getCollectorMessage().getBornTimestamp()) > maxLiveTime) {
                    LOGGER.warn("Message bornTime out ,system is busy now ,drop message bornTime :{} ...",
                        new Date(taskMessage.getCollectorMessage().getBornTimestamp()));
                    task.setErrorCode(ErrorCode.MESSAGE_DROP,
                        "Message drop! Born at " + taskMessage.getCollectorMessage().getBornTimestamp());
                    return false;
                }

                redisDao.pushMessage(keyString, keyString, interactiveTimeoutSeconds);
                Login.INSTANCE.doLogin(context);
                if (!context.getLoginStatus().equals(Login.Status.SUCCEED)) {
                    task.setErrorCode(ErrorCode.COOKIE_INVALID);
                    return false;
                }
            } else {
                Login.INSTANCE.doLogin(context);
                if (!context.getLoginStatus().equals(Login.Status.SUCCEED)) {
                    task.setErrorCode(ErrorCode.COOKIE_INVALID);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("login error websiteName={}", context.getWebsiteName(), e);
            if (e instanceof InterruptedException) {
                throw (InterruptedException) e;
            }
            // 用户中断任务不算失败
            if (e instanceof ResultEmptyException) {
                task.setErrorCode(ErrorCode.NOT_EMPTY_ERROR_CODE, e.getMessage());
            } else {
                task.setErrorCode(ErrorCode.COOKIE_INVALID);
            }
            // 登录失败终止任务
            return false;
        } finally {
        }
    }

    /**
     * 登录
     *
     * @param taskMessage
     * @return
     * @throws InterruptedException
     */
    public HttpResult<Boolean> doLogin2(TaskMessage taskMessage) throws InterruptedException {
        HttpResult<Boolean> loginResult = new HttpResult<>();
        Task task = taskMessage.getTask();
        SearchProcessorContext context = taskMessage.getContext();
        try {
            if (context.needInteractive()) {
                String keyString = RedisKeyUtils.genCollectorMessageRedisKey(taskMessage.getCollectorMessage());
                String result = redisDao.pullResult(keyString);
                //重复消息 or 指令等待中
                if (StringUtils.isNotBlank(result)) {
                    LOGGER.warn("Give up retry ,taskId={},websiteName={} ", task.getTaskId(), task.getWebsiteName());
                    return loginResult.failure(ErrorCode.GIVE_UP_RETRY);
                }
                //消息延迟了
                if ((System.currentTimeMillis() - taskMessage.getCollectorMessage().getBornTimestamp()) > maxLiveTime) {
                    LOGGER.warn("Message drop! Born at  bornTime = {} ,maxLiveTime ={},taskId={},websiteName={}",
                        DateUtils.formatYmdhms(taskMessage.getCollectorMessage().getBornTimestamp()), maxLiveTime,
                        task.getTaskId(), task.getWebsiteName());
                    return loginResult.failure(ErrorCode.MESSAGE_DROP);
                }
                redisDao.pushMessage(keyString, keyString, interactiveTimeoutSeconds);
            }
            Login.INSTANCE.doLogin(context);
            if (!context.getLoginStatus().equals(Login.Status.SUCCEED)) {
                LOGGER.warn("login fail taskId={},websiteName={} ", task.getTaskId(), task.getWebsiteName());
                return loginResult.failure(ErrorCode.COOKIE_INVALID);
            }
            LOGGER.info("login success taskId={},websiteName={} ", task.getTaskId(), task.getWebsiteName());
            return loginResult.success(true);
        } catch (Exception e) {
            LOGGER.error("login error taskId={},websiteName={} ", task.getTaskId(), task.getWebsiteName(), e);
            if (e instanceof InterruptedException) {
                return loginResult.failure(ErrorCode.TASK_INTERRUPTED_ERROR);
            }
            if (e instanceof ResultEmptyException) {
                return loginResult.failure(ErrorCode.NOT_EMPTY_ERROR_CODE.getErrorCode(), e.getMessage());
            }
            return loginResult.failure(ErrorCode.COOKIE_INVALID);
        }
    }

    /**
     * 爬取数据
     *
     * @param taskMessage
     * @return
     */
    public HttpResult<Map<String, Object>> doSearch2(TaskMessage taskMessage) throws Exception {
        HttpResult<Map<String, Object>> searchResult = new HttpResult<>();
        Task task = taskMessage.getTask();
        SearchProcessorContext context = taskMessage.getContext();
        try {
            Collection<SearchTemplateConfig> templateList = context.getWebsite().getSearchConfig()
                .getSearchTemplateConfigList();
            if (CollectionUtils.isEmpty(templateList)) {
                LOGGER.error("templateList is empty taskId={}, websiteName={}", task.getTaskId(),
                    task.getWebsiteName());
                return searchResult.failure(ErrorCode.CONFIG_ERROR);
            }
            String templateId = taskMessage.getTemplateId();
            List<Future<Object>> futureList = new ArrayList<Future<Object>>();
            for (SearchTemplateConfig templateConfig : templateList) {
                if (StringUtils.isNotBlank(templateId)) {
                    if (!templateId.contains(templateConfig.getId())) {
                        LOGGER.warn("filter template ,taskId={},templateId={} filter configId={},websiteName={}",
                            task.getTaskId(), templateId, templateConfig.getId(), task.getWebsiteName());
                        continue;
                    }
                } else {
                    if (BooleanUtils.isNotTrue(templateConfig.getAutoStart())) {
                        LOGGER.warn("filter template ,taskId={},templateId={} filter configId={},websiteName={}",
                            task.getTaskId(), templateId, templateConfig.getId(), task.getWebsiteName());
                        continue;
                    }
                }
                try {
                    String searchTemplate = null;

                    Request request = templateConfig.getRequest();
                    if (null == request) {
                        LOGGER.warn("request is empty ,taskId={},templateId={},configId={},websiteName={}",
                            task.getTaskId(), templateId, templateConfig.getId(), task.getWebsiteName());
                        continue;
                    }
                    // get from context ,seedurl may given by sub task or other external task
                    Object templateUrl = ProcessorContextUtil.getValue(context, templateConfig.getId());
                    if (templateUrl != null) {
                        searchTemplate = templateUrl.toString();
                    } else if (CollectionUtils.isNotEmpty(request.getSearchTemplateList())) {
                        searchTemplate = request.getSearchTemplateList().get(0);
                    }

                    if (StringUtils.isEmpty(searchTemplate)) {
                        LOGGER.warn(
                            "searchTemplate is empty or null,taskId={},templateId={},configId={},websiteName={}",
                            task.getTaskId(), templateId, templateConfig.getId(), task.getWebsiteName());
                        continue;
                    }
                    searchTemplate = ReplaceUtils.replaceMap(context.getContext(), searchTemplate);
                    maxExecuteMinutes = request.getMaxExecuteMinutes() != null ? request.getMaxExecuteMinutes()
                        : maxExecuteMinutes;
                    String headerString = request.getDefaultHeader();
                    if (StringUtils.isNotBlank(headerString)) {
                        headerString = SourceUtil.sourceExpression(context.getContext(), headerString);
                        Map<String, String> defaultHeader = (Map<String, String>) GsonUtils.fromJson(headerString,
                            Map.class);
                        if (MapUtils.isNotEmpty(defaultHeader)) {
                            context.getDefaultHeader().putAll(defaultHeader);
                        }
                    }
                    // get all the possible result tag
                    resultTagSet.addAll(templateConfig.getResultTagList());
                    SearchProcessor searchProcessor = new SearchProcessor(taskMessage).setSearchTemplate(searchTemplate)
                        .setTemplateId(templateConfig.getId()).setSearchTemplateConfig(templateConfig)
                        .setMaxExecuteMinutes(maxExecuteMinutes).setResultDataHandler(resultDataHandler)
                        .setExtractorActorRef(extractorActorRef).setDuplicateChecker(duplicateChecker)
                        .setInitLinkNodeList(initLinkNodeList);

                    crawlExcutorHandler.crawlExecutor(searchProcessor);
                    futureList.addAll(searchProcessor.getFutureList());
                } catch (Exception e) {
                    // 除了中断,内容为空,字段为空等错误,任务不算失败
                    LOGGER.error("doSearch error taskId={}, websiteName={}", task.getTaskId(), task.getWebsiteName(),
                        e);
                    if (e instanceof InterruptedException) {
                        return searchResult.failure(ErrorCode.TASK_INTERRUPTED_ERROR);
                    }
                    if (e instanceof ResponseCheckException) {
                        return searchResult.failure(ErrorCode.RESPONSE_EMPTY_ERROR_CODE.getErrorCode(), e.getMessage());
                    }
                    if (e instanceof ResultEmptyException) {
                        return searchResult.failure(ErrorCode.NOT_EMPTY_ERROR_CODE.getErrorCode(), e.getMessage());
                    }
                }
            }
            Map<String, Object> resultMap = futureResultHander(futureList, taskMessage);
            Map<String, Object> submitkeyResult = mergeSubTaskResult(task.getId(), resultMap);
            LOGGER.error("doSearch success taskId={}, websiteName={}", task.getTaskId(), task.getWebsiteName());
            return searchResult.success(submitkeyResult);
        } catch (Exception e) {
            LOGGER.error("doSearch error taskId={}, websiteName={}", task.getTaskId(), task.getWebsiteName(), e);
            return searchResult.failure();
        } finally {
            task.setFinishedAt(UnifiedSysTime.INSTANCE.getSystemTime());
            task.setDuration((task.getFinishedAt().getTime() - task.getStartedAt().getTime()) / 1000);
            context.release();
        }
    }

    /**
     * 爬取数据
     * 
     * @param taskMessage
     * @param futureList
     * @return
     */
    private boolean doSearch(TaskMessage taskMessage, List<Future<Object>> futureList) throws Exception {
        Task task = taskMessage.getTask();
        SearchProcessorContext context = taskMessage.getContext();
        try {
            Collection<SearchTemplateConfig> templateList = context.getWebsite().getSearchConfig()
                .getSearchTemplateConfigList();
            if (CollectionUtils.isEmpty(templateList)) {
                LOGGER.error("templateList is empty," + "websiteId:" + task.getWebsiteId());
                task.setErrorCode(ErrorCode.CONFIG_ERROR);
                return false;
            }
            String templateId = taskMessage.getTemplateId();

            for (SearchTemplateConfig templateConfig : templateList) {
                if (StringUtils.isNotBlank(templateId)) {
                    if (!templateId.contains(templateConfig.getId())) {
                        LOGGER.warn(taskMessage + " filter template:" + templateConfig);
                        continue;
                    }
                } else {
                    if (BooleanUtils.isNotTrue(templateConfig.getAutoStart())) {
                        LOGGER.warn(taskMessage + " filter template:" + templateConfig);
                        continue;
                    }
                }
                try {
                    String searchTemplate = null;

                    Request request = templateConfig.getRequest();
                    if (null == request) {
                        LOGGER.warn(templateConfig + "'s request is empty !");
                        continue;
                    }
                    // get from context ,seedurl may given by sub task or other external task
                    Object templateUrl = ProcessorContextUtil.getValue(context, templateConfig.getId());
                    if (templateUrl != null) {
                        searchTemplate = templateUrl.toString();
                    } else if (CollectionUtils.isNotEmpty(request.getSearchTemplateList())) {
                        searchTemplate = request.getSearchTemplateList().get(0);
                    }

                    if (StringUtils.isEmpty(searchTemplate)) {
                        LOGGER.error(
                            taskMessage + " searchTemplate is empty or null , websiteId : . " + task.getWebsiteId());
                        continue;
                    }
                    searchTemplate = ReplaceUtils.replaceMap(context.getContext(), searchTemplate);
                    maxExecuteMinutes = request.getMaxExecuteMinutes() != null ? request.getMaxExecuteMinutes()
                        : maxExecuteMinutes;
                    String headerString = request.getDefaultHeader();
                    if (StringUtils.isNotBlank(headerString)) {
                        headerString = SourceUtil.sourceExpression(context.getContext(), headerString);
                        Map<String, String> defaultHeader = (Map<String, String>) GsonUtils.fromJson(headerString,
                            Map.class);
                        if (MapUtils.isNotEmpty(defaultHeader)) {
                            context.getDefaultHeader().putAll(defaultHeader);
                        }
                    }
                    // get all the possible result tag
                    resultTagSet.addAll(templateConfig.getResultTagList());
                    SearchProcessor searchProcessor = new SearchProcessor(taskMessage).setSearchTemplate(searchTemplate)
                        .setTemplateId(templateConfig.getId()).setSearchTemplateConfig(templateConfig)
                        .setMaxExecuteMinutes(maxExecuteMinutes).setResultDataHandler(resultDataHandler)
                        .setExtractorActorRef(extractorActorRef).setDuplicateChecker(duplicateChecker)
                        .setInitLinkNodeList(initLinkNodeList);

                    crawlExcutorHandler.crawlExecutor(searchProcessor);
                    futureList.addAll(searchProcessor.getFutureList());
                } catch (Exception e) {
                    // 允许部分失败(除了抛出异常)
                    LOGGER.error("doSearch error websiteName={}", context.getWebsiteName(), e);
                    if (e instanceof InterruptedException) {
                        throw (InterruptedException) e;
                    }
                    if (e instanceof ResponseCheckException) {
                        task.setErrorCode(ErrorCode.RESPONSE_EMPTY_ERROR_CODE, e.getMessage());
                        throw (ResultEmptyException) e;
                    }
                    if (e instanceof ResultEmptyException) {
                        task.setErrorCode(ErrorCode.NOT_EMPTY_ERROR_CODE, e.getMessage());
                        throw (ResultEmptyException) e;
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw (InterruptedException) e;
            } else {
            }
            throw e;
        } finally {
        }
        return true;
    }

    public Map<String, Object> futureResultHander(List<Future<Object>> futureList, TaskMessage taskMessage) {
        Map<String, Object> submitkeyResult = new HashMap();
        try {
            Task task = taskMessage.getTask();
            ExtractCount extractCount = new ExtractCount();
            Exception exception = null;
            for (Future future : futureList) {
                try {
                    ExtractMessage message = (ExtractMessage) Await.result(future,
                        new Timeout(CollectorConstants.EXTRACT_ACTOR_TIMEOUT).duration());
                    this.extractCodeCount(extractCount, message, submitkeyResult);
                } catch (Exception e) {
                    exception = e;
                    extractCount.extractFailedCount++;
                    LOGGER.error("get extract future result error " + e.getMessage(), e);
                }
            }
            if (extractCount.extractSucceedCount == 0 && extractCount.extractFailedCount > 0) {
            } else {
            }
            task.setExtractedCount(extractCount.extractedCount);
            task.setExtractFailedCount(extractCount.extractFailedCount);
            task.setNotExtractCount(extractCount.notExtractCount);
            task.setExtractSucceedCount(extractCount.extractSucceedCount);
            task.setStoreFailedCount(extractCount.storeFailedCount);
        } catch (Exception e) {
            throw e;
        } finally {
        }
        return submitkeyResult;
    }

    public Map<String, Object> mergeSubTaskResult(int taskid, Map<String, Object> resultMap) {
        List<Map> results = subTaskManager.getSyncedSubTaskResults(taskid);
        if (CollectionUtils.isNotEmpty(results)) {
            LOGGER.info("try to merage subTaskResult" + results);
            List<Map> errorSubTasks = new ArrayList<Map>();
            Iterator<Map> iter = results.iterator();
            while (iter.hasNext()) {
                Map map = iter.next();
                if (map == null || map.containsKey("exception")) {
                    iter.remove();
                    errorSubTasks.add(map);
                }
            }
            if (CollectionUtils.isNotEmpty(results)) {
                resultMap.put("subTasks", results);
            }
            if (CollectionUtils.isNotEmpty(errorSubTasks)) {
                resultMap.put("errorSubTasks", errorSubTasks);
            }
        }
        return resultMap;
    }

    private void extractCodeCount(ExtractCount extractCount, ExtractMessage message,
                                  Map<String, Object> submitkeyResult) {
        extractCount.extractedCount++;
        ExtractCode result = message.getExtractCode();
        if (result == null) {
            extractCount.extractFailedCount++;
        } else if (result.equals(ExtractCode.EXTRACT_SUCCESS)) {
            extractCount.extractSucceedCount++;
        } else if (result.equals(ExtractCode.ERROR_INPUT) || result.equals(ExtractCode.EXTRACT_CONF_FAIL)) {
            extractCount.notExtractCount++;
        } else if (result.equals(ExtractCode.EXTRACT_STORE_FAIL)) {
            extractCount.storeFailedCount++;
        } else {
            extractCount.extractFailedCount++;
        }
        submitkeyResult.putAll(message.getSubmitkeyResult());
        if (message.getSubExtractMessageList() != null) {
            for (ExtractMessage subExtractMessage : message.getSubExtractMessageList()) {
                this.extractCodeCount(extractCount, subExtractMessage, submitkeyResult);
            }
        }
    }

    class ExtractCount {

        int extractedCount      = 0;

        int extractSucceedCount = 0;

        int extractFailedCount  = 0;

        int storeFailedCount    = 0;

        int notExtractCount     = 0;
    }

    /**
     * @return the crawlExcutorHandler
     */
    public CrawlExcutorHandler getCrawlExcutorHandler() {
        return crawlExcutorHandler;
    }

    /**
     * @param crawlExcutorHandler the crawlExcutorHandler to set
     */
    public CollectorWorker setCrawlExcutorHandler(CrawlExcutorHandler crawlExcutorHandler) {
        this.crawlExcutorHandler = crawlExcutorHandler;
        return this;
    }

    /**
     * @return the resultDataHandler
     */
    public ResultDataHandler getResultDataHandler() {
        return resultDataHandler;
    }

    /**
     * @param resultDataHandler the resultDataHandler to set
     */
    public CollectorWorker setResultDataHandler(ResultDataHandler resultDataHandler) {
        this.resultDataHandler = resultDataHandler;
        return this;
    }

    /**
     * @return the extractorActorRef
     */
    public WrappedActorRef getExtractorActorRef() {
        return extractorActorRef;
    }

    /**
     * @param extractorActorRef the extractorActorRef to set
     */
    public CollectorWorker setExtractorActorRef(WrappedActorRef extractorActorRef) {
        this.extractorActorRef = extractorActorRef;
        return this;
    }

    /**
     * @return the duplicateChecker
     */
    public DuplicateChecker getDuplicateChecker() {
        return duplicateChecker;
    }

    /**
     * @param duplicateChecker the duplicateChecker to set
     */
    public CollectorWorker setDuplicateChecker(DuplicateChecker duplicateChecker) {
        this.duplicateChecker = duplicateChecker;
        return this;
    }

    public Set<String> getResultTagSet() {
        return resultTagSet;
    }

    /**
     * @return the subTaskManager
     */
    public SubTaskManager getSubTaskManager() {
        return subTaskManager;
    }

    /**
     * @param subTaskManager the subTaskManager to set
     */
    public CollectorWorker setSubTaskManager(SubTaskManager subTaskManager) {
        this.subTaskManager = subTaskManager;
        return this;
    }

    /**
     * @return the initLinkNodeList
     */
    public List<LinkNode> getInitLinkNodeList() {
        return initLinkNodeList;
    }

    /**
     * @param initLinkNodeList the initLinkNodeList to set
     */
    public CollectorWorker setInitLinkNodeList(List<LinkNode> initLinkNodeList) {
        this.initLinkNodeList = initLinkNodeList;
        return this;
    }

    /**
     * @return the redisDao
     */
    public RedisDao getRedisDao() {
        return redisDao;
    }

    /**
     * @param redisDao the redisDao to set
     */
    public CollectorWorker setRedisDao(RedisDao redisDao) {
        this.redisDao = redisDao;
        return this;
    }

    public CollectorWorker setMessageService(MessageService messageService) {
        this.messageService = messageService;
        return this;
    }
}
