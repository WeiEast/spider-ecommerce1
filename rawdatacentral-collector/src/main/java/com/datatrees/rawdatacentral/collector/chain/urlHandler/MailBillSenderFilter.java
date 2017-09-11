/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.chain.urlHandler;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.domain.config.search.SearchType;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.domain.enums.WebsiteType;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.core.model.data.MailBillData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月7日 上午12:42:44
 */
public class MailBillSenderFilter implements Filter {

    private static final Logger logger                 = LoggerFactory.getLogger(MailBillSenderFilter.class);
    private static       String senderBlackListPattern = PropertiesConfiguration.getInstance().get("mail.sender.blacklist.pattern", "@lakala.com|^10086@139.com|^mail139@139.com|^10000@qq.com|@51zhangdan.com.cn|@wacai.com|magazine@shenzhenair.com|@feidee.com|@news1.elong.com|service@service.fenqile.com|no-reply@notice.jimubox.com|noreply@ppdai.com|cmpassport139@139.com|xinyue@tencent.com|Info@ctrip.com|week@feidee.net|service@iboxpay.com|noreply@firstp2p.com|@ygdai.com");

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        try {
            SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
            LinkNode fetched = ContextUtil.getFetchLinkNode(context);
            String websiteType = searchProcessor.getProcessorContext().getWebsite().getWebsiteType();

            if (websiteType != null && WebsiteType.MAIL.getValue().equals(websiteType) && SearchType.KEYWORD_SEARCH.equals(searchProcessor.getSearchTemplateConfig().getType())) {
                logger.debug("MailBillSenderFilter execute begin ...");
                Object sender = fetched.getProperty(MailBillData.SENDER);
                if (sender != null && PatternUtils.match(senderBlackListPattern, sender.toString().toLowerCase())) {
                    logger.info("Node:" + fetched + " filtered as the sender is " + sender);
                    fetched.setRemoved(true);
                }
            }
            if (!fetched.isRemoved()) {
                filterChain.doFilter(context, filterChain);
            } else {
                searchProcessor.getTask().getFilteredCount().getAndIncrement();
            }
        } catch (Exception e) {
            logger.error("do mailBill SenderFilter filter error " + e.getMessage(), e);
        }
    }
}
