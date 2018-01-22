package com.datatrees.rawdatacentral.collector.search;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;

import akka.dispatch.Future;
import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.domain.config.properties.Properties;
import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.search.Crawler;
import com.datatrees.rawdatacentral.collector.actor.TaskMessage;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.FilterConstant;
import com.datatrees.rawdatacentral.collector.chain.FilterExecutor;
import com.datatrees.rawdatacentral.collector.chain.FilterListFactory;
import com.datatrees.rawdatacentral.collector.common.CollectorConstants;
import com.datatrees.rawdatacentral.collector.worker.ResultDataHandler;
import com.datatrees.rawdatacentral.domain.model.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午12:45:25
 */
public class SearchProcessor {

    private static final Logger log = LoggerFactory.getLogger(SearchProcessor.class);
    private String               searchTemplate;
    private SearchTemplateConfig searchTemplateConfig;
    private String               templateId;
    private String               encoding;
    private long                 waitIntervalMillis;
    private boolean              duplicateRemoval;
    private long                 maxExecuteMinutes;
    private ResultDataHandler    resultDataHandler;
    private final List<Future<Object>> futureList = new ArrayList<>();
    private Task                   task;
    private SearchProcessorContext processorContext;
    private String                 keyword;
    private boolean                isLastLink;
    private boolean                needEarlyQuit;
    private TaskMessage            taskMessage;
    private ThreadPoolExecutor crawlExecutorPool = null;

    public SearchProcessor(TaskMessage taskMessage) {
        try {
            this.taskMessage = taskMessage;
            this.task = taskMessage.getTask();
            this.processorContext = taskMessage.getContext();
            if (processorContext != null && null != processorContext.getSearchConfig()) {
                SearchConfig config = processorContext.getSearchConfig();
                if (null != config) {
                    Properties properties = config.getProperties();
                    if (null != properties) {
                        setEncoding(StringUtils.isBlank(properties.getEncoding()) ? CollectorConstants.DEFUALT_ENCODING : properties.getEncoding());
                        setWaitIntervalMillis(properties.getWaitIntervalMillis() == null ? 0 : properties.getWaitIntervalMillis());
                        setDuplicateRemoval(properties.getDuplicateRemoval());
                        log.info("DuplicateRemoval Config is  " + properties.getDuplicateRemoval() + " ,workingTaskEntity_id " + task.getId());
                    }
                }
            }

        } catch (Exception e) {
            log.error("SearchProcessor init Resource  error.", e);
        }
    }

    /**
     *
     */
    public void init(String keyword) {
        this.keyword = keyword;
        this.init();
    }

    public void init() {
        needEarlyQuit = false;
        isLastLink = false;
    }

    /**
     * @return the task
     */
    public Task getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(Task task) {
        this.task = task;
    }

    /**
     * @return the processorContext
     */
    public SearchProcessorContext getProcessorContext() {
        return processorContext;
    }

    /**
     * @param processorContext the processorContext to set
     */
    public void setProcessorContext(SearchProcessorContext processorContext) {
        this.processorContext = processorContext;
    }

    private URLHandlerImpl initURLHandlerImpl() {
        URLHandlerImpl handler = new URLHandlerImpl();
        handler.setSearchProcessor(this);
        return handler;
    }

    /**
     * @param url
     * @return
     * @exception InvocationTargetException
     * @exception IllegalAccessException
     * @exception ResultEmptyException
     */
    public List<LinkNode> crawlOneURL(LinkNode url) throws IllegalAccessException, InvocationTargetException, ResultEmptyException {
        List<LinkNode> linkNodeList = null;
        CrawlRequest request = null;
        try {
            URLHandlerImpl handler = initURLHandlerImpl();
            request = CrawlRequest.build().setProcessorContext(processorContext).setUrl(url).setSearchTemplateId(templateId).setSearchTemplate(searchTemplate).setUrlHandler(handler).contextInit();

            RequestUtil.setKeyWord(request, keyword);
            CrawlResponse response = Crawler.crawl(request);
            List<Object> objs = ResponseUtil.getResponseObjectList(response);

            if (CollectionUtils.isNotEmpty(objs)) {
                synchronized (futureList) {
                    futureList.addAll(resultDataHandler.resultListHandler(objs, taskMessage));
                }
            }

            linkNodeList = response.getUrls();
            linkNodeList.addAll(handler.getTempLinkNodes());

            Context context = new Context();
            context.setAttribute(FilterConstant.CURRENT_LINK_NODE, url);
            context.setAttribute(FilterConstant.SEARCH_PROCESSOR, this);
            context.setAttribute(FilterConstant.CURRENT_REQUEST, request);
            context.setAttribute(FilterConstant.CURRENT_RESPONSE, response);
            context.setAttribute(FilterConstant.FETCHED_LINK_NODE_LIST, linkNodeList);

            FilterExecutor.INSTANCE.execut(context, FilterListFactory.SEARCH.getFilterList());
        } catch (Exception e) {
            if (e instanceof ResultEmptyException) {
                throw (ResultEmptyException) e;
            } else {
                log.error("Caught Exception in crawlOneURL ,url [" + url.getUrl() + "]", e);
            }
        } finally {
            if (null != request) {
                // reset page content
                RequestUtil.setContent(request, null);
            }
            // clear Embedded context
            ProcessorContextUtil.clearThreadLocalLinkNode(processorContext);
            ProcessorContextUtil.clearThreadLocalResponseList(processorContext);
        }
        return linkNodeList;
    }

