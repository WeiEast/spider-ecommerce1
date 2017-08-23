/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 11:16:45 AM
 */
public class ProcessorFactoryTest {

    @Test
    public void testProcessorFactoryWithServiceImpl() {
        AbstractService service = null;
        try {
            ServiceBase base = ProcessorFactory.getService(service);
            System.out.println(base.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
