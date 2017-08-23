/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.filter;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 7:24:40 PM
 */
public abstract class RegexRule {

    private final boolean sign;

    /**
     * Constructs a new regular expression rule.
     *
     * @param sign specifies if this rule must filter-in or filter-out. A <code>true</code> value
     *        means that any url matching this rule must be accepted, a <code>false</code> value
     *        means that any url matching this rule must be rejected.
     * @param regex is the regular expression used for matching (see {@link #match(String)} method).
     */
    protected RegexRule(boolean sign, String regex) {
        this.sign = sign;
    }

    /**
     * Return if this rule is used for filtering-in or out.
     *
     * @return <code>true</code> if any url matching this rule must be accepted, otherwise
     *         <code>false</code>.
     */
    protected boolean accept() {
        return sign;
    }

    /**
     * Checks if a url matches this rule.
     *
     * @param url is the url to check.
     * @return <code>true</code> if the specified url matches this rule, otherwise
     *         <code>false</code>.
     */
    protected abstract boolean match(String url);

}
