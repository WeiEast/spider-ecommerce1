package com.datatrees.rawdatacentral.collector.chain.urlHandler;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.collector.worker.deduplicate.DuplicateChecker;
import com.datatrees.rawdatacentral.core.model.data.AbstractData;

/**
 * @author <A HREF="mailto:deng_kui@vobile.cn">Deng Kui</A>
 * @version 1.0
 * @since 2014-6-16 上午11:15:39
 */
public class DuplicateRemoveFilter extends RemovedFetchLinkNodeFilter {

    private static       boolean deduplicateRemoveSwitch = PropertiesConfiguration.getInstance().getBoolean("uniqueKey.deduplicate.remove.switch", true);

    @Override
    protected void doProcess(LinkNode fetchLinkNode, SearchProcessor searchProcessor, Context context) {
        String websiteType = searchProcessor.getProcessorContext().getWebsite().getWebsiteType();
        DuplicateChecker checker = ContextUtil.getDuplicateChecker(context);
        Object uniqueKey = fetchLinkNode.getProperty(AbstractData.UNIQUESIGN);
        if (deduplicateRemoveSwitch && uniqueKey != null && checker != null && searchProcessor.isDuplicateRemoval()) {
            if (checker.isDuplicate(websiteType, uniqueKey.toString())) {
                fetchLinkNode.setRemoved(true);
                logger.info("Node:" + fetchLinkNode + " filtered by uniqueKey:" + uniqueKey + "websiteType:" + websiteType);
                // mark the task has been DuplicateRemoved
                searchProcessor.getTask().setDuplicateRemoved(true);
            }
        }
    }

}
