/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.format;

import com.datatrees.common.conf.DefaultConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.format.impl.CurrencyPaymentFormatImpl;
import org.junit.Test;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 31, 2014 2:51:31 PM
 */
public class CurrencyFormatTest {

    @Test
    public void testCurrencyPaymentFormat() {
        CurrencyPaymentFormatImpl currencyFormatImpl = new CurrencyPaymentFormatImpl();
        Request request = new Request();
        Response response = new Response();
        currencyFormatImpl.setConf(new DefaultConfiguration());
        RequestUtil.setConf(request, new DefaultConfiguration());
        System.out.println(currencyFormatImpl.format(request, response, "支出34 美元", null));
        System.out.println(currencyFormatImpl.format(request, response, "34 人民币", null));
        System.out.println(currencyFormatImpl.format(request, response, "34", null));
        System.out.println(currencyFormatImpl.format(request, response, "存入34欧元", null));
        System.out.println(currencyFormatImpl.format(request, response, "-34 日元", null));
        System.out.println(currencyFormatImpl.format(request, response, "哈哈", null));

    }

}
