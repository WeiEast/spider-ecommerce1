/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2014
 */

package com.datatrees.common.protocol;

import com.datatrees.common.protocol.util.LangUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 24, 2014 1:51:25 PM
 */
public class NameValuePair {

    /**
     * Name.
     */
    private String name  = null;

    /**
     * Value.
     */
    private String value = null;

    // ----------------------------------------------------- Instance Variables

    /**
     * Default constructor.
     */
    public NameValuePair() {
        this(null, null);
    }

    /**
     * Constructor.
     * @param name  The name.
     * @param value The value.
     */
    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    // ------------------------------------------------------------- Properties

    /**
     * Return the name.
     * @return String name The name
     * @see #setName(String)
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * @param name The new name
     * @see #getName()
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the current value.
     * @return String value The current value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value.
     * @param value The new value.
     */
    public void setValue(String value) {
        this.value = value;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Get a String representation of this pair.
     * @return A string representation.
     */
    public String toString() {
        return ("name=" + name + ", " + "value=" + value);
    }

    public boolean equals(final Object object) {
        if (object == null) return false;
        if (this == object) return true;
        if (object instanceof NameValuePair) {
            NameValuePair that = (NameValuePair) object;
            return LangUtils.equals(this.name, that.name) && LangUtils.equals(this.value, that.value);
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = LangUtils.HASH_SEED;
        hash = LangUtils.hashCode(hash, this.name);
        hash = LangUtils.hashCode(hash, this.value);
        return hash;
    }
}
