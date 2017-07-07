package com.datatrees.rawdatacentral.collector.search;

import com.datatrees.common.util.ThreadInterruptedUtil;
import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.domain.config.search.SearchType;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.format.unit.TimeUnit;
import com.datatrees.crawler.core.processor.search.SearchTemplateCombine;
import com.datatrees.rawdatacentral.collector.common.CollectorConstants;
import com.datatrees.rawdatacentral.core.common.UnifiedSysTime;
import com.datatrees.rawdatacentral.domain.model.Keyword;
import com.datatrees.rawdatacentral.service.KeywordService;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.model.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 下午2:21:06
 */
@Service
public class CrawlExcutorHandler {

    private static final Logger log = LoggerFactory.getLogger(CrawlExcutorHandler.class);

    @Resource
    private KeywordService      keywordService;

    /**
     * 
     * @param searchProcessor
     * @throws ResultEmptyException
     */
    public void crawlExecutor(SearchProcessor searchProcessor) throws ResultEmptyException {
        LinkQueue linkQueue = null;
        LinkNode linkNode = null;
        try {
            log.info("Rowdate collector start ... searchTemplate : " + searchProcessor.getSearchTemplate());
            Task task = searchProcessor.getTask();
            SearchTemplateConfig searchTemplateConfig = searchProcessor.getSearchTemplateConfig();
            linkQueue = new LinkQueue(searchProcessor.getSearchTemplateConfig());
            if (!linkQueue.init(searchProcessor.getInitLinkNodeList())) {
                task.setErrorCode(ErrorCode.INIT_QUEUE_FAILED_ERROR_CODE);
                log.info(searchTemplateConfig.getType() + "--" + "The queue is empty, the system will exit ."
                         + "Template: " + searchProcessor.getSearchTemplate());
                return;
            }

            if (SearchType.KEYWORD_SEARCH.equals(searchTemplateConfig.getType())) {
                List<Keyword> keywordList = keywordService.queryByWebsiteType(
                    Integer.valueOf(searchProcessor.getProcessorContext().getWebsite().getWebsiteType()));
                for (Keyword keyword : keywordList) {
                    searchProcessor.init(keyword.getKeyword());
                    ProcessorContextUtil.setKeyword(searchProcessor.getProcessorContext(), keyword.getKeyword());
                    String url = SearchTemplateCombine.constructSearchURL(searchProcessor.getSearchTemplate(),
                        keyword.getKeyword(), searchProcessor.getEncoding(), 0, true,
                        searchProcessor.getProcessorContext().getContext());
                    linkNode = new LinkNode(url).setDepth(0);
                    this.doLoopCrawl(searchProcessor, linkQueue, linkNode, searchTemplateConfig.getThreadCount());
                }
            } else {
                searchProcessor.init();
                String url = SearchTemplateCombine.constructSearchURL(searchProcessor.getSearchTemplate(), "",
                    searchProcessor.getEncoding(), 0, true, searchProcessor.getProcessorContext().getContext());
                linkNode = new LinkNode(url).setDepth(0);
                this.doLoopCrawl(searchProcessor, linkQueue, linkNode, searchTemplateConfig.getThreadCount());
            }

        } catch (Exception e) {
            this.exceptionHandle(e, "crawlExecutor encountered a problem .");
        } finally {
            if (searchProcessor.getCrawlExecutorPool() != null) {
                searchProcessor.getCrawlExecutorPool().shutdownNow();
                log.info("shutdownNow crawlExecutorPool success.");
            }
            if (null != linkNode && null != linkQueue) {
                linkQueue.closeLinkQueue();
            }
        }
    }

    private boolean isTimeOutOfTask(SearchProcessor searchProcessor) {
        boolean isTimeOut = false;
        try {
            Task task = searchProcessor.getTask();
            long taskStartTime = searchProcessor.getTask().getStartedAt().getTime();
            long currentTime = UnifiedSysTime.INSTANCE.getSystemTime().getTime();
            // unit minutes
            long timeOut = TimeUnit.MINUTE.toMillis(searchProcessor.getMaxExecuteMinutes());
            if ((currentTime - taskStartTime) >= timeOut) {
                task.setErrorCode(ErrorCode.TASK_TIMEOUT_ERROR_CODE);
                isTimeOut = true;
            }
            if (log.isDebugEnabled()) {
                log.debug("Task Timeout ,taskId : " + searchProcessor.getTask().getId() + " ,taskStartTime : "
                          + taskStartTime + " ,currentTime : " + currentTime + "  ,isTimeOut : " + isTimeOut);
            }
        } catch (Exception e) {
            log.error("isTimeOut encounter a problem ,error : ", e);
        }

        return isTimeOut;
    }

