package com.treefinance.crawler.framework.extension.plugin;

import java.util.Map;
import java.util.Objects;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.plugin.Plugin;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.treefinance.crawler.framework.exception.PluginException;

/**
 * @author Jerry
 * @since 14:44 15/05/2017
 */
public final class PluginCaller {

    private PluginCaller() {
    }

    public static Object call(String pluginId, AbstractProcessorContext context, PluginParamsSupplier parametersSupplier) {
        Objects.requireNonNull(context);

        AbstractPlugin pluginDesc = context.getPluginDescByID(pluginId);

        return call(pluginDesc, context, parametersSupplier);
    }

    public static Object call(AbstractPlugin pluginMetadata, AbstractProcessorContext context, PluginParamsSupplier parametersSupplier) {
        Plugin plugin = PluginFactory.getPlugin(pluginMetadata, context);

        try {
            Request req = new Request();

            if (parametersSupplier != null) {
                Map<String, String> parameters = parametersSupplier.get();
                if (parameters != null) {
                    RequestUtil.setPluginRuntimeConf(req, parameters);
                }
            }

            Response resp = new Response();

            plugin.invoke(req, resp);

            return resp.getOutPut();
        } catch (Exception e) {
            throw new PluginException("Error calling plugin! >>> " + pluginMetadata, e);
        }
    }
}
