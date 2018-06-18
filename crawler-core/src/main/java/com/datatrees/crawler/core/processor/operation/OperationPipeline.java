package com.datatrees.crawler.core.processor.operation;

import java.util.List;

import com.datatrees.common.pipeline.ProcessPipeline;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.OperationException;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.extractor.FieldExtractResultSet;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author Jerry
 * @since 20:59 2018/5/14
 */
public class OperationPipeline extends ProcessPipeline {

    private final FieldExtractor fieldExtractor;

    public OperationPipeline(FieldExtractor fieldExtractor) {
        this.fieldExtractor = fieldExtractor;
        List<AbstractOperation> operations = fieldExtractor.getOperationList();
        if (CollectionUtils.isNotEmpty(operations)) {
            for (AbstractOperation operation : operations) {
                if (operation == null) continue;
                addValve(ProcessorFactory.getOperation(operation, fieldExtractor));
            }
        }
    }

    public Object start(String content, Request request, FieldExtractResultSet fieldExtractResultSet) throws OperationException, ResultEmptyException {
        if (isPrepared()) {
            Response resp = new Response();
            ResponseUtil.setFieldExtractResultSet(resp, fieldExtractResultSet);
            String lastContent = RequestUtil.getContent(request);
            try {
                request.setInput(content);
                super.invoke(request, resp);
                return resp.getOutPut();
            } catch (ResultEmptyException e) {
                throw e;
            } catch (Exception e) {
                throw new OperationException("Error invoking operations!", e);
            } finally {
                request.setInput(lastContent);
            }
        } else {
            logger.warn("Not found available operations for field: {}", fieldExtractor.getField());
        }

        return content;
    }
}
