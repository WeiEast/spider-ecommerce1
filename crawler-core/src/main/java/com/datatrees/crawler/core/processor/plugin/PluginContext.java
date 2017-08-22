package com.datatrees.crawler.core.processor.plugin;

import com.datatrees.crawler.core.processor.AbstractProcessorContext;

/**
 * @author Jerry
 * @since 16:31 15/05/2017
 */
public class PluginContext {
  private static final ThreadLocal<AbstractProcessorContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

  public static void setProcessorContext(AbstractProcessorContext context) {
    CONTEXT_THREAD_LOCAL.set(context);
  }

  public static AbstractProcessorContext getProcessorContext() {
    return CONTEXT_THREAD_LOCAL.get();
  }

  public static void clearProcessorContext() {
    CONTEXT_THREAD_LOCAL.remove();
  }
}
