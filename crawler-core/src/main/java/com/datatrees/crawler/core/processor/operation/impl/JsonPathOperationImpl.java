package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.JsonPathOperation;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
import com.datatrees.crawler.core.util.json.JsonPathUtil;

/**
 * @author Jerry
 * @datetime 2015-07-17 20:02
 */
public class JsonPathOperationImpl extends Operation<JsonPathOperation> {

    @Override
    public void process(Request request, Response response) throws Exception {
        String original = OperationHelper.getStringInput(request, response);

        JsonPathOperation operation = getOperation();

        String jsonpath = operation.getJsonpath();
        // replace from context
        Map<String, Object> fieldContext = FieldExtractorWarpperUtil.fieldWrapperMapToField(ResponseUtil.getResponseFieldResult(response));
        Map<String, Object> sourceMap = RequestUtil.getSourceMap(request);

        jsonpath = ReplaceUtils.replaceMap(fieldContext, sourceMap, jsonpath);

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
