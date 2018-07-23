/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor;

import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 6:52:53 PM
 */
public class FieldExtractResult {

    private FieldExtractor extractor;

    private Object         result;

    public FieldExtractResult() {
        super();
    }

    public FieldExtractResult(FieldExtractor extractor, Object result) {
        super();
        this.extractor = extractor;
        this.result = result;
    }

    public FieldExtractResult(Object result) {
        super();
        this.result = result;
    }

    public FieldExtractor getExtractor() {
        return extractor;
    }

    public void setExtractor(FieldExtractor extractor) {
        this.extractor = extractor;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isNotEmpty() {
        if (result instanceof String) {
            return StringUtils.isNotEmpty((String) result);
        }
        return result != null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FieldExtractResult [extractor=" + extractor + ", result=" + result + "]";
    }

}
