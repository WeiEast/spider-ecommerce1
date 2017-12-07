package com.datatrees.rawdatacentral.service.plugin;

import javax.annotation.Resource;
import java.io.File;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.common.exception.PluginException;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.processor.plugin.PluginWrapper;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/7/6.
 */
@Service
public class RedisPluginManager extends PluginManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisPluginManager.class);
    @Resource
    private ClassLoaderService classLoaderService;

    @Override
    public AbstractClientPlugin loadPlugin(String jarName, String mainClass,
            Long taskId) throws PluginException, IllegalAccessException, InstantiationException {
        try {
            Class pluginClass = classLoaderService.loadPlugin(jarName, mainClass, taskId);
            if (null == pluginClass) {
                logger.error("plugin class load fail,jarName={},mainClass={}", jarName, mainClass);
                throw new PluginException(TemplateUtils.format("plugin class load fail,jarName={},mainClass={}", jarName, mainClass));
            }
            if (!AbstractClientPlugin.class.isAssignableFrom(pluginClass)) {
                logger.error("plugin class not impl com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin,jarName={},mainClass={}",
                        jarName, mainClass);
                throw new PluginException("plugin class not impl com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin");
            }
            return (AbstractClientPlugin) pluginClass.newInstance();
        } catch (Throwable e) {
            logger.error("loadPlugin error,jarName={},mainClass={}", jarName, mainClass, e);
            throw new PluginException(e);
        }
    }

    @Override
    public PluginWrapper getPlugin(String websiteName, AbstractPlugin pluginDesc) {
        PluginWrapper wrapper = new PluginWrapper(new File("不再用这个加载,请使用com.datatrees.rawdatacentral.service.plugin.RedisPluginManager.loadPlugin"),
                pluginDesc);
        wrapper.setForceReload(true);
        return wrapper;
    }
}
