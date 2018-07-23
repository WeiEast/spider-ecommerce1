/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol;

import com.datatrees.common.conf.Configurable;

/** A retriever of url content. Implemented by protocol extensions. */
public interface Protocol extends Configurable {

    /*
     * Returns the {@link Content} for a fetchlist entry.
     */
    ProtocolOutput getProtocolOutput(String url);

    ProtocolOutput getProtocolOutput(String url, long lastModified);

    ProtocolOutput getProtocolOutput(ProtocolInput input);
}
