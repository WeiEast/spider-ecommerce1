package com.datatrees.crawler.core.processor.operation;

import java.util.List;

import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.OperationException;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.extractor.FieldExtractResultSet;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.function.SpiderResponseFactory;
import com.treefinance.crawler.framework.context.pipeline.ProcessPipeline;
import com.treefinance.crawler.framework.exception.InvalidDataException;
import com.treefinance.crawler.framework.exception.InvalidOperationException;
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

    public Object start(String content, SpiderRequest request,
            FieldExtractResultSet fieldExtractResultSet) throws OperationException, ResultEmptyException {
        if (isPrepared()) {
            SpiderResponse resp = SpiderResponseFactory.make();
            ResponseUtil.setFieldExtractResultSet(resp, fieldExtractResultSet);
            String lastContent = RequestUtil.getContent(request);
            try {
                request.setInput(content);

                super.invoke(request, resp);

                OperationEntity outPut = (OperationEntity) resp.getOutPut();
                logger.debug("operations result: {}", outPut);
                if (outPut != null) {
                    return outPut.getData();
                } else {
                    logger.warn("Unexpected operation result! No operations processed for {}", fieldExtractor);
                }
            } catch (InvalidDataException e) {
                logger.warn("Error processing {} with operations! >> {}", fieldExtractor, e.getMessage());
                return null;
            } catch (InvalidOperationException e) {
                throw new OperationException("Unexpected exception with operations!", e);
            } catch (ResultEmptyException e) {
                throw e;
            } catch (Exception e) {
                throw new OperationException("Error invoking operations!", e);
            } finally {
                request.setInput(lastContent);
            }
        } else {
            logger.warn("Not found available operations for {}", fieldExtractor);
        }

        return content;
    }
}
