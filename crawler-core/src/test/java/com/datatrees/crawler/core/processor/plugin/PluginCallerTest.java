package com.datatrees.crawler.core.processor.plugin;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.exception.PluginException;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import org.junit.Test;

/**
 * @author Jerry
 * @since 11:34 16/05/2017
 */
public class PluginCallerTest extends BaseConfigTest {

    @Test
    public void call() throws Exception {
        Website website = new Website();
        website.setWebsiteName("xxxxx");
        website.setSearchConfig(getSearchConfig("config.xml"));

        SearchProcessorContext context = new SearchProcessorContext(website);
        context.setPluginManager(new PluginManager() {
            @Override
            public AbstractClientPlugin loadPlugin(String jarName, String mainClass) {
                return null;
            }

            @Override
            public PluginWrapper getPlugin(String websiteName, AbstractPlugin pluginDesc) throws PluginException {
                PluginWrapper wrapper = new PluginWrapper();
                File pluginFile = new File("./src/test/resources/plugin/simpleSearch.jar");

                wrapper.setFile(pluginFile);
                wrapper.setPlugin(pluginDesc);

                return wrapper;
            }
        });

        JavaPlugin pluginDesc = new JavaPlugin();
        pluginDesc.setPhase("search");
        pluginDesc.setType("jar");
        pluginDesc.setExtraConfig("ttttttttttt");
        pluginDesc.setMainClass("com.datatrees.crawler.core.plugin.SimpleSearchPlugin");

        String result = (String) PluginCaller.call(context, pluginDesc, (PluginConfSupplier) pluginWrapper -> {
            Map<String, String> params = new LinkedHashMap<>();
            params.put(PluginConstants.PAGE_CONTENT, "page Content");
            params.put(PluginConstants.FIELD, "xx");

            return params;
        });

        // get plugin json result
        Map<String, Object> pluginResultMap = PluginUtil.checkPluginResult(result);
        Object template = pluginResultMap.get(PluginConstants.TEMPLATE);
        System.out.println(template);
    }

}
