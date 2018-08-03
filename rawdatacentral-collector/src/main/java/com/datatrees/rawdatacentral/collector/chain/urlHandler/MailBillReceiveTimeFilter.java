/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.chain.urlHandler;

import java.util.Date;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.domain.config.search.SearchType;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.spider.share.service.util.UnifiedSysTime;
import com.datatrees.spider.share.service.domain.data.MailBillData;
import com.datatrees.spider.share.domain.website.WebsiteType;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月7日 上午1:17:04
 */
public class MailBillReceiveTimeFilter extends RemovedFetchLinkNodeFilter {

    private static long    maxMailReceiveInterval      = PropertiesConfiguration.getInstance()
            .getLong("max.mail.receive.interval", new Double(1000L * 3600L * 24L * 365L * 1.5).longValue());

    private static boolean mailReceiveTimeFilterSwitch = PropertiesConfiguration.getInstance().getBoolean("mail.receive.filter.switch", true);

    @Override
    protected void doProcess(LinkNode fetchLinkNode, SearchProcessor searchProcessor, Context context) {
        String websiteType = searchProcessor.getProcessorContext().getWebsite().getWebsiteType();
        Object receiveAt = fetchLinkNode.getPropertys().get(MailBillData.RECEIVED);
        LinkNode currentLinkNode = context.getCurrentLinkNode();

        if (searchProcessor.isLastLink() && currentLinkNode != null && currentLinkNode.getpNum() > 0) {
            logger.info("filter pageNode: {} as LastPageLink marked ,receiveAt: {}", fetchLinkNode, receiveAt);
            fetchLinkNode.setRemoved(true);
        } else if (mailReceiveTimeFilterSwitch && receiveAt != null && receiveAt instanceof Date && websiteType != null &&
                WebsiteType.MAIL.getValue().equals(websiteType) &&
                SearchType.KEYWORD_SEARCH.equals(searchProcessor.getSearchTemplateConfig().getType())) {
            if ((UnifiedSysTime.INSTANCE.getSystemTime().getTime() - ((Date) receiveAt).getTime() > maxMailReceiveInterval)) {
                logger.info("Node: {},receiveAt: {} receive time filtered...", fetchLinkNode, receiveAt);
                fetchLinkNode.setRemoved(true);
                if (currentLinkNode != null && currentLinkNode.getpNum() > 0) {
                    logger.info("receive time come to threshold, mark as the LastPageLink ...");
                    searchProcessor.setLastLink(true);
                    searchProcessor.getProcessorContext().getProcessorResult().put("LastPageLink", true);
                }
            }
        }
    }

}
