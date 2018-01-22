/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.crawler.framework.extension;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Objects;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.treefinance.crawler.framework.exception.ExtensionException;
import com.treefinance.crawler.framework.exception.UnsupportedExtensionException;
import com.treefinance.crawler.framework.extension.plugin.ExpansionPlugin;
import com.treefinance.crawler.framework.extension.spider.PageProcessor;
import com.treefinance.crawler.framework.extension.spider.Spider;

/**
 * @author Jerry
 * @since 15:11 15/01/2018
 */
public final class ExtensionFactory {

    private ExtensionFactory() {
    }

    public static ExpansionPlugin getPlugin(@Nonnull final PluginFile pluginFile) {
        return getPlugin(pluginFile, null);
    }

    public static ExpansionPlugin getPlugin(@Nonnull final PluginFile pluginFile, ClassLoader parent) {
        checkExtension(pluginFile);

        File file = pluginFile.getFile();

        String mainClass = ((JavaPlugin) pluginFile.getMetadata()).getMainClass();

        return ExtensionLoader.load(file, mainClass, false, parent, ExpansionPlugin.class);
    }

    public static Spider getSpider(@Nonnull final PluginFile pluginFile, final SearchProcessorContext context, final PageProcessor pageProcessor) {
        return getSpider(pluginFile, context, pageProcessor, null);
    }

    public static Spider getSpider(@Nonnull final PluginFile pluginFile, final SearchProcessorContext context, final PageProcessor pageProcessor, final Interrupter interrupter) {
        checkExtension(pluginFile);

        File file = pluginFile.getFile();
        String mainClass = ((JavaPlugin) pluginFile.getMetadata()).getMainClass();

        Spider spider = ExtensionLoader.load(file, mainClass, false, null, Spider.class);

        spider.setContext(context);
        if (context != null) {
            spider.setProxyManager(context.getProxyManager());
        }
        spider.setPageProcessor(pageProcessor);
        spider.setInterrupter(interrupter);

        return spider;
    }

    private static void checkExtension(PluginFile pluginFile) {
        Objects.requireNonNull(pluginFile);
        AbstractPlugin plugin = pluginFile.getMetadata();
        if (!(plugin instanceof JavaPlugin)) {
            throw new UnsupportedExtensionException("Unsupported extension type - " + plugin.getClass());
        }

        File file = pluginFile.getFile();
        if (file == null || !file.exists()) {
            throw new ExtensionException("Extension file does not exist.");
        }
    }
}
