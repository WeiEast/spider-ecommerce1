package com.treefinance.crawler.framework.format.datetime;

import java.util.Date;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.exception.FormatException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jerry
 * @since 18:42 2018/7/13
 */
public class DateFormatterTest {

    @Test
    public void format() throws FormatException {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        String value = "23:35:59";
        DateFormatter formatter = new DateFormatter();
        Date date = formatter.format(value, "HH:mm:ss", new Request(), new Response());
        Assert.assertEquals(DateTimeFormat.forPattern("yyyy-MM-dd").print(DateTime.now()) + " 23:35:59", format.print(date.getTime()));

        value = "07-14 23:35:59";
        date = formatter.format(value, "MM-dd HH:mm:ss", new Request(), new Response());
        Assert.assertEquals(DateTimeFormat.forPattern("yyyy").print(DateTime.now()) + "-07-14 23:35:59", format.print(date.getTime()));

        value = "08-14 23:35:59";
        date = formatter.format(value, "MM-dd HH:mm:ss", new Request(), new Response());
        Assert.assertEquals(DateTimeFormat.forPattern("yyyy").print(DateTime.now().minusYears(1)) + "-08-14 23:35:59", format.print(date.getTime()));
    }
}