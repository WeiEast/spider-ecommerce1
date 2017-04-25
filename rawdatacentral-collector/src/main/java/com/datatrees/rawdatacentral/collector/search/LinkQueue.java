package com.datatrees.rawdatacentral.collector.search;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.bdb.operator.BDBOperator;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentLockedException;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午12:36:17
 */
public class LinkQueue {
    private static final Logger log = LoggerFactory.getLogger(LinkQueue.class);

    private BDBOperator bdbOperator;
    private SearchTemplateConfig searchTemplateConfig;
    private int queueSize = 0;
    private boolean isFull = false;

    public LinkQueue(SearchTemplateConfig searchTemplateConfig) {
        this.searchTemplateConfig = searchTemplateConfig;
    }

    /**
     * @param homePath
     * @return
     */
    public boolean init() {
        return this.init(null);
    }

    public boolean init(List<LinkNode> initLinkNodeList) {
        try {
            bdbOperator = new BDBOperator();
            if (CollectionUtils.isNotEmpty(initLinkNodeList)) {
                this.addLinks(initLinkNodeList);
            }
            return true;
        } catch (EnvironmentLockedException e) {
            log.error("LinkQueue init failed because of EnvironmentLockedException ", e);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e1) {
                // ignore
            }
        } catch (DatabaseException e) {
            log.error("LinkQueue init failed because of DatabaseException", e);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e1) {
                // ignore
            }
        } catch (Exception e) {
            log.error("LinkQueue init failed ", e);
        }
        return false;
    }


    /**
     * add batch of links into BDB queue
     * 
     * @param links
     * @param worker
     * @return new link num
     */
    public int addLinks(List<LinkNode> links) {
        synchronized (bdbOperator) {
            int newLinkNum = 0;
            long preId = bdbOperator.getCurrentId();
            for (LinkNode linkNode : links) {
                if (linkNode == null) continue;
                try {
                    newLinkNum += addLink(linkNode);
                } catch (Exception ex) {
                    log.error("Catch exception when add link: [" + linkNode.getUrl() + "] to bdb");
                    log.error(ex.toString(), ex);
                }
            }

            log.info("Added [" + (bdbOperator.getCurrentId() - preId) + "] links to link queue , total [" + (bdbOperator.getCurrentId() - 1)
                    + "] in queue current position is " + bdbOperator.getLastFetchId());
            log.info("New link added into BDB, new link num:" + newLinkNum);
            return newLinkNum;
        }
    }

    // need optimization later
    public int addLink(LinkNode linkNode) {
        synchronized (bdbOperator) {
            int result = 0;
            if (!checkIfBoundary(linkNode)) {
                result = bdbOperator.addLink(linkNode);
                queueSize += result;
            }
            return result;
        }
    }

    /**
     * 
     * @param newLinkNum
     * @param linkNode
     * @param deep
     */
    private boolean checkIfBoundary(LinkNode linkNode) {
        int deep = linkNode.getDepth();
        int maxPage = searchTemplateConfig.getRequest().getMaxPages();
        int maxDepth = searchTemplateConfig.getMaxDepth();

        if (deep > maxDepth) {
            log.debug("drop link [" + linkNode.getUrl() + "] for max depth reached, link depth = [" + deep + "] max depth = [" + maxDepth + "]");

            return true;
        } else if (queueSize > maxPage) {
            isFull = true;
            log.info("max page is " + maxPage + " current queue size is " + queueSize);
            log.warn("drop link [" + linkNode.getUrl() + "] for max page reached. The link queue is full");

            return true;
        }

        return false;
    }

    // need optimization later
    public LinkedList<LinkNode> fetchNewLinks(int size) {
        synchronized (bdbOperator) {
            return bdbOperator.fetchNewLinks(size);
        }
    }

    public void closeLinkQueue() {
        synchronized (bdbOperator) {
            if (null != bdbOperator) {
                bdbOperator.closeDatabase();
            }
        }
    }

    public long getQueueSize() {
        return queueSize;
    }

    public boolean isFull() {
        return isFull;
    }
}
