/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.worker;

import java.util.*;
import java.util.concurrent.TimeUnit;

import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.util.Timeout;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.search.Request;
import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.exception.ResponseCheckException;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.login.Login;
import com.datatrees.rawdatacentral.collector.actor.TaskMessage;
import com.datatrees.rawdatacentral.collector.common.CollectorConstants;
import com.datatrees.rawdatacentral.collector.search.CrawlExecutor;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.collector.worker.filter.BusinessTypeFilter;
import com.datatrees.rawdatacentral.collector.worker.filter.TemplateFilter;
import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.core.common.UnifiedSysTime;
import com.datatrees.rawdatacentral.core.dao.RedisDao;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.core.subtask.SubTaskManager;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.ExtractCode;
import com.datatrees.rawdatacentral.domain.exception.LoginTimeOutException;
import com.datatrees.rawdatacentral.domain.model.Task;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.submitter.common.RedisKeyUtils;
import com.google.gson.reflect.TypeToken;
import com.treefinance.crawler.framework.exception.ConfigException;
import com.treefinance.crawler.framework.expression.StandardExpression;
import com.treefinance.crawler.framework.extension.spider.Spiders;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午9:16:04
 */
public class CollectorWorker {

    private static final Logger  LOGGER                    = LoggerFactory.getLogger(CollectorWorker.class);
    private final        long    maxLiveTime               = TimeUnit.SECONDS.toMillis(PropertiesConfiguration.getInstance().getInt("max.live.seconds", 30));
    // 爬虫任务超时时间，单位：秒
    private static final int     DEFAULT_TASK_TIMEOUT      = PropertiesConfiguration.getInstance().getInt("crawler.task.timeout", 7 * 60);
    private              Integer interactiveTimeoutSeconds = PropertiesConfiguration.getInstance().getInt("interactive.timeout.seconds", 300);
    private CrawlExecutor     crawlExecutor;
    private ResultDataHandler resultDataHandler;
    private SubTaskManager    subTaskManager;
    private Set<String> resultTagSet = new HashSet<>();
    private RedisDao           redisDao;
    private BusinessTypeFilter businessTypeFilter;

