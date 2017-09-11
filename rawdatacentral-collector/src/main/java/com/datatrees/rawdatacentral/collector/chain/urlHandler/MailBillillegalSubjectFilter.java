/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.chain.urlHandler;

import java.util.regex.Pattern;

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
public class MailBillillegalSubjectFilter implements Filter {

    private static final Logger  logger                        = LoggerFactory.getLogger(MailBillillegalSubjectFilter.class);
    private static       Pattern subjectContainsPattern        = Pattern.compile(PropertiesConfiguration.getInstance().get("mail.subject.contains.pattern", "账单|信用卡|对账"));
    private static       Pattern subjectBlackListPattern       = Pattern.compile(PropertiesConfiguration.getInstance().get("mail.subject.blacklist.pattern", "消费提醒全面升级-每日信用管家|微·服务】|招商银行信用卡账单分期体验调查|信用卡账单分期订单已成功|每日E-Mail账单提醒|凤凰知音深航会员|天了噜~极速体验账单|分期乐提醒您|账单分期订单已成功|月里程账单|“广发信用卡”微信|账单信息Biu的|携程对账单|QQ邮箱动态|一键“狙击”账单|人人贷|携程旅行网|QQ邮箱账单|（AD）|您的京东|挖财|支付宝|话费|电信|快钱|快递|网易|话单|账单杂志|保险|电费|基金|证券|POS|自动回复|汇添富|地址|星座|易方达|人寿|有限公司|（月度）|易宝|南方电网|致谢函|贷款|电局|已读:|借记卡|温馨提示|额度调升|分期大回馈|分期感恩季"));
    private static       Pattern secondSubjectBlackListPattern = Pattern.compile(PropertiesConfiguration.getInstance().get("mail.subject.second.blacklist.pattern", "分期感恩季"));
    private static       Pattern subjectWhilteListPattern      = Pattern.compile(PropertiesConfiguration.getInstance().get("mail.subject.whiltelist.pattern", "招商"));

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        try {
            SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
            LinkNode fetched = ContextUtil.getFetchLinkNode(context);
            String websiteType = searchProcessor.getProcessorContext().getWebsite().getWebsiteType();
            Object subject = fetched.getProperty(MailBillData.SUBJECT);
            if (subject != null && websiteType != null && WebsiteType.MAIL.getValue().equals(websiteType) && SearchType.KEYWORD_SEARCH.equals(searchProcessor.getSearchTemplateConfig().getType())) {
                logger.debug("MailBillillegalSubjectFilter execute bagin ...");
                if (PatternUtils.match(subjectContainsPattern, subject.toString().replaceAll("\\s", ""))) {
                    if (PatternUtils.match(subjectBlackListPattern, subject.toString())) {
                        if (!PatternUtils.match(subjectWhilteListPattern, subject.toString()) || PatternUtils.match(secondSubjectBlackListPattern, subject.toString())) {
                            logger.info("Node:" + fetched + ",subject:" + subject + " filtered by subject blacklist.");
                            fetched.setRemoved(true);
                        }
                    }
                } else {
                    fetched.setRemoved(true);
                    logger.info("Node:" + fetched + " filtered as " + subject + " discontain '{}'", searchProcessor.getKeyword());
                }
            }
            if (!fetched.isRemoved()) {
                filterChain.doFilter(context, filterChain);
            } else {
                searchProcessor.getTask().getFilteredCount().getAndIncrement();
            }
        } catch (Exception e) {
            logger.error("do mailBill illegalSubject filter error " + e.getMessage(), e);
        }
    }
}
