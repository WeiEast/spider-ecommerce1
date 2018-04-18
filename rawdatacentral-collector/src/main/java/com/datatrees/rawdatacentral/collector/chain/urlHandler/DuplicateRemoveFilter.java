package com.datatrees.rawdatacentral.collector.chain.urlHandler;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.collector.worker.deduplicate.DuplicateChecker;
import com.datatrees.rawdatacentral.core.model.data.AbstractData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:deng_kui@vobile.cn">Deng Kui</A>
 * @version 1.0
 * @since 2014-6-16 上午11:15:39
 */
public class DuplicateRemoveFilter implements Filter {

    private static final Logger  logger                  = LoggerFactory.getLogger(DuplicateRemoveFilter.class);
    private static       boolean deduplicateRemoveSwitch = PropertiesConfiguration.getInstance().getBoolean("uniqueKey.deduplicate.remove.switch", true);

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        try {
            SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
            LinkNode fetched = ContextUtil.getFetchLinkNode(context);
            String websiteType = searchProcessor.getProcessorContext().getWebsite().getWebsiteType();
            DuplicateChecker checker = ContextUtil.getDuplicateChecker(context);
            Object uniqueKey = fetched.getProperty(AbstractData.UNIQUESIGN);
            if (deduplicateRemoveSwitch && uniqueKey != null && checker != null && searchProcessor.isDuplicateRemoval()) {
                if (checker.isDuplicate(websiteType, uniqueKey.toString())) {
                    fetched.setRemoved(true);
                    logger.info("Node:" + fetched + " filtered by uniqueKey:" + uniqueKey + "websiteType:" + websiteType);
                    // mark the task has been DuplicateRemoved
                    searchProcessor.getTask().setDuplicateRemoved(true);
                }
            }
            if (!fetched.isRemoved()) {
                filterChain.doFilter(context);
            } else {
                searchProcessor.getTask().getFilteredCount().getAndIncrement();
            }
        } catch (Exception e) {
            logger.error("do duplicate remove filter error " + e.getMessage(), e);
        }
    }
}
