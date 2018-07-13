package com.treefinance.crawler.framework.format.datetime;

import java.util.Date;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.exception.FormatException;
import org.junit.Test;

/**
 * @author Jerry
 * @since 18:42 2018/7/13
 */
public class DateFormatterTest {

    @Test
    public void format() throws FormatException {
        DateFormatter formatter = new DateFormatter();

        Date date = formatter.format("2018-07-13 23:35:59", "yyyy-MM-dd hh:mm:ss", new Request(), new Response());
        System.out.println(date);
    }
}