/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.bean;

import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.treefinance.toolkit.util.Preconditions;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午5:40:15
 */
public class ExtractRequest extends Request {

    private ExtractRequest() {
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private ExtractorProcessorContext extractContext;
        private Object                    input;

        private Builder() {
        }

        public Builder setExtractContext(ExtractorProcessorContext extractContext) {
            this.extractContext = extractContext;
            return this;
        }

        public Builder setInput(Object input) {
            this.input = input;
            return this;
        }

        @SuppressWarnings("unchecked")
        public ExtractRequest build() {
            Preconditions.notNull("extractContext", extractContext);

            ExtractRequest extractRequest = new ExtractRequest();
            extractRequest.setProcessorContext(extractContext);
            extractRequest.setInput(input);

            if (input instanceof Map) {
                extractRequest.addRequestContext((Map<String, Object>) input);
            }
            extractRequest.addRequestContext(extractContext.getContext());

            return extractRequest;
        }

    }

}
