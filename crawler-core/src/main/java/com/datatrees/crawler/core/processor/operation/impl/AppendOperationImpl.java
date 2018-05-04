package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.operation.impl.AppendOperation;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
import org.apache.commons.lang.StringUtils;

public class AppendOperationImpl extends Operation<AppendOperation> {

    @Override
    public void process(Request request, Response response) throws Exception {
        AppendOperation operation = getOperation();
        if (logger.isDebugEnabled()) {
            logger.debug("AppendOperation : {}", GsonUtils.toJson(operation));
        }

        int index = operation.getIndex();
        String value = operation.getValue();
        Map<String, Object> fieldContext = FieldExtractorWarpperUtil.fieldWrapperMapToField(ResponseUtil.getResponseFieldResult(response));
        Map<String, Object> sourceMap = RequestUtil.getSourceMap(request);

        value = ReplaceUtils.replaceMap(fieldContext, sourceMap, value);

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
