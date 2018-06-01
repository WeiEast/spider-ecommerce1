/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol;

import com.datatrees.common.conf.Configurable;

/** A retriever of url content. Implemented by protocol extensions. */
public interface Protocol extends Configurable {

    /** The name of the extension point. */
    public final static String X_POINT_ID = Protocol.class.getName();

    /**
     * Property name. If in the current configuration this property is set to true, protocol
     * implementations should handle "politeness" limits internally. If this is set to false, it is
     * assumed that these limits are enforced elsewhere, and protocol implementations should not
     * enforce them internally.
     */
    public final static String CHECK_BLOCKING = "protocol.check.blocking";

    /**
     * Property name. If in the current configuration this property is set to true, protocol
     * implementations should handle robot exclusion rules internally. If this is set to false, it
     * is assumed that these limits are enforced elsewhere, and protocol implementations should not
     * enforce them internally.
     */
    public final static String CHECK_ROBOTS = "protocol.check.robots";

    /*
     * Returns the {@link Content} for a fetchlist entry.
     */
    ProtocolOutput getProtocolOutput(String url);

    ProtocolOutput getProtocolOutput(String url, long lastModified);

    ProtocolOutput getProtocolOutput(ProtocolInput input);
}
