package com.datatrees.rawdatacentral.collector.chain.search;

import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.common.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午3:19:47
 */
public class ResponseStatusFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ResponseStatusFilter.class);

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
        Task task = searchProcessor.getTask();

        List<Response> threadLocalResponseList = ProcessorContextUtil.getThreadLocalResponseList(searchProcessor.getProcessorContext());
        if (threadLocalResponseList != null) {
            for (Response response : threadLocalResponseList) {

                int codeStatus = ResponseUtil.getResponseStatus(response);

                if (Status.FILTERED == codeStatus) {
                    task.getFilteredCount().incrementAndGet();
                } else if (codeStatus < Status.REQUEUE) {
                    // record download url
                    task.getOpenUrlCount().incrementAndGet();
                }

                if (StringUtils.isNotBlank(ResponseUtil.getResponseErrorMsg(response))) {
                    // record failed count
                    task.getRequestFailedCount().incrementAndGet();
                }

                if (Status.BLOCKED == codeStatus) {
                    log.info("Access block, the system will early quit ,workingTaskEntity_id : " + task.getId());
                    task.setErrorCode(ErrorCode.BLOCKED_ERROR_CODE);
                    searchProcessor.setNeedEarlyQuit(true);
                }

                if (Status.NO_SEARCH_RESULT == codeStatus) {
                    task.setErrorCode(ErrorCode.NO_RESULT_ERROR_CODE);
                }

                if (Status.NO_PROXY == codeStatus) {
                    log.info("no proxy get from wiseproxy ,workingTaskEntity_id : " + task.getId());
                    task.setErrorCode(ErrorCode.NO_ACTIVE_PROXY);
                }

            }
        }
        log.debug("Execute ResponseStatusFilter end...");
        filterChain.doFilter(context, filterChain);
    }



}
