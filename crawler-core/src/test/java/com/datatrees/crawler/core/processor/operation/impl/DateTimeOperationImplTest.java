package com.datatrees.crawler.core.processor.operation.impl;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.operation.impl.DateTimeOperation;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 * @author Jerry
 * @since 23:00 21/05/2017
 */
public class DateTimeOperationImplTest {
    @Test
    public void process() throws Exception {
        DateTimeOperationImpl impl = new DateTimeOperationImpl();
        DateTimeOperation operation = new DateTimeOperation();
        operation.setOffset("-6");
        operation.setBaseType("custom");
        operation.setDateTimeFieldType("month");
        operation.setCalibrate(true);
        operation.setFormat("timestamp");
        impl.setOperation(operation);
        Response response = new Response();
        response.setOutPut(new DateTime());
        impl.process(new Request(), response);
        System.out.println(response.getOutPut());
    }

}