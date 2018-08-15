package com.treefinance.crawler.framework.process.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.JsonPathOperation;
import com.treefinance.crawler.framework.process.operation.Operation;
import com.datatrees.crawler.core.util.json.JsonPathUtil;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.InvalidPathException;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.exception.InvalidDataException;
import com.treefinance.crawler.framework.exception.InvalidOperationException;
import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @datetime 2015-07-17 20:02
 */
public class JsonPathOperationImpl extends Operation<JsonPathOperation> {

    public JsonPathOperationImpl(@Nonnull JsonPathOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected void validate(@Nonnull JsonPathOperation operation, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        super.validate(operation, request, response);

        if (StringUtils.isEmpty(operation.getJsonpath())) {
            throw new InvalidOperationException("Invalid jsonpath operation! - 'jsonpath/text()' must not be empty.");
        }
    }

    @Override
    protected Object doOperation(@Nonnull JsonPathOperation operation, @Nonnull Object operatingData, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        String jsonpath = operation.getJsonpath();

        jsonpath = StandardExpression.eval(jsonpath, request, response);

        logger.debug("Actual jsonpath: {}", jsonpath);

        if (StringUtils.isBlank(jsonpath)) {
            throw new InvalidOperationException("Incorrect jsonpath! \nOriginal Jsonpath: " + operation.getJsonpath() + "\nActual Jsonpath: " + jsonpath);
        }

        String input = (String) operatingData;

        String result;
        try {
            result = JsonPathUtil.readAsString(input, jsonpath);
        } catch (InvalidJsonException e) {
            throw new InvalidDataException("Invalid operating data! >> " + e.getMessage() + "\nOriginal Jsonpath: " + operation.getJsonpath() + "\nActual Jsonpath: " + jsonpath + "\nInput:\n" + input, e);
        } catch (InvalidPathException e) {
            throw new InvalidOperationException("Incorrect jsonpath! >> " + e.getMessage() + "\nOriginal Jsonpath: " + operation.getJsonpath() + "\nActual Jsonpath: " + jsonpath, e);
        } catch (Exception e) {
            throw new InvalidOperationException("Error parsing with jsonpath! >> " + e.getMessage() + "\nOriginal Jsonpath: " + operation.getJsonpath() + "\nActual Jsonpath: " + jsonpath + "\nInput:\n" + input, e);
        }

        if (result.isEmpty() && BooleanUtils.isTrue(operation.getEmptyToNull())) {
            result = null;
        }

        return result;
    }
}
