package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.TripleOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.triple.TripleType;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.treefinance.crawler.framework.exception.InvalidOperationException;
import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.lang3.StringUtils;

public class TripleOperationImpl extends Operation<TripleOperation> {

    public TripleOperationImpl(@Nonnull TripleOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected boolean isSkipped(TripleOperation operation, Request request, Response response) {
        // invalid xpath operation and skip
        boolean flag = StringUtils.isBlank(operation.getValue());
        if (flag) {
            logger.warn("Empty expression of triple operation and skip.");
        }
        return flag;
    }

    @Override
    protected Object doOperation(@Nonnull TripleOperation operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception {
        String expression = operation.getValue();

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

        String input = (String) operatingData;
        param1 = evalExp(param1, input, request, response);
        param2 = evalExp(param2, input, request, response);
        result1 = evalExp(result1, input, request, response);
        result2 = evalExp(result2, input, request, response);

        return type.calculate(param1, param2, result1, result2);
    }

    private String evalExp(String value, String operatingData, Request request, Response response) {
        String val = StringUtils.replace(value, "${this}", operatingData);

        return StandardExpression.eval(val, request, response);
    }
}
