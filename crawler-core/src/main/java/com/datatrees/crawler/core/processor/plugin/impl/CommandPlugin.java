/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin.impl;

import java.io.File;

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.plugin.PluginType;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.exception.PluginInvokeException;
import com.datatrees.crawler.core.processor.plugin.Plugin;
import com.datatrees.crawler.core.util.CommandLineExecutor;
import com.treefinance.crawler.framework.extension.manager.WrappedExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 1:21:47 PM
 */
public class CommandPlugin extends Plugin {

    private static final Logger logger = LoggerFactory.getLogger(CommandPlugin.class);

    public CommandPlugin(AbstractPlugin metadata, AbstractProcessorContext context) {
        super(metadata, context);
    }

    @Override
    protected Object invokePlugin(AbstractPlugin metadata, String args, Request request) throws Exception {
        WrappedExtension<File> extension = getContext().loadExtension(metadata, File.class);

        File file = extension.getExtension();
        String path = file.getAbsolutePath();
        logger.info("Command: " + path + ", with args: " + args);

        return executorCommand(path, args, metadata);
    }

    private String executorCommand(String path, String args, AbstractPlugin metadata) throws PluginInvokeException {
        CommandLineExecutor executor = null;
        try {
            PluginType type = metadata.getType();
            String shell = "bash";
            if (type == PluginType.PYTHON) {
                shell = "python";
            }
            executor = new CommandLineExecutor(shell, path);
            executor.setInput(args);
            executor.execute();
            return executor.getStdOut();
        } catch (Exception e) {
            throw new PluginInvokeException("Error executing command line", e);
        } finally {
            if (executor != null) executor.destroy();
        }
    }
}
