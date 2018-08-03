/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.service.collector.chain.urlHandler;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.domain.config.search.SearchType;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorResult;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.spider.share.service.collector.chain.Context;
import com.datatrees.spider.share.service.domain.data.MailBillData;
import com.datatrees.spider.share.domain.website.WebsiteType;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月7日 上午12:42:44
 */
public class MailBillOutboxFilter extends RemovedFetchLinkNodeFilter {

    private static String[] recipientPatternKeys = PropertiesConfiguration.getInstance().get("mail.recipient.pattern.key", "emailAccount,qqAccount")
            .split(",");

    private static String   outBoxFolderName     = PropertiesConfiguration.getInstance().get("mail.outbox.folder.name", "已发送");

    @Override
    protected void doProcess(LinkNode fetchLinkNode, SearchProcessor searchProcessor, Context context) {
        String websiteType = searchProcessor.getProcessorContext().getWebsite().getWebsiteType();
        Object sender = fetchLinkNode.getProperty(MailBillData.SENDER);
        Object folder = fetchLinkNode.getProperty(MailBillData.FOLDER);
        // use sender to detect mail in outbox
        if (websiteType != null && WebsiteType.MAIL.getValue().equals(websiteType) &&
                SearchType.KEYWORD_SEARCH.equals(searchProcessor.getSearchTemplateConfig().getType())) {
            boolean inOutbox = false;
            if (folder != null && PatternUtils.match(outBoxFolderName, folder.toString())) {
                inOutbox = true;
            }

            if (!inOutbox && sender != null) {
                ProcessorResult result = searchProcessor.getProcessorContext().getProcessorResult();
                for (String key : recipientPatternKeys) {
                    Object value = result.get(key);
                    // mail is send by self
                    if (value != null && StringUtils.isNotBlank(value.toString()) && PatternUtils.match(value.toString(), sender.toString())) {
                        inOutbox = true;
                        break;
                    }
                }
            }
            if (inOutbox) {
                logger.info("Node: {} filtered as in outbox.", fetchLinkNode);
                fetchLinkNode.setRemoved(true);
            }
        }
    }

}
