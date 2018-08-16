package com.treefinance.crawler.framework.extension.plugin;

import javax.annotation.Nonnull;
import java.util.Objects;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderRequestFactory;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.function.SpiderResponseFactory;
import com.treefinance.crawler.framework.exception.PluginException;
import com.treefinance.crawler.framework.extension.plugin.impl.CommandPluginHandler;
import com.treefinance.crawler.framework.extension.plugin.impl.JavaPluginHandler;

/**
 * @author Jerry
 * @since 14:44 15/05/2017
 */
public final class PluginCaller {

    private PluginCaller() {
    }

    public static Object call(String pluginId, AbstractProcessorContext context, PluginParamsSupplier parametersSupplier) {
        Objects.requireNonNull(context);

        AbstractPlugin pluginDesc = context.getPluginMetadataById(pluginId);

        return call(pluginDesc, context, parametersSupplier);
    }

    public static Object call(AbstractPlugin pluginMetadata, AbstractProcessorContext context, PluginParamsSupplier parametersSupplier) {
        PluginHandler pluginHandler = getPluginHandler(pluginMetadata, context);

        try {
            SpiderRequest request = SpiderRequestFactory.make();
            // TODO: 2018/7/24 field scope shared
            if (parametersSupplier != null) {
                request.setInput(parametersSupplier.get());
            }

            SpiderResponse response = SpiderResponseFactory.make();

            pluginHandler.invoke(request, response);

            return response.getOutPut();
        } catch (Exception e) {
            throw new PluginException("Error calling plugin! >>> " + pluginMetadata, e);
        }
    }

    public static PluginHandler getPluginHandler(@Nonnull final AbstractPlugin metadata, @Nonnull final AbstractProcessorContext context) {
        Objects.requireNonNull(metadata);
        Objects.requireNonNull(context);

        if (metadata instanceof JavaPlugin) {
            return new JavaPluginHandler((JavaPlugin) metadata, context);
        }

        return new CommandPluginHandler(metadata, context);
    }
}
