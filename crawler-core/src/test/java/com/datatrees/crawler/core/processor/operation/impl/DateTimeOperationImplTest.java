package com.datatrees.crawler.core.processor.operation.impl;

import com.treefinance.crawler.framework.context.pipeline.InvokeException;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.DateTimeOperation;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import org.junit.Test;

/**
 * @author Jerry
 * @since 11:30 2018/7/16
 */
public class DateTimeOperationImplTest {

    @Test
    public void invoke() throws InvokeException, ResultEmptyException {
        FieldExtractor extractor = new FieldExtractor();
        DateTimeOperation operation = new DateTimeOperation();
        operation.setBaseType("now");
        operation.setDateTimeFieldType("month");
        operation.setOffset("-6");
        operation.setFormat("yyyy-MM-dd");

        DateTimeOperationImpl impl = new DateTimeOperationImpl(operation, extractor);

        Request request = new Request();
        request.setInput("1967-01-01");
        Response response = new Response();
        impl.invoke(request, response);

        operation = new DateTimeOperation();
        operation.setBaseType("custom");
        operation.setSourceFormat("yyyy-MM-dd");
        operation.setFormat("timestamp");
        impl = new DateTimeOperationImpl(operation, extractor);
        impl.invoke(request, response);
        Object outPut = response.getOutPut();
        System.out.println(outPut);
    }
}