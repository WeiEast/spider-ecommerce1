/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.pipeline;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 7:50:10 PM
 */
public abstract class ProcessPipeline {

    protected final Logger   logger   = LoggerFactory.getLogger(getClass());

    private         Pipeline pipeline = new StandardPipeline();

    public ProcessPipeline() {
    }

    public ProcessPipeline(List<Valve> valves) {
        Objects.requireNonNull(valves);
        for (Valve processor : valves) {
            addValve(processor);
        }
    }

    public void addValve(Valve valve) {
        if (valve != null) this.pipeline.addValve(valve);
    }

    public void invoke(@Nonnull Request request, @Nonnull Response response) throws InvokeException, ResultEmptyException {
        Valve valve = pipeline.getFirst();

        if (valve != null) {
            valve.invoke(request, response);
        }
    }

    public void invokeQuietly(@Nonnull Request request, @Nonnull Response response) throws ResultEmptyException {
        try {
            invoke(request, response);
        } catch (ResultEmptyException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error invoking processor pipeline!", e);
        }
    }

    public boolean isPrepared() {
        return pipeline.getFirst() != null;
    }
}
