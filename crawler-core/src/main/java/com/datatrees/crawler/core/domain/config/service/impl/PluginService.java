package com.datatrees.crawler.core.domain.config.service.impl;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Path;
import com.treefinance.crawler.framework.config.annotation.Tag;

@Path(".[@type='plugin']")
@Tag("service")
public class PluginService extends AbstractService {

    /**
     *
     */
    private static final long           serialVersionUID = 7775621324577608743L;

    private              AbstractPlugin plugin;

    @Attr(value = "plugin-ref", referenced = true)
    public AbstractPlugin getPlugin() {
        return plugin;
    }

    @Node(value = "@plugin-ref", referenced = true)
    public void setPlugin(AbstractPlugin plugin) {
        this.plugin = plugin;
    }
}
