package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.JsonPathOperation;
import com.datatrees.crawler.core.processor.operation.Operation;
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
    protected Object doOperation(@Nonnull JsonPathOperation operation, @Nonnull Object operatingData, @Nonnull Request request,
            @Nonnull Response response) throws Exception {
        String original = (String) operatingData;

        String jsonpath = operation.getJsonpath();

        jsonpath = StandardExpression.eval(jsonpath, request, response);

        try {
            original = JsonPathUtil.readAsString(original, jsonpath);
        } catch (Exception e) {
            logger.error("Error extracting content with jsonpath: {}", jsonpath, e);
            original = null;
        }

        return original;
    }
}
