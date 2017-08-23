/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.plugin.impl;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 12, 2014 3:33:36 PM
 */
@Path(".[@file-type='sh']")
@Tag("plugin")
public class ShellPlugin extends AbstractPlugin {

    /**
     *
     */
    private static final long serialVersionUID = -2600704916838043696L;

}
