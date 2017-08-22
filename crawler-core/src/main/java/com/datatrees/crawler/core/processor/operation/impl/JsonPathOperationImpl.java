package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.JsonPathOperation;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.util.json.JsonPathUtil;

/**
 * @author Jerry
 * @datetime 2015-07-17 20:02
 */
public class JsonPathOperationImpl extends Operation {

    private static final Logger log = LoggerFactory.getLogger(JsonPathOperationImpl.class);

    @Override
    public void process(Request request, Response response) throws Exception {

        String original = getInput(request, response);

        JsonPathOperation operation = (JsonPathOperation) getOperation();

        String jsonpath = operation.getJsonpath();
        // replace from context
        Map<String, Object> fieldContext = FieldExtractorWarpperUtil.fieldWrapperMapToField(ResponseUtil.getResponseFieldResult(response));
        Map<String, Object> sourceMap = RequestUtil.getSourceMap(request);

        jsonpath = ReplaceUtils.replaceMap(fieldContext, sourceMap, jsonpath);

        try {
            original = JsonPathUtil.readAsString(original, jsonpath);
        } catch (Exception e) {
            log.error("jsonpath extract empty content! " + jsonpath + "exception :" + e.getMessage());
            original = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("jsonPath extract result:" + original);
        }

        response.setOutPut(original);
    }
}