    public long getMaxExecuteMinutes() {
        return maxExecuteMinutes;
    }

    public SearchProcessor setMaxExecuteMinutes(long maxExecuteMinutes) {
        this.maxExecuteMinutes = maxExecuteMinutes;
        return this;
    }

    public boolean isDuplicateRemoval() {
        return duplicateRemoval;
    }

    public void setDuplicateRemoval(boolean duplicateRemoval) {
        this.duplicateRemoval = duplicateRemoval;
    }

    public boolean isNeedEarlyQuit() {
        return needEarlyQuit;
    }

    public void setNeedEarlyQuit(boolean needEarlyQuit) {
        this.needEarlyQuit = needEarlyQuit;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * @return the searchTemplateF
     */
    public String getSearchTemplate() {
        return searchTemplate;
    }

    /**
     * @param searchTemplate the searchTemplate to set
     */
    public SearchProcessor setSearchTemplate(String searchTemplate) {
        this.searchTemplate = searchTemplate;
        return this;
    }

    /**
     * @param templateId the templateId to set
     */
    public SearchProcessor setTemplateId(String templateId) {
        this.templateId = templateId;
        return this;
    }

    public long getWaitIntervalMillis() {
        if (searchTemplateConfig == null || searchTemplateConfig.getWaitIntervalMillis() == null) {
            return waitIntervalMillis;
        } else {
            return searchTemplateConfig.getWaitIntervalMillis();
        }
    }

    public void setWaitIntervalMillis(long waitIntervalMillis) {
        this.waitIntervalMillis = waitIntervalMillis;
    }

    public SearchTemplateConfig getSearchTemplateConfig() {
        return searchTemplateConfig;
    }

    public SearchProcessor setSearchTemplateConfig(SearchTemplateConfig searchTemplateConfig) {
        this.searchTemplateConfig = searchTemplateConfig;
        return this;
    }

    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }


    /**
     * @param resultDataHandler the resultDataHandler to set
     */
    public SearchProcessor setResultDataHandler(ResultDataHandler resultDataHandler) {
        this.resultDataHandler = resultDataHandler;
        return this;
    }

    /**
     * @return the futureList
     */
    public List<Future<Object>> getFutureList() {
        return futureList;
    }

    /**
     * @return the isLastLink
     */
    public boolean isLastLink() {
        return isLastLink;
    }

    /**
     * @param isLastLink the isLastLink to set
     */
    public void setLastLink(boolean isLastLink) {
        this.isLastLink = isLastLink;
    }

    /**
     * @return the crawlExecutorPool
     */
    public ExecutorService getCrawlExecutorPool(Integer threadCount) {
        if (crawlExecutorPool != null) {
            crawlExecutorPool.setCorePoolSize(threadCount);
            crawlExecutorPool.setMaximumPoolSize(threadCount);
        } else {
            crawlExecutorPool = initCrawlExecutorPool(threadCount);
        }
        return crawlExecutorPool;
    }

    private ThreadPoolExecutor initCrawlExecutorPool(int threadCount) {
        return new ThreadPoolExecutor(threadCount, threadCount, 20L, java.util.concurrent.TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactoryImpl(Thread.currentThread().getName() + "_"));
    }

    public ExecutorService getCrawlExecutorPool() {
        return crawlExecutorPool;
    }
}
