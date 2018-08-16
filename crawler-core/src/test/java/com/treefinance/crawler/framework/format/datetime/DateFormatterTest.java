package com.treefinance.crawler.framework.format.datetime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.treefinance.crawler.framework.exception.FormatException;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderRequestFactory;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.function.SpiderResponseFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParserBucket;
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
        SpiderRequest request = SpiderRequestFactory.make();
        SpiderResponse response = SpiderResponseFactory.make();
        Date date = formatter.format(value, "HH:mm:ss", request, response);
        Assert.assertEquals(DateTimeFormat.forPattern("yyyy-MM-dd").print(DateTime.now()) + " 23:35:59", format.print(date.getTime()));

        value = "07-14 23:35:59";
        date = formatter.format(value, "MM-dd HH:mm:ss", request, response);
        Assert.assertEquals(DateTimeFormat.forPattern("yyyy").print(DateTime.now()) + "-07-14 23:35:59", format.print(date.getTime()));

        value = "08-14 23:35:59";
        date = formatter.format(value, "MM-dd HH:mm:ss", request, response);
        Assert.assertEquals(DateTimeFormat.forPattern("yyyy").print(DateTime.now().minusYears(1)) + "-08-14 23:35:59", format.print(date.getTime()));

        value = "2018-08-14 23:35:59";
        date = formatter.format(value, "yyyy-MM-dd HH:mm", request, response);
        Assert.assertEquals("2018-08-14 23:35", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").print(date.getTime()));

        date = formatter.format(value, "yyyy-MM-dd", request, response);
        Assert.assertEquals("2018-08-14", DateTimeFormat.forPattern("yyyy-MM-dd").print(date.getTime()));

        date = formatter.format(value, "yyyy-MM", request, response);
        Assert.assertEquals("2018-08", DateTimeFormat.forPattern("yyyy-MM").print(date.getTime()));

        date = formatter.format(value, "yyyy", request, response);
        Assert.assertEquals("2018", DateTimeFormat.forPattern("yyyy").print(date.getTime()));

        try {
            formatter.format(value, "MM-dd HH:mm:ss", request, response);
        } catch (FormatException e) {
            Assert.assertTrue(true);
        }

        try {
            formatter.format(value, "MM-dd", request, response);
        } catch (FormatException e) {
            Assert.assertTrue(true);
        }

        try {
            formatter.format(value, "HH:mm:ss", request, response);
        } catch (FormatException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testSimpleDateFormat() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        format.setLenient(true);
        System.out.println(format.parse("2017/08/23"));
    }

    @Test
    public void testDateTimeFormat() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM");
        String text = "2017/08/23";
        DateTime dateTime = null;
        try {
            dateTime = formatter.parseDateTime(text);
        } catch (IllegalArgumentException e) {
            DateTimeParserBucket bucket = new DateTimeParserBucket(0, null, formatter.getLocale(), formatter.getPivotYear(), formatter.getDefaultYear());
            int errorPos = formatter.getParser().parseInto(bucket, text, 0);
            if (errorPos > 0 && errorPos < text.length()) {
                dateTime = formatter.parseDateTime(text.substring(0, errorPos));
            }
        }
        System.out.println(dateTime);
    }
}