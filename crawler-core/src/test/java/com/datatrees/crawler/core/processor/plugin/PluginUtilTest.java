package com.datatrees.crawler.core.processor.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.classfile.ClassLoaderManager;
import com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin;
import org.junit.Test;

/**
 * @author Jerry
 * @since 00:05 22/05/2017
 */
public class PluginUtilTest {

    @Test
    public void mapPluginInput() throws Exception {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        Map<String, String> params = new HashMap<String, String>();
        params.put("x", "xxxxxxx");
        params.put("dd", "ddddd");
        System.out.println(PluginUtil.mapPluginInput(params));

        data.add(params);

        params = new HashMap<String, String>();
        params.put("x", "xxxxxxx");
        params.put("dd", "ddddd");
        data.add(params);
        System.out.println(GsonUtils.toJson(data));

    }

    @Test
    public void loadPlugin() throws Exception {
        File parent = new File("/Users/Jerry/.m2/repository/com/datatrees/rawdatacentral/rawdatacentral-plugin/3.0.7-SNAPSHOT/rawdatacentral-plugin-3.0.7-SNAPSHOT.jar");
        ClassLoader classLoader = ClassLoaderManager.findClassLoader(parent, getClass().getClassLoader(), false);

        File file = new File("/Users/Jerry/Studio/projects/work/crawler/saas/rawdata-plugin/target/plugin/operator-hi-10086.jar");

        JavaPlugin javaPlugin = new JavaPlugin();
        javaPlugin.setMainClass("com.datatrees.crawler.plugin.main.LoginMainShop");

        PluginWrapper pluginWrapper = new PluginWrapper();
        pluginWrapper.setFile(file);
        pluginWrapper.setForceReload(true);
        pluginWrapper.setPlugin(javaPlugin);

        AbstractClientPlugin plugin1 = PluginUtil.loadPlugin(pluginWrapper, classLoader);

        pluginWrapper.setForceReload(false);

        AbstractClientPlugin plugin2 = PluginUtil.loadPlugin(pluginWrapper, classLoader);

    }
}