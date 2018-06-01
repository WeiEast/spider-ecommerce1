/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import java.util.List;

import com.datatrees.common.pipeline.*;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 7:50:10 PM
 */
public class ProcessorRunner {

    private final Pipeline pipeline = new StandardPipeline();

    public ProcessorRunner(List<Processor> processors) {
        if (CollectionUtils.isNotEmpty(processors)) {
            for (Processor processor : processors) {
                pipeline.addValve(processor);
            }
        }
    }

    public void run(Request request, Response response) throws Exception {
        Valve valve = pipeline.getFirst();

        if (valve != null) {
            valve.invoke(request, response);
        }
    }

}