    private void doLoopCrawl(SearchProcessor searchProcessor, LinkQueue linkQueue, LinkNode linkNode,
                             Integer threadCount) throws ResultEmptyException {
        Task task = searchProcessor.getTask();
        log.info("Start doLoopCrawl , Task id:" + task.getId() + ",linkNode:" + linkNode);
        if (linkNode != null && StringUtils.isNotBlank(linkNode.getUrl())) {
            linkQueue.addLink(linkNode);
        }
        ExecutorService crawlExecutorPool = null;
        List<Future<Boolean>> futureList = null;
        try {
            outer: while (!Thread.currentThread().isInterrupted()
                          && !ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
                LinkedList<LinkNode> nextLink = linkQueue.fetchNewLinks(CollectorConstants.MAX_QUEUE_SIZE);
                if (CollectionUtils.isEmpty(nextLink)) {
                    if (CollectionUtils.isEmpty(futureList)) {
                        log.info("Link queue is empty,program will exit ");
                        break;
                    } else {
                        Iterator<Future<Boolean>> it = futureList.iterator();
                        while (it.hasNext()) {
                            Future<Boolean> future = it.next();
                            future.get();// sync to get result may throw exception
                            it.remove();
                        }
                    }
                }
                while (CollectionUtils.isNotEmpty(nextLink)
                       && !ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
                    try {
                        LinkNode link = nextLink.removeFirst();
                        // Time Out Logic
                        if (isTimeOutOfTask(searchProcessor)) {
                            log.warn("TaskWorker has been Time Out.The program will exit");
                            break outer;
                        }

                        if (linkQueue.isFull()) {
                            searchProcessor.getTask().setErrorCode(ErrorCode.QUEUE_FULL_ERROR_CODE);
                        }
                        if (threadCount != null && threadCount > 1) {
                            if (crawlExecutorPool != null) {
                                // run as muti thread pool
                                try {
                                    futureList.add(crawlExecutorPool.submit(new Callable<Boolean>() {
                                        @Override
                                        public Boolean call() throws Exception {
                                            List<LinkNode> findLinks = searchProcessor.crawlOneURL(link);
                                            if (!Thread.currentThread().isInterrupted()) {
                                                linkQueue.addLinks(findLinks);
                                            }
                                            return true;
                                        }
                                    }));
                                } catch (Exception e) {
                                    if (e instanceof RejectedExecutionException) {
                                        // ignore RejectedExecutionException
                                        nextLink.addFirst(link);
                                        Thread.sleep(300);
                                    } else {
                                        log.error("submit linked cause unknow exception " + e.getMessage(), e);
                                    }
                                }
                            } else {
                                /*
                                 * 1. more Efficientive for muti thread mode, using web thread to
                                 * crawl the first linknode 2. can collect urlCount in mian thread
                                 */
                                List<LinkNode> findLinks = searchProcessor.crawlOneURL(link);
                                linkQueue.addLinks(findLinks);
                                log.info("get crawlExecutorPool with threadCount:" + threadCount);
                                crawlExecutorPool = searchProcessor.getCrawlExecutorPool(threadCount);
                                futureList = new ArrayList<Future<Boolean>>();
                            }
                        } else {
                            // Url Request
                            List<LinkNode> findLinks = searchProcessor.crawlOneURL(link);
                            linkQueue.addLinks(findLinks);
                        }

                        // may block 505
                        if (searchProcessor.isNeedEarlyQuit()) {
                            log.info("TaskWorker quit early.The program will exit");
                            break outer;
                        }

                    } catch (Exception e) {
                        this.exceptionHandle(e, "TaskWorker crawlOneURL  encountered a problem .");
                    }
                }
            }
        } catch (Exception e) {
            this.exceptionHandle(e, "doLoopCrawl error ");
        }
    }

    private void exceptionHandle(Exception e, String remark) throws ResultEmptyException {
        if (e instanceof ResultEmptyException) {
            throw (ResultEmptyException) e;
        } else if (e.getCause() instanceof ResultEmptyException) {
            throw new ResultEmptyException(e);
        } else {
            log.error(remark + e.getMessage(), e);
        }
    }
}