    /**
     * 登录
     * @param taskMessage
     * @return
     */
    public HttpResult<Boolean> doLogin(TaskMessage taskMessage) {
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
                    LOGGER.warn("Message drop! Born at  bornTime = {} ,maxLiveTime ={},taskId={},websiteName={}", DateUtils.formatYmdhms(taskMessage.getCollectorMessage().getBornTimestamp()), maxLiveTime, task.getTaskId(), task.getWebsiteName());
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
            if (e instanceof LoginTimeOutException) {
                return loginResult.failure(ErrorCode.LOGIN_TIMEOUT_ERROR.getErrorCode(), e.getMessage());
            }
            return loginResult.failure(ErrorCode.COOKIE_INVALID);
        }
    }

    /**
     * 爬取数据
     * @param taskMessage
     * @return
     */
    public HttpResult<Map<String, Object>> doSearch(TaskMessage taskMessage) {
        HttpResult<Map<String, Object>> searchResult = new HttpResult<>();
        Task task = taskMessage.getTask();
        SearchProcessorContext context = taskMessage.getContext();
        try {
            List<Future<Object>> futureList = this.startCrawler(taskMessage, task, context);

            Map<String, Object> resultMap = this.awaitDone(futureList, taskMessage);

            Map<String, Object> extractResult = mergeSubTaskResult(task.getId(), resultMap);

            LOGGER.info("doSearch success taskId={}, websiteName={}", task.getTaskId(), task.getWebsiteName());

            return searchResult.success(extractResult);
        } catch (ConfigException e) {
            LOGGER.error("Search config is incorrect! taskId={}, websiteName= {}", task.getTaskId(), task.getWebsiteName(), e);
            return searchResult.failure(ErrorCode.CONFIG_ERROR);
        } catch (ResponseCheckException e) {
            LOGGER.error("Response checking is not pass! taskId={}, websiteName={}", task.getTaskId(), task.getWebsiteName(), e);
            return searchResult.failure(ErrorCode.RESPONSE_EMPTY_ERROR_CODE.getErrorCode(), e.getMessage());
        } catch (ResultEmptyException e) {
            LOGGER.error("The necessary fields is empty! taskId={}, websiteName={}", task.getTaskId(), task.getWebsiteName(), e);
            return searchResult.failure(ErrorCode.NOT_EMPTY_ERROR_CODE.getErrorCode(), e.getMessage());
        } catch (Throwable e) {
            LOGGER.error("Something is wrong when searching! taskId={}, websiteName={}", task.getTaskId(), task.getWebsiteName(), e);
            return searchResult.failure();
        } finally {
            task.setFinishedAt(UnifiedSysTime.INSTANCE.getSystemTime());
            task.setDuration((task.getFinishedAt().getTime() - task.getStartedAt().getTime()) / 1000);
            //释放代理
            if (searchResult.getResponseCode() != ErrorCode.TASK_INTERRUPTED_ERROR.getErrorCode()) {
                context.release();
            }
        }
    }

    private Map<String, Object> awaitDone(List<Future<Object>> futureList, TaskMessage taskMessage) {
        Map<String, Object> extractResult = new HashMap<>();

        ExtractCount extractCount = new ExtractCount();

        for (Future future : futureList) {
            try {
                ExtractMessage message = (ExtractMessage) Await.result(future, new Timeout(CollectorConstants.EXTRACT_ACTOR_TIMEOUT).duration());
                this.extractCodeCount(extractCount, message, extractResult);
            } catch (Exception e) {
                extractCount.extractFailedCount++;
                LOGGER.error("Error awaiting extract result : {}", e.getMessage(), e);
            }
        }

        Task task = taskMessage.getTask();
        task.setExtractedCount(extractCount.extractedCount);
        task.setExtractFailedCount(extractCount.extractFailedCount);
        task.setNotExtractCount(extractCount.notExtractCount);
        task.setExtractSucceedCount(extractCount.extractSucceedCount);
        task.setStoreFailedCount(extractCount.storeFailedCount);

        return extractResult;
    }

    private void extractCodeCount(ExtractCount extractCount, ExtractMessage message, Map<String, Object> extractResult) {
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

        extractResult.putAll(message.getSubmitkeyResult());

        if (message.getSubExtractMessageList() != null) {
            for (ExtractMessage subExtractMessage : message.getSubExtractMessageList()) {
                this.extractCodeCount(extractCount, subExtractMessage, extractResult);
            }
        }
    }

    private List<Future<Object>> startCrawler(TaskMessage taskMessage, Task task, SearchProcessorContext context) throws ConfigException, ResultEmptyException {
        Collection<SearchTemplateConfig> templateList = context.getSearchTemplates();
        if (CollectionUtils.isEmpty(templateList)) {
            throw new ConfigException("Search template in config is empty!");
        }

        List<Future<Object>> futureList = new ArrayList<>();

        String templateId = taskMessage.getTemplateId();

        LOGGER.debug("Expected search template: {}", templateId);

        for (SearchTemplateConfig templateConfig : templateList) {
            LOGGER.info("Start search template: {}", templateConfig.getId());

            if (TemplateFilter.isFilter(templateConfig, templateId) || businessTypeFilter.isFilter(templateConfig.getBusinessType(), context)) {
                LOGGER.info("Skip search template: {}, taskId: {}, websiteName: {}, businessType: {}", templateConfig.getId(), context.getTaskId(), context.getWebsiteName(), templateConfig.getBusinessType());
                continue;
            }

            // get all the possible result tag
            resultTagSet.addAll(templateConfig.getResultTagList());

            Collection<Future<Object>> futures;
            if (templateConfig.getPlugin() == null) {
                futures = crawlByNormal(templateConfig, context, taskMessage, task);
            } else {
                futures = crawlByExtension(templateConfig, context, taskMessage);
            }

            if (futures != null) {
                futureList.addAll(futures);
            }
        }

        return futureList;
    }

    private Collection<Future<Object>> crawlByExtension(SearchTemplateConfig templateConfig, SearchProcessorContext context, TaskMessage taskMessage) {
        try {
            AbstractPlugin plugin = templateConfig.getPlugin();

            CustomPageProcessor pageProcessor = new CustomPageProcessor(resultDataHandler, taskMessage);

            Spiders.run(plugin, context, pageProcessor);

            return pageProcessor.getFutures();
        } catch (Exception e) {
            LOGGER.error("Error crawling by extensional spider!", e);
        }

        return null;
    }

    /**
     * @exception ResultEmptyException
     * @exception ResponseCheckException
     */
    private Collection<Future<Object>> crawlByNormal(SearchTemplateConfig templateConfig, SearchProcessorContext context, TaskMessage taskMessage, Task task) throws ResultEmptyException {
        try {
            Request request = templateConfig.getRequest();
            if (null == request) {
                LOGGER.warn("Request in search-template[{}] is empty! taskId: {}, websiteName: {}", templateConfig.getId(), task.getTaskId(), task.getWebsiteName());
                return null;
            }

            String searchTemplate = getSearchSeedUrl(request, context, templateConfig.getId());

            if (StringUtils.isEmpty(searchTemplate)) {
                LOGGER.warn("Not found seed url in search-template[{}]! taskId: {}, websiteName: {}", templateConfig.getId(), task.getTaskId(), task.getWebsiteName());
                return null;
            }

            addDefaultHeaders(context, request);

            SearchProcessor searchProcessor = new SearchProcessor(taskMessage).setSearchTemplate(searchTemplate).setSearchTemplateConfig(templateConfig).setResultDataHandler(resultDataHandler);
            searchProcessor.setDefaultTimeout(DEFAULT_TASK_TIMEOUT, TimeUnit.SECONDS);
            if (request.getMaxExecuteMinutes() != null) {
                searchProcessor.setTimeout(request.getMaxExecuteMinutes(), TimeUnit.MINUTES);
            }

            crawlExecutor.crawlExecutor(searchProcessor);

            return searchProcessor.getFutureList();
        } catch (ResultEmptyException e) {
            throw e;
        } catch (Exception e) {
            // 除了中断,内容为空,字段为空等错误,任务不算失败
            LOGGER.error("doSearch error taskId={}, websiteName={}", task.getTaskId(), task.getWebsiteName(), e);
        }

        return null;
    }

    private void addDefaultHeaders(SearchProcessorContext context, Request request) {
        String headerString = request.getDefaultHeader();
        if (StringUtils.isNotBlank(headerString)) {
            headerString = StandardExpression.eval(headerString, context.getContext());
            Map<String, String> defaultHeader = GsonUtils.fromJson(headerString, new TypeToken<Map<String, String>>() {}.getType());
            if (MapUtils.isNotEmpty(defaultHeader)) {
                context.getDefaultHeader().putAll(defaultHeader);
            }
        }
    }

    private String getSearchSeedUrl(Request request, SearchProcessorContext context, String templateId) {
        String seedUrl = null;

        // get from context ,seed url may given by sub task or other external task
        Object templateUrl = ProcessorContextUtil.getValue(context, templateId);
        if (templateUrl != null) {
            seedUrl = templateUrl.toString();
        } else if (CollectionUtils.isNotEmpty(request.getSearchTemplateList())) {
            seedUrl = request.getSearchTemplateList().get(0);
        }

        return StandardExpression.eval(seedUrl, context.getContext(), false);
    }

    public Map<String, Object> mergeSubTaskResult(int taskid, Map<String, Object> resultMap) {
        List<Map> results = subTaskManager.getSyncedSubTaskResults(taskid);
        if (CollectionUtils.isNotEmpty(results)) {
            LOGGER.info("try to merge subTaskResult: {}", results);
            List<Map> errorSubTasks = new ArrayList<>();
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

    /**
     * @param crawlExecutor the crawl executor to set
     */
    public CollectorWorker setCrawlExecutor(CrawlExecutor crawlExecutor) {
        this.crawlExecutor = crawlExecutor;
        return this;
    }

    /**
     * @param resultDataHandler the resultDataHandler to set
     */
    public CollectorWorker setResultDataHandler(ResultDataHandler resultDataHandler) {
        this.resultDataHandler = resultDataHandler;
        return this;
    }

    public Set<String> getResultTagSet() {
        return resultTagSet;
    }

    /**
     * @param subTaskManager the subTaskManager to set
     */
    public CollectorWorker setSubTaskManager(SubTaskManager subTaskManager) {
        this.subTaskManager = subTaskManager;
        return this;
    }

    /**
     * @param redisDao the redisDao to set
     */
    public CollectorWorker setRedisDao(RedisDao redisDao) {
        this.redisDao = redisDao;
        return this;
    }

    /**
     * businessTypeFilter to set
     * @param businessTypeFilter
     * @return
     */
    public CollectorWorker setBusinessTypeFilter(BusinessTypeFilter businessTypeFilter) {
        this.businessTypeFilter = businessTypeFilter;
        return this;
    }

    class ExtractCount {

        int extractedCount      = 0;
        int extractSucceedCount = 0;
        int extractFailedCount  = 0;
        int storeFailedCount    = 0;
        int notExtractCount     = 0;
    }

}
