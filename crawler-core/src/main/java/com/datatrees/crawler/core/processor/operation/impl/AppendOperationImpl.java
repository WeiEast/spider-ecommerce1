package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.AppendOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.lang.StringUtils;

public class AppendOperationImpl extends Operation<AppendOperation> {

    public AppendOperationImpl(@Nonnull AppendOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        AppendOperation operation = getOperation();
        if (logger.isDebugEnabled()) {
            logger.debug("AppendOperation : {}", GsonUtils.toJson(operation));
        }

        int index = operation.getIndex();
        String value = operation.getValue();

        value = StandardExpression.eval(value, request, response);

        logger.debug("Actual append text: {}", value);

        String input = OperationHelper.getStringInput(request, response);

        logger.debug("AppendOperation, input : {}", input);

        StringBuilder result = new StringBuilder();
        if (index < 0) {
            result.append(input).append(value);
        } else {
            result.append(StringUtils.substring(input, 0, index)).append(value).append(StringUtils.substring(input, index, input.length()));
        }

        String outPut = result.toString();

        logger.debug("AppendOperation, output: {}", outPut);

        response.setOutPut(outPut);
    }

}
