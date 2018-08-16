package com.datatrees.spider.share.service.collector.chain.search;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.spider.share.service.collector.chain.Context;
import com.datatrees.spider.share.service.collector.chain.Filter;
import com.datatrees.spider.share.service.collector.chain.FilterChain;
import com.datatrees.spider.share.service.collector.search.SearchProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午3:20:11
 */
public class SleepModeFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(SleepModeFilter.class);

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        CrawlResponse response = context.getCrawlResponse();
        int codeStatus = response.getStatus();
        if (Status.FILTERED != codeStatus) {
            SearchProcessor searchProcessor = context.getSearchProcessor();
            long waitIntervalMillis = searchProcessor.getWaitIntervalMillis();
            if (0 != waitIntervalMillis) {
                try {
                    Thread.sleep(waitIntervalMillis);
                } catch (InterruptedException e) {
                    log.error("doFilter error context={}", GsonUtils.toJson(context), e);
                }
            }
        }
        filterChain.doFilter(context);
    }
}
