/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import java.util.List;

import com.datatrees.common.pipeline.ContextBase;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 7:50:10 PM
 */
public class ProcessorRunner extends ContextBase {

    private List<Processor> processors;

    public ProcessorRunner() {
    }

    public ProcessorRunner(List<Processor> processors) {
        this.processors = processors;
    }

    public List<Processor> getProcessors() {
        return processors;
    }

    public void setProcessors(List<Processor> processors) {
        this.processors = processors;
    }

    public void run(Request request, Response response) throws Exception {
        initProcessor();
        invoke(request, response);
    }

    /**
     *
     */
    private void initProcessor() {
        Preconditions.checkState(CollectionUtils.isNotEmpty(processors), "processors should not be null!");
        for (Processor processor : processors) {
            addValve(processor);
        }
    }

}
