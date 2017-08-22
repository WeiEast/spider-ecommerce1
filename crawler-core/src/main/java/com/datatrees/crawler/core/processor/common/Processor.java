/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.common;

import com.datatrees.common.conf.Configurable;
import com.datatrees.common.conf.Configuration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.pipeline.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 1:27:06 PM
 */
public abstract class Processor extends ValveBase implements Configurable {
    protected Configuration conf;

    @Override
    public void invoke(Request request, Response response) throws Exception {
        preProcess(request, response);
        process(request, response);
        postProcess(request, response);
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }

    protected void preProcess(Request request, Response response) throws Exception {
    }

    public abstract void process(Request request, Response response) throws Exception;

    protected void postProcess(Request request, Response response) throws Exception {
    }

}
