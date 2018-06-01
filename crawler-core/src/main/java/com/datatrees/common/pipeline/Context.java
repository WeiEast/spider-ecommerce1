/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.pipeline;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:09:36 PM
 */
public interface Context {
    /**
     * Obtains attribute with the given name.
     * 
     * @param id the attribute name.
     * @return attribute value, or <code>null</code> if not set.
     */
    Object getAttribute(String id);

    /**
     * Sets value of the attribute with the given name.
     * 
     * @param id the attribute name.
     * @param obj the attribute value.
     */
    void setAttribute(String id, Object obj);

    /**
     * Removes attribute with the given name from the context.
     * 
     * @param id the attribute name.
     * @return attribute value, or <code>null</code> if not set.
     */
    Object removeAttribute(String id);

}
