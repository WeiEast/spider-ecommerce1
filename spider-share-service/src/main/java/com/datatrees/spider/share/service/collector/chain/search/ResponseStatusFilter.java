package com.datatrees.spider.share.service.collector.chain.search;

import javax.annotation.Nonnull;
import java.util.List;

import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.spider.share.service.collector.chain.Context;
import com.datatrees.spider.share.service.collector.search.SearchProcessor;
import com.datatrees.spider.share.domain.model.Task;
import com.datatrees.spider.share.domain.ErrorCode;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午3:19:47
 */
public class ResponseStatusFilter extends ResponsesFilter {

    @Override
    protected void doInternalFilter(@Nonnull List<Response> responses, SearchProcessor searchProcessor, Context context) {
        Task task = searchProcessor.getTask();
        for (Response response : responses) {

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
                log.info("Access block, the system will early quit ,workingTaskEntity_id : {}", task.getId());
                task.setErrorCode(ErrorCode.BLOCKED_ERROR_CODE);
                searchProcessor.setNeedEarlyQuit(true);
            }

            if (Status.NO_SEARCH_RESULT == codeStatus) {
                task.setErrorCode(ErrorCode.NO_RESULT_ERROR_CODE);
            }

            if (Status.NO_PROXY == codeStatus) {
                log.info("no proxy get from wiseproxy ,workingTaskEntity_id : {}", task.getId());
                task.setErrorCode(ErrorCode.NO_ACTIVE_PROXY);
            }

        }
    }

}
