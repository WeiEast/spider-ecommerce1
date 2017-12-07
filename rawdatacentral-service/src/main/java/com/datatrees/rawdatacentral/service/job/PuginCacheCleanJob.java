package com.datatrees.rawdatacentral.service.job;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.datatrees.rawdatacentral.service.PluginService;
import com.datatrees.rawdatacentral.service.impl.ClassLoaderServiceImpl;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主动清除guava过期缓存
 */
public class PuginCacheCleanJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger("plugin_log");
    @Resource
    private PluginService pluginService;

    @Override
    public void execute(ShardingContext shardingContext) {
        LoadingCache<String, Class> classCache = ClassLoaderServiceImpl.getClassCache();
        LoadingCache<String, ClassLoader> classLoacerCache = ClassLoaderServiceImpl.getClassLoacerCache();
        if (null == classCache || null == classLoacerCache) {
            logger.info("plugin class is not init");
            return;
        }
        classCache.asMap().keySet().forEach(key -> {
            String[] split = key.split(":");
            String pluginName = split[0];
            String classVersion = split[1];
            try {
                String pluginVersion = pluginService.getPluginVersionFromCache(pluginName);
                if (!StringUtils.equals(pluginVersion, classVersion)) {
                    classCache.invalidate(key);
                    logger.info("remove class cache, key:{},pluginVersion:{}", key, pluginVersion);
                }
            } catch (ExecutionException e) {
                logger.error("getPluginVersionFromCache error,pluginName:{}", pluginName, e);
            }
        });

        classLoacerCache.asMap().keySet().forEach(key -> {
            String[] split = key.split(":");
            String pluginName = split[0];
            String classLoaderVersion = split[1];
            try {
                String pluginVersion = pluginService.getPluginVersionFromCache(pluginName);
                if (!StringUtils.equals(pluginVersion, classLoaderVersion)) {
                    classLoacerCache.invalidate(key);
                    logger.info("remove classLoader cache, key:{},pluginVersion:{}", key, pluginVersion);
                }
            } catch (ExecutionException e) {
                logger.error("getPluginVersionFromCache error,pluginName:{}", pluginName, e);
            }
        });

    }
}
