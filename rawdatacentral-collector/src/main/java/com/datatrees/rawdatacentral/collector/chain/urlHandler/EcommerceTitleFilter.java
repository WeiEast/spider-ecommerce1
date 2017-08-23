package com.datatrees.rawdatacentral.collector.chain.urlHandler;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.chain.common.WebsiteType;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.core.model.data.EcommerceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EcommerceTitleFilter implements Filter {

    private static final Logger logger                     = LoggerFactory.getLogger(EcommerceTitleFilter.class);
    private static       String titleBlackListPattern      = PropertiesConfiguration.getInstance().get("ecommerce.title.blacklist.pattern", "充值|电影票|提现|转账|话费|自动发货|代金劵");
    private static       String EcommerceTitleFilterSwitch = PropertiesConfiguration.getInstance().get("ecommerce.title.filter.switch", "off");
    private static       String titleWhilteListPattern     = PropertiesConfiguration.getInstance().get("ecommerce.title.whiltelist.pattern", "淘宝购物");

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        try {
            SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
            LinkNode fetched = ContextUtil.getFetchLinkNode(context);
            String websiteType = searchProcessor.getProcessorContext().getWebsite().getWebsiteType();
            Object title = fetched.getProperty(EcommerceData.TITLE);
            if (EcommerceTitleFilterSwitch.toLowerCase().equals("on") && title != null && websiteType != null && WebsiteType.ECOMMERCE.getValue().equals(websiteType)) {
                logger.debug("ECOMMERCE TitleFilter execute bagin ...");
                if (PatternUtils.match(titleWhilteListPattern, title.toString())) {
                    if (!PatternUtils.match(titleBlackListPattern, title.toString())) {
                        logger.info("ECOMMERCE Node:" + fetched + " filter success...");
                        fetched.setRemoved(false);
                    } else {
                        logger.info("ECOMMERCE Node:" + fetched + " filtered by title blacklist...");
                        // in black list need filter
                        fetched.setRemoved(true);
                    }
                } else {
                    // not in whilte list need filter
                    logger.info("ECOMMERCE Node:" + fetched + " filtered by title whiltelist...");
                    fetched.setRemoved(true);
                }
            }
            if (!fetched.isRemoved()) {
                filterChain.doFilter(context, filterChain);
            } else {
                searchProcessor.getTask().getFilteredCount().getAndIncrement();
            }
        } catch (Exception e) {
            logger.error("do ECOMMERCE Title Filter error " + e.getMessage(), e);
        }
    }

}
