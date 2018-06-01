/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol;

import java.net.URL;

/**
 * This class holds the rules which were parsed from a robots.txt file, and can test paths against
 * those rules.
 */
public interface RobotRules {
    /**
     * Get expire time
     */
    public long getExpireTime();

    /**
     * Get Crawl-Delay, in milliseconds. This returns -1 if not set.
     */
    public long getCrawlDelay();

    /**
     * Returns <code>false</code> if the <code>robots.txt</code> file prohibits us from accessing
     * the given <code>url</code>, or <code>true</code> otherwise.
     */
    public boolean isAllowed(URL url);

}
