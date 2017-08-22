package com.datatrees.crawler.core.processor.plugin;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.plugin.impl.JavaPlugin;
import java.util.Map;

/**
 * @author Jerry
 * @since 14:44 15/05/2017
 */
public final class PluginCaller {

  private PluginCaller() {
  }

  public static Object call(AbstractProcessorContext context, AbstractPlugin pluginDesc,
      PluginConfSupplier parametersSupplier)
      throws Exception {
    return call(context, pluginDesc,
        (PluginCallable<Object>) plugin -> invokePlugin(plugin, parametersSupplier));
  }


  public static Object call(AbstractProcessorContext context, String pluginId,
      PluginConfSupplier parametersSupplier)
      throws Exception {
    return call(context, pluginId,
        (PluginCallable<Object>) plugin -> invokePlugin(plugin, parametersSupplier));
  }

  private static Object invokePlugin(Plugin plugin, PluginConfSupplier parametersSupplier)
      throws Exception {
    Request req = new Request();

    if (parametersSupplier != null) {
      Map<String, String> parameters = parametersSupplier.get(plugin.getPluginDesc());
      if (parameters != null) {
        RequestUtil.setPluginRuntimeConf(req, parameters);
      }
    }

    Response resp = new Response();

    plugin.invoke(req, resp);

    return resp.getOutPut();
  }

  public static <R> R call(AbstractProcessorContext context, AbstractPlugin pluginDesc,
      PluginCallable<R> callable)
      throws Exception {
    if (context == null) {
      throw new IllegalArgumentException("Processor context must not be null.");
    }

    PluginWrapper wrapper = context.createPluginWrapper(pluginDesc);

    return call(context, wrapper, callable);
  }

  public static <R> R call(AbstractProcessorContext context, String pluginId,
      PluginCallable<R> callable)
      throws Exception {
    if (context == null) {
      throw new IllegalArgumentException("Processor context must not be null.");
    }

    PluginWrapper wrapper = context.createPluginWrapper(pluginId);

    return call(context, wrapper, callable);
  }

  public static <R> R call(AbstractProcessorContext context, PluginWrapper wrapper,
      PluginCallable<R> callable) throws Exception {
    if (callable == null) {
      throw new NullPointerException("Plugin callable must not be null.");
    }

    Plugin plugin = PluginFactory.getPlugin(wrapper);

    if (plugin instanceof JavaPlugin) {
      PluginContext.setProcessorContext(context);
    }

    try {
      return callable.call(plugin);
    } finally {
      PluginContext.clearProcessorContext();
    }
  }
}
