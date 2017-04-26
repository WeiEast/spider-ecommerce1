package com.datatrees.rawdatacentral.collector.chain.search;

import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.ProtocolOutput;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.rawdatacentral.domain.common.Task;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午3:19:33
 */
public class RecordNetworkTrafficFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RecordNetworkTrafficFilter.class);

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
        Task task = searchProcessor.getTask();
        List<Response> threadLocalResponseList = ProcessorContextUtil.getThreadLocalResponseList(searchProcessor.getProcessorContext());
        if (threadLocalResponseList != null) {
            for (Response response : threadLocalResponseList) {
                ProtocolOutput outPut = ResponseUtil.getProtocolResponse(response);
                if (outPut != null && outPut.getContent() != null && outPut.getContent().getContent() != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("add new networkTraffic length:" + outPut.getContent().getContent().length);
                    }
                    task.getNetworkTraffic().addAndGet(outPut.getContent().getContent().length);
                }
            }
        }
        log.debug("Execute RecordNetworkTrafficFilter end...");
        filterChain.doFilter(context, filterChain);
    }

}
