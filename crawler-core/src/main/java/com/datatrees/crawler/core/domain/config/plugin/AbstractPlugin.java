/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.plugin;

import java.io.Serializable;

import com.datatrees.common.util.json.annotation.Description;
import com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin;
import com.datatrees.crawler.core.domain.config.plugin.impl.PythonPlugin;
import com.datatrees.crawler.core.domain.config.plugin.impl.ShellPlugin;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Tag;
import com.datatrees.crawler.core.util.xml.definition.AbstractBeanDefinition;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 11:13:43 AM
 */
@Description(value = "type", keys = {"JAVA", "SHELL", "PYTHON"}, types = {JavaPlugin.class, ShellPlugin.class, PythonPlugin.class})
public abstract class AbstractPlugin extends AbstractBeanDefinition implements Serializable {

    /**
     *
     */
    private static final long        serialVersionUID = -721450264346621071L;

    private              PluginType  type;

    private              PluginPhase phase;

    private              String      extraConfig;

    private              String      fileName;

    @Attr("file-type")
    public PluginType getType() {
        return type;
    }

    @Node("@file-type")
    public void setType(String type) {
        this.type = PluginType.getPluginType(type);
    }

    @Attr("phase")
    public PluginPhase getPhase() {
        return phase;
    }

    @Node("@phase")
    public void setPhase(String phase) {
        this.phase = PluginPhase.getPluginPhase(phase);
    }

    @Tag("extra-config")
    public String getExtraConfig() {
        return extraConfig;
    }

    @Node("extra-config/text()")
    public void setExtraConfig(String extraConfig) {
        this.extraConfig = extraConfig;
    }

    @Attr("file-name")
    public String getFileName() {
        return fileName;
    }

    @Node("@file-name")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
