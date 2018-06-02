package com.datatrees.crawler.core.processor.operation.impl;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.TripleOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.triple.TripleType;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
import com.treefinance.crawler.framework.exception.InvalidOperationException;
import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.lang3.StringUtils;

public class TripleOperationImpl extends Operation<TripleOperation> {

    @Override
    public void process(Request request, Response response) throws Exception {
        String input = OperationHelper.getStringInput(request, response);
        TripleOperation operation = getOperation();
        String expression = operation.getValue();
        logger.debug("triple expression: {}", expression);

        String result;
        if (StringUtils.isBlank(operation.getValue())) {
            result = input;
        } else {
            // ${this}=${a}?${b}:${c}
            TripleType type = operation.getTripleType();
            if (type == null) {
                type = TripleType.EQ;
            }

            String exp = type.getExpression();
            int i = expression.indexOf(exp);
            if (i == -1) {
                throw new InvalidOperationException("Invalid triple operation! - Triple expression was incorrect.");
            }

            String param1 = expression.substring(0, i);

            i = i + exp.length();
            int j = expression.indexOf("?", i);
            if (i == -1) {
                throw new InvalidOperationException("Invalid triple operation! - Triple expression was incorrect.");
            }

            String param2 = expression.substring(i, j);

            i = j + 1;
            j = expression.indexOf(":", i);
            if (j == -1) {
                throw new InvalidOperationException("Invalid triple operation! - Triple expression was incorrect.");
            }

            String result1 = expression.substring(i, j);
            String result2 = expression.substring(j + 1);

            param1 = evalExp(param1, input, request, response);
            param2 = evalExp(param2, input, request, response);
            result1 = evalExp(result1, input, request, response);
            result2 = evalExp(result2, input, request, response);

             result = type.calculate(param1, param2, result1, result2);
        }


        logger.debug("input: {}, output: {}", input, result);

        response.setOutPut(result);
    }

    private String evalExp(String value, String operatingData, Request request, Response response) {
        String val = StringUtils.replace(value, "${this}", operatingData);

        return StandardExpression.eval(val, request, response);
    }
}
