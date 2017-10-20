/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.util;

import com.datatrees.crawler.core.processor.common.CalculateUtil;
import org.junit.Test;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月23日 下午1:23:59
 */
public class CalculateUtilTest {

    @Test
    public void testName() throws Exception {
        System.out.println(CalculateUtil.calculate("936.25 --57.61 +468.85999999999996 ", 1));
    }
}