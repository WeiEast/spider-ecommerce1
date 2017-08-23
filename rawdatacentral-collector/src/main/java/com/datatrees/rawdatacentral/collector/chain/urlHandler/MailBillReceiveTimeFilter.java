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
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.chain.common.WebsiteType;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.core.common.UnifiedSysTime;
import com.datatrees.rawdatacentral.core.model.data.MailBillData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月7日 上午1:17:04
 */
public class MailBillReceiveTimeFilter implements Filter {

    private static final Logger  log                         = LoggerFactory.getLogger(MailBillReceiveTimeFilter.class);
    private static       long    maxMailReceiveInterval      = PropertiesConfiguration.getInstance().getLong("max.mail.receive.interval", new Double(1000L * 3600L * 24L * 365L * 1.5).longValue());
    private static       boolean mailReceiveTimeFilterSwitch = PropertiesConfiguration.getInstance().getBoolean("mail.receive.filter.switch", true);

    /*
     * (non-Javadoc)
     * 
     * @see
     * Filter#doFilter(com.datatrees.rawdatacentral.collector.chain
     * .Context, FilterChain)
     */
    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        try {
            SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
            LinkNode fetchedLinkNode = ContextUtil.getFetchLinkNode(context);
            String websiteType = searchProcessor.getProcessorContext().getWebsite().getWebsiteType();
            Object receiveAt = fetchedLinkNode.getPropertys().get(MailBillData.RECEIVED);
            LinkNode currentLinkNode = ContextUtil.getCurrentLinkNode(context);

            if (searchProcessor.isLastLink() && currentLinkNode != null && currentLinkNode.getpNum() > 0) {
                log.info("filter pageNode:" + fetchedLinkNode + " as LastPageLink marked ,receiveAt:" + receiveAt);
                fetchedLinkNode.setRemoved(true);
            } else if (mailReceiveTimeFilterSwitch && receiveAt != null && receiveAt instanceof Date && websiteType != null && WebsiteType.MAIL.getValue().equals(websiteType) && SearchType.KEYWORD_SEARCH.equals(searchProcessor.getSearchTemplateConfig().getType())) {
                log.debug("MailBillReceiveTimeFilter execute bagin ...");
                if ((UnifiedSysTime.INSTANCE.getSystemTime().getTime() - ((Date) receiveAt).getTime() > maxMailReceiveInterval)) {
                    log.info("Node:" + fetchedLinkNode + ",receiveAt:" + receiveAt + " receivetime filtered...");
                    fetchedLinkNode.setRemoved(true);
                    if (currentLinkNode != null && currentLinkNode.getpNum() > 0) {
                        log.info("receivetime come to threshold, mark as the LastPageLink ...");
                        searchProcessor.setLastLink(true);
                        searchProcessor.getProcessorContext().getProcessorResult().put("LastPageLink", true);
                    }
                }
            }
            if (!fetchedLinkNode.isRemoved()) {
                filterChain.doFilter(context, filterChain);
            } else {
                searchProcessor.getTask().getFilteredCount().getAndIncrement();
            }
        } catch (Exception e) {
            log.error("do mailBill receivetime filter error " + e.getMessage(), e);
        }

    }
}
