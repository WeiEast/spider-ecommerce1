/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.bean;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.RequestUtil;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午5:40:15
 */
public class ExtractorRepuest extends Request {

    public static ExtractorRepuest build() {
        return new ExtractorRepuest();
    }

    public AbstractProcessorContext getProcessorContext() {
        return RequestUtil.getProcessorContext(this);
    }

    public ExtractorRepuest setProcessorContext(AbstractProcessorContext context) {
        RequestUtil.setProcessorContext(this, context);
        return this;
    }

    public Map<String, Object> getContext() {
        Map<String, Object> context = RequestUtil.getContext(this);
        if (context == null) {
            context = new HashMap<String, Object>();
            RequestUtil.setContext(this, context);
        }
        return context;
    }

    public ExtractorRepuest contextInit() { //
        if (this.getInput() instanceof Map) {
            this.getContext().putAll((Map) this.getInput());
        }
        this.getContext().putAll(this.getProcessorContext().getContext());
        return this;
    }

}
