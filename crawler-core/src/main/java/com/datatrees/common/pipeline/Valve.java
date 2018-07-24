/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.pipeline;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:05:52 PM
 */
public interface Valve extends Invoker {

    /**
     * Return the next Valve in the pipeline containing this Valve, if any.
     */
    Valve getNext();

    /**
     * Set the next Valve in the pipeline containing this Valve.
     * @param valve The new next valve, or <code>null</code> if none
     */
    void setNext(Valve valve);

}
