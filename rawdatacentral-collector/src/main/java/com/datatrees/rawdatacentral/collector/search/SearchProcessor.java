package com.datatrees.rawdatacentral.collector.search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import com.datatrees.rawdatacentral.collector.chain.Filters;
import com.datatrees.rawdatacentral.collector.common.CollectorConstants;
import com.datatrees.rawdatacentral.collector.worker.ResultDataHandler;
import com.datatrees.rawdatacentral.domain.model.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    private String               encoding;
    private long                 waitIntervalMillis;
    private boolean              duplicateRemoval;
    private ResultDataHandler    resultDataHandler;
    private String               keyword;
    private boolean              isLastLink;
    private boolean              needEarlyQuit;
    private       ThreadPoolExecutor   crawlExecutorPool = null;
    private final List<Future<Object>> futureList        = new ArrayList<>();
    private final TaskMessage        taskMessage;
    // 任务默认超时时间，单位：毫秒
    private long defaultTimeout = -1;
    // 任务超时时间，单位：毫秒
    private long timeout        = -1;
    // 任务可执行的结束时间戳，单位：毫秒
    private long deadLine       = -1;

    public SearchProcessor(TaskMessage taskMessage) {
        this.taskMessage = taskMessage;

        SearchProcessorContext processorContext = getProcessorContext();
        if (processorContext != null) {
            SearchConfig config = processorContext.getSearchConfig();
            if (null != config) {
                Properties properties = config.getProperties();
                if (null != properties) {
                    String encoding = StringUtils.defaultIfBlank(properties.getEncoding(), CollectorConstants.DEFUALT_ENCODING);
                    setEncoding(encoding);
                    setWaitIntervalMillis(properties.getWaitIntervalMillis() == null ? 0 : properties.getWaitIntervalMillis());
                    setDuplicateRemoval(properties.getDuplicateRemoval());

                    Task task = taskMessage.getTask();
                    log.info("DuplicateRemoval Config is {}, workingTaskEntity_id: {}", properties.getDuplicateRemoval(), task.getId());
                }
            }
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

    private URLHandlerImpl initURLHandlerImpl() {
        URLHandlerImpl handler = new URLHandlerImpl();
        handler.setSearchProcessor(this);
        return handler;
    }

    public List<LinkNode> crawlOneURL(LinkNode url) throws ResultEmptyException {
        List<LinkNode> linkNodeList = null;
        CrawlRequest request = null;
        try {
            URLHandlerImpl handler = initURLHandlerImpl();
            request = CrawlRequest.build().setProcessorContext(getProcessorContext()).setUrl(url).setSearchTemplateId(searchTemplateConfig.getId()).setSearchTemplate(searchTemplate).setUrlHandler(handler).contextInit();

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
            context.setCurrentLinkNode(url);
            context.setSearchProcessor(this);
            context.setCrawlRequest(request);
            context.setCrawlResponse(response);
            context.setFetchedLinkNodeList(linkNodeList);

            Filters.SEARCH.doFilter(context);
        } catch (ResultEmptyException e) {
            throw e;
        } catch (Exception e) {
            log.error("Caught Exception in crawlOneURL ,url [{}]", url.getUrl(), e);
        } finally {
            if (null != request) {
                // reset page content
                RequestUtil.setContent(request, null);
            }
            // clear Embedded context
            ProcessorContextUtil.clearThreadLocalLinkNode(getProcessorContext());
            ProcessorContextUtil.clearThreadLocalResponseList(getProcessorContext());
        }
        return linkNodeList;
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

    /**
     * 设置默认超时. if <code>unit</code> is null,parse <code>timeout</code> as millis.
     * @param timeout 超时时间
     * @param unit    时间单位
     */
    public void setDefaultTimeout(long timeout, TimeUnit unit) {
        if (unit != null) {
            this.defaultTimeout = unit.toMillis(timeout);
        } else {
            this.defaultTimeout = timeout;
        }
        if (this.deadLine == -1) {
            resetDeadLine(this.defaultTimeout);
        }
    }

    /**
     * 设置超时. if <code>unit</code> is null,parse <code>timeout</code> as millis.
     * @param timeout 超时时间
     * @param unit    时间单位
     */
    public void setTimeout(long timeout, TimeUnit unit) {
        if (unit != null && timeout > 0) {
            this.timeout = unit.toMillis(timeout);
        } else {
            this.timeout = timeout;
        }
        resetDeadLine(this.timeout);
    }

    private void resetDeadLine(long timeout) {
        if (timeout > 0) {
            this.deadLine = getStartTime() + timeout;
        } else {
            this.deadLine = 0;
        }
    }

    public boolean isTimeout(long timeInMillis) {
        if (deadLine <= 0) {
            return false;
        }

        long now = timeInMillis;
        if (now <= 0) {
            now = System.currentTimeMillis();
        }

        return now > deadLine;
    }

    public Task getTask() {
        return taskMessage.getTask();
    }

    public int getTaskId() {
        return getTask().getId();
    }

    public long getStartTime() {
        if (getTask().getStartedAt() == null) {
            throw new IllegalArgumentException("Task may be created incorrect! The start time was lost!");
        }
        return getTask().getStartedAt().getTime();
    }

    public SearchProcessorContext getProcessorContext() {
        return taskMessage.getContext();
    }
}
