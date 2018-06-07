package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.JsonPathOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
import com.datatrees.crawler.core.util.json.JsonPathUtil;
import com.treefinance.crawler.framework.expression.StandardExpression;

/**
 * @author Jerry
 * @datetime 2015-07-17 20:02
 */
public class JsonPathOperationImpl extends Operation<JsonPathOperation> {

    public JsonPathOperationImpl(@Nonnull JsonPathOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        String original = OperationHelper.getStringInput(request, response);

        JsonPathOperation operation = getOperation();

        String jsonpath = operation.getJsonpath();

        jsonpath = StandardExpression.eval(jsonpath, request, response);

        try {
            original = JsonPathUtil.readAsString(original, jsonpath);
        } catch (Exception e) {
            logger.error("Error extracting content with jsonpath: {}", jsonpath, e);
            original = null;
        }

        logger.debug("jsonPath extract result: {}", original);

        response.setOutPut(original);
    }
}
