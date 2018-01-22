package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.AppendOperation;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.apache.commons.lang.StringUtils;

public class AppendOperationImpl extends Operation {

    @Override
    public void process(Request request, Response response) throws Exception {
        AppendOperation operation = (AppendOperation) getOperation();
        int index = operation.getIndex();
        String value = operation.getValue();
        String orginal = getInput(request, response);

        if (logger.isDebugEnabled()) {
            logger.debug("AppendOperation input: " + String.format("value: %s, index: %d", value, index));
        }

        Map<String, Object> fieldContext = FieldExtractorWarpperUtil.fieldWrapperMapToField(ResponseUtil.getResponseFieldResult(response));
        Map<String, Object> sourceMap = RequestUtil.getSourceMap(request);

        value = ReplaceUtils.replaceMap(fieldContext, sourceMap, value);

        StringBuilder result = new StringBuilder();
        if (index < 0) {
            result.append(orginal).append(value);
        } else {
            result.append(StringUtils.substring(orginal, 0, index)).append(value).append(StringUtils.substring(orginal, index, orginal.length()));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("AppendOperation content: " + String.format("orginal: %s , dest: %s", orginal, result));
        }

        response.setOutPut(result.toString());
    }

}
