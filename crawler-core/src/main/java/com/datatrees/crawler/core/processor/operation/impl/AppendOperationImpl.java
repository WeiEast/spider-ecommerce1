package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.AppendOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.lang.StringUtils;

public class AppendOperationImpl extends Operation<AppendOperation> {

    public AppendOperationImpl(@Nonnull AppendOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected Object doOperation(@Nonnull AppendOperation operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception {
        int index = operation.getIndex();
        String value = operation.getValue();

        value = StandardExpression.eval(value, request, response);

        logger.debug("Actual append text: {}", value);

        String input = (String) operatingData;

        String outPut;
        if (index < 0) {
            outPut = input + value;
        } else {
            outPut = StringUtils.substring(input, 0, index) + value + StringUtils.substring(input, index, input.length());
        }

        return outPut;
    }

}
