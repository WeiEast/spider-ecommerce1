/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.format;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.DefaultConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.processor.format.container.NumberMapContainer;
import com.datatrees.crawler.core.processor.format.impl.NumberFormatImpl;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 31, 2014 10:57:34 AM
 */
public class NumberFormatTest {

    @Test
    public void testNumberForamt() {

        Configuration conf = new DefaultConfiguration();

        //        Map<String, String> periodMap = new HashMap<String, String>();
        //        periodMap.put("YEAR", "年前");
        //        periodMap.put("MONTH", "月前");
        //        periodMap.put("DAY", "天前");
        //        periodMap.put("HOUR", "小时前");
        //        periodMap.put("MINUTE", "分钟前");
        //
        //        String result = GsonUtils.toJson(periodMap);
        //        System.out.println(result);
        //        // TimeUnit yearTimeUnit = TimeUnit.valueOf("YEAR");
        //        // System.out.println(yearTimeUnit.name());
        //        // TimeUnit[] units = TimeUnit.values();
        //        // for (TimeUnit timeUnit : units) {
        //        // System.out.println(timeUnit.name());
        //        // }
        //        conf.set(Constants.PEROID_FROMAT_CONFIG, result);

        NumberMapContainer container = NumberMapContainer.get(conf);

        NumberFormatImpl periodFormat = new NumberFormatImpl();
        periodFormat.setConf(conf);

        Request req = new Request();
        Object rs = periodFormat.format(req, null, "- 1500.00", null);
        System.out.println(rs);

        rs = periodFormat.format(req, null, "14 万", null);
        System.out.println(rs);
    }
}
