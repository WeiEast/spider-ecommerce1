/**
 * This document and its contents are protected by copyright 2005 and owned by Treefinance.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月9日 下午4:53:54
 */
@Tag("operation")
@Path(".[@type='trim']")
public class TrimOperation extends AbstractOperation {

    /**
     *
     */
    private static final long serialVersionUID = 3788053557412842703L;

}
