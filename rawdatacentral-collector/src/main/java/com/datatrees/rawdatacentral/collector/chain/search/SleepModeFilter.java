package com.datatrees.rawdatacentral.collector.chain.search;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.ResponseUtil;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午3:20:11
 */
public class SleepModeFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(SleepModeFilter.class);

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
        CrawlResponse response = ContextUtil.getCrawlResponse(context);
        int codeStatus = ResponseUtil.getResponseStatus(response);
        if (Status.FILTERED != codeStatus) {
            long waitIntervalMillis = searchProcessor.getWaitIntervalMillis();
            if (0 != waitIntervalMillis) {
                try {
                    Thread.currentThread().sleep(waitIntervalMillis);
                } catch (InterruptedException e) {
                    log.error("doFilter error context={}", GsonUtils.toJson(context), e);
                }
            }
            log.debug("Execute SleepModeFilter end...");
        }
        filterChain.doFilter(context, filterChain);
    }
}
