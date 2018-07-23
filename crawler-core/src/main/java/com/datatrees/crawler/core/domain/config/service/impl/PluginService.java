package com.datatrees.crawler.core.domain.config.service.impl;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

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
