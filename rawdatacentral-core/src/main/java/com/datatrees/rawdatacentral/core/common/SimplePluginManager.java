package com.datatrees.rawdatacentral.core.common;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.CacheUtil;
import com.datatrees.common.util.locker.MutexProvider;
import com.datatrees.common.util.locker.MutexProvider.Mutex;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.common.exception.PluginExeception;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import com.datatrees.crawler.core.processor.plugin.PluginWrapper;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午12:29:10
 */

//@Service
public class SimplePluginManager extends PluginManager {

    private static final Logger logger = LoggerFactory.getLogger(SimplePluginManager.class);

    private String pluginPath = PropertiesConfiguration.getInstance().get("plugin.store.path", "plugin");

    private String pluginCacheKeyPrefix = "plugin_local_prefix_";

    private String pluginBackupSuffix = "_version2";


    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.vt.core.processer.plugin.PluginManager#getPlugin(java.lang.String,
     * com.datatrees.vt.core.domain.config.plugin.AbstractPlugin)
     */
    @Override
    public PluginWrapper getPlugin(String websiteName, AbstractPlugin pluginDesc) throws PluginExeception {
        String key = websiteName + "/" + pluginDesc.getId() + "." + pluginDesc.getType().getValue();
        String cecheKyeString = pluginCacheKeyPrefix + key;
        String pathName = (String) CacheUtil.getInstance().getObject(cecheKyeString);
        File plugin = null;
        if (null == pathName) {
            pathName = pluginPath + "/" + key;
            Mutex mutex = MutexProvider.getMutex(pathName);
            synchronized (mutex) {
                String cachePathName = (String) CacheUtil.getInstance().getObject(cecheKyeString);
                if (null == cachePathName) {// need download
//                    cachePathName = (String) CacheUtil.getInstance().getNoExpiredObject(cecheKyeString);
//                    if (cachePathName != null) {// is expired
//                        if (cachePathName.endsWith(pluginBackupSuffix)) {
//                            pathName = cachePathName.split(pluginBackupSuffix)[0];
//                        } else {
//                            pathName = pathName + pluginBackupSuffix;
//                        }
//                    }

                    plugin = new File((String) pathName);
                    // get plugin file
                    // VTBossService.INSTANCE.getPluginService().getPlugin(websiteName,
                    // pluginDesc.getId() + "." + pluginDesc.getType().getValue(), plugin);
                    // put path in cache
                    CacheUtil.getInstance().insertObject(cecheKyeString, pathName);
                    logger.info("fetch and store plugin success with key:" + cecheKyeString + "," + "path:" + pathName);
                } else {
                    logger.debug("get reload plugin with key:" + cecheKyeString + "," + "path:" + cachePathName);
                    plugin = new File((String) cachePathName);
                }
            }
        } else {// cached
            logger.debug("get cached plugin with key:" + cecheKyeString + "," + "path:" + pathName);
            plugin = new File((String) pathName);
        }
        return new PluginWrapper(plugin, pluginDesc);
    }

}
