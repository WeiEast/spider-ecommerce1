/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.format;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.datatrees.common.conf.DefaultConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.format.impl.DateFormatImpl;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 31, 2014 2:51:31 PM
 */
public class DateFormatTest {

    @Test
    public void testDateFormat() {
        String data = "2013-11-1 12:0";
        DateFormatImpl dateFormatImpl = new DateFormatImpl();
        Request request = new Request().setInput(data);
        dateFormatImpl.setConf(new DefaultConfiguration());
        RequestUtil.setConf(request, new DefaultConfiguration());
        Date obj = (Date) dateFormatImpl.format(request, null, data, "yy-MM-dd");
        //        obj.setMonth(obj.getMonth()-1);
        System.out.println(obj.getMonth());
        System.out.println(obj);
    }

    @Test
    public void testPF() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormatImpl dateFormatImpl = new DateFormatImpl();
        Request req = new Request();

        Date date = (Date) dateFormatImpl.format(req,null, "2015-08-31T06:06:50Z", "yyyy-MM-dd'T'HH:mm:ss");
        System.out.println(date);


    }

}
