package com.datatrees.rawdatacentral.collector.chain.urlHandler;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.spider.share.service.domain.data.EcommerceData;
import com.datatrees.spider.share.domain.website.WebsiteType;

public class EcommerceTitleFilter extends RemovedFetchLinkNodeFilter {

    private static String titleBlackListPattern      = PropertiesConfiguration.getInstance()
            .get("ecommerce.title.blacklist.pattern", "充值|电影票|提现|转账|话费|自动发货|代金劵");

    private static String EcommerceTitleFilterSwitch = PropertiesConfiguration.getInstance().get("ecommerce.title.filter.switch", "off");

    private static String titleWhiteListPattern      = PropertiesConfiguration.getInstance().get("ecommerce.title.whiltelist.pattern", "淘宝购物");

    @Override
    protected void doProcess(LinkNode fetchLinkNode, SearchProcessor searchProcessor, Context context) {
        String websiteType = searchProcessor.getProcessorContext().getWebsite().getWebsiteType();
        Object title = fetchLinkNode.getProperty(EcommerceData.TITLE);
        if (EcommerceTitleFilterSwitch.toLowerCase().equals("on") && title != null && websiteType != null &&
                WebsiteType.ECOMMERCE.getValue().equals(websiteType)) {
            if (PatternUtils.match(titleWhiteListPattern, title.toString())) {
                if (!PatternUtils.match(titleBlackListPattern, title.toString())) {
                    logger.info("ECOMMERCE Node: {} filter success...", fetchLinkNode);
                    fetchLinkNode.setRemoved(false);
                } else {
                    logger.info("ECOMMERCE Node: {} filtered by title blacklist...", fetchLinkNode);
                    // in black list need filter
                    fetchLinkNode.setRemoved(true);
                }
            } else {
                // not in whilte list need filter
                logger.info("ECOMMERCE Node: {} filtered by title whitelist...", fetchLinkNode);
                fetchLinkNode.setRemoved(true);
            }
        }
    }

}
