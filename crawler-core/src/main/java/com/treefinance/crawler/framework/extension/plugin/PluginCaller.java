package com.treefinance.crawler.framework.extension.plugin;

import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.plugin.Plugin;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.crawler.core.processor.plugin.PluginWrapper;

/**
 * @author Jerry
 * @since 14:44 15/05/2017
 */
public final class PluginCaller {

    private PluginCaller() {
    }

    public static Object call(AbstractProcessorContext context, AbstractPlugin pluginDesc, PluginParamsSupplier parametersSupplier) throws Exception {
        if (context == null) {
            throw new IllegalArgumentException("Processor context must not be null.");
        }

        PluginWrapper wrapper = context.createPluginWrapper(pluginDesc);

        return call(context, wrapper, parametersSupplier);
    }

    public static Object call(AbstractProcessorContext context, String pluginId, PluginParamsSupplier parametersSupplier) throws Exception {
        if (context == null) {
            throw new IllegalArgumentException("Processor context must not be null.");
        }

        PluginWrapper wrapper = context.createPluginWrapper(pluginId);

        return call(context, wrapper, parametersSupplier);
    }

    public static Object call(AbstractProcessorContext context, PluginWrapper wrapper, PluginParamsSupplier parametersSupplier) throws Exception {
        Plugin plugin = PluginFactory.getPlugin(wrapper, context);

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
    }
}
