/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin;

import javax.annotation.Nonnull;
import java.util.Objects;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.plugin.impl.CommandPlugin;
import com.datatrees.crawler.core.processor.plugin.impl.JavaPlugin;
import com.treefinance.crawler.framework.extension.plugin.ProcessContextHolder;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 7:29:26 PM
 */
public final class PluginFactory {

    private PluginFactory() {
    }

    /**
     * @see ProcessContextHolder#getProcessorContext()
     */
    @Deprecated
    public static AbstractProcessorContext getProcessorContext() {
        return ProcessContextHolder.getProcessorContext();
    }

    public static Plugin getPlugin(@Nonnull final AbstractPlugin metadata, @Nonnull final AbstractProcessorContext context) {
        Objects.requireNonNull(metadata);
        Objects.requireNonNull(context);

        if (metadata instanceof com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin) {
            return new JavaPlugin((com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin) metadata, context);
        }

        return new CommandPlugin(metadata, context);
    }

}
