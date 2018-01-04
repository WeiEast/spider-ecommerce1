package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.api.ConfigServiceApi;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.api.service.CommonPluginService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ClassLoaderUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.exception.CommonException;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.rawdatacentral.service.PluginService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import com.google.common.cache.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/7/14.
 */
@Service
public class ClassLoaderServiceImpl implements ClassLoaderService, InitializingBean {

    private static final Logger logger                   = LoggerFactory.getLogger("plugin_log");
    private static final String OPERATOR_PLUGIN_FILENAME = "rawdatacentral-plugin-operator.jar";
    private static LoadingCache<String, ClassLoader> classLoacerCache;
    private static LoadingCache<String, Class>       classCache;
    @Resource
    private        PluginService                     pluginService;
    @Resource
    private        RedisService                      redisService;
    @Resource
    private        WebsiteOperatorService            websiteOperatorService;
    @Resource
    private        ConfigServiceApi                  configServiceApi;

    public static LoadingCache<String, ClassLoader> getClassLoacerCache() {
        return classLoacerCache;
    }

    public static LoadingCache<String, Class> getClassCache() {
        return classCache;
    }

    @Override
    public Class loadPlugin(String pluginName, String className, Long taskId) {
        CheckUtils.checkNotBlank(pluginName, "pluginName is blank");
        CheckUtils.checkNotBlank(className, "className is blank");
        try {
            String version = pluginService.getPluginVersionFromCache(pluginName);
            Class mainClass = getClassFromCache(pluginName, version, className, taskId);
            return mainClass;
        } catch (Throwable e) {
            logger.error("loadPlugin error pluginName={},className={}", pluginName, className, e);
            throw new RuntimeException(TemplateUtils.format("loadPlugin error pluginName={},className={}", pluginName, className));
        }
    }

    @Override
    public OperatorPluginService getOperatorPluginService(String websiteName, Long taskId) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(websiteName);
        if (null == websiteOperator) {
            logger.error("not found config,websiteName={}", websiteName);
            throw new CommonException("not found config,websiteName=" + websiteName);
        }
        String mainLoginClass = websiteOperator.getPluginClass();
        String pluginFileName = redisService.getString(RedisKeyPrefixEnum.WEBSITE_PLUGIN_FILE_NAME.getRedisKey(websiteName));
        if (StringUtils.isNoneBlank(pluginFileName)) {
            logger.info("websiteName={},独立映射到了插件pluginFileName={}", websiteName, pluginFileName);
        } else {
            pluginFileName = OPERATOR_PLUGIN_FILENAME;
        }
        try {
            Class loginClass = loadPlugin(pluginFileName, mainLoginClass, taskId);
            if (!OperatorPluginService.class.isAssignableFrom(loginClass)) {
                throw new RuntimeException("mainLoginClass not impl " + OperatorPluginService.class.getName());
            }
            return (OperatorPluginService) loginClass.newInstance();
        } catch (Throwable e) {
            logger.error("getOperatorService error websiteName={}", websiteName, e);
            throw new RuntimeException("getOperatorPluginService error websiteName=" + websiteName, e);
        }
    }

    @Override
    public CommonPluginService getCommonPluginService(String pluginName, String className, Long taskId) {
        try {
            Class loginClass = loadPlugin(pluginName, className, taskId);
            if (!CommonPluginService.class.isAssignableFrom(loginClass)) {
                throw new RuntimeException("mainLoginClass not impl " + CommonPluginService.class.getName());
            }
            return (CommonPluginService) loginClass.newInstance();
        } catch (Throwable e) {
            logger.error("get common plugin service error pluginName={},className={},taskId={}", pluginName, className, taskId, e);
            throw new RuntimeException(
                    TemplateUtils.format("get common plugin service error pluginName={},className={},taskId={}", pluginName, className, taskId), e);
        }
    }

    @Override
    public CommonPluginService getCommonPluginService(CommonPluginParam pluginParam) {
        String pluginName = pluginParam.getPluginName();
        String className = pluginParam.getPluginClassName();
        Long taskId = pluginParam.getTaskId();
        if (StringUtils.isBlank(pluginName)) {
            logger.warn("plugin file name is blank,taskId={}", taskId);
            pluginName = getDefaultPluginFile(pluginParam.getWebsiteName(), taskId);
        }
        if (StringUtils.isBlank(className)) {
            logger.warn("plugin class name is blank,taskId={}", taskId);
            className = getDefaultPluginClass(pluginParam.getWebsiteName(), taskId);
        }
        return getCommonPluginService(pluginName, className, taskId);
    }

    private Class getClassFromCache(String pluginName, String version, String className, Long taskId) throws ExecutionException {
        String key = buildCacheKeyForClass(pluginName, version, className);
        logger.info("get class from cache key:{},taskId:{},cacheSize:{}", key, taskId, classCache.size());
        return classCache.get(key);
    }

    private ClassLoader getClassLoaderFromCache(String pluginName, String version) throws ExecutionException {
        String key = buildCacheKeyForClassLoader(pluginName, version);
        logger.info("get classload from cache key:{},,cacheSize:{}", key, classLoacerCache.size());
        return classLoacerCache.get(key);
    }

    private String buildCacheKeyForClassLoader(String pluginName, String version) {
        return new StringBuilder(pluginName).append(":").append(version).toString();
    }

    private String buildCacheKeyForClass(String pluginName, String version, String className) {
        return new StringBuilder(pluginName).append(":").append(version).append(":").append(className).toString();
    }

    private String getDefaultPluginFile(String websiteName, Long taskId) {
        String pluginName = configServiceApi.getProperty("plugin.file", websiteName);
        logger.info("get default plugin file,webisteName={},taskId={},pluginFile={}", websiteName, taskId, pluginName);
        return pluginName;
    }

    private String getDefaultPluginClass(String websiteName, Long taskId) {
        String pluginClass = configServiceApi.getProperty("plugin.class", websiteName);
        logger.info("get default plugin class,webisteName={},taskId={},pluginClass={}", websiteName, taskId, pluginClass);
        return pluginClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //默认1小时更新缓存
        int classloader_upgrade_interval = PropertiesConfiguration.getInstance().getInt("plugin.classloader.upgrade.interval", 3600);
        int classloader_upgrade_max = PropertiesConfiguration.getInstance().getInt("plugin.classloader.upgrade.max", 30);
        logger.info("cache config classloader_upgrade_interval={},classloader_upgrade_max={}", classloader_upgrade_interval, classloader_upgrade_max);
        classLoacerCache = CacheBuilder.newBuilder().expireAfterWrite(classloader_upgrade_interval, TimeUnit.SECONDS)
                .maximumSize(classloader_upgrade_max).removalListener(new RemovalListener<Object, Object>() {
                    @Override
                    public void onRemoval(RemovalNotification<Object, Object> notification) {
                        Object key = notification.getKey();
                        logger.info("cache remove key:{}", key.toString());
                    }
                }).build(new CacheLoader<String, ClassLoader>() {
                    @Override
                    public ClassLoader load(String key) throws Exception {
                        String[] split = key.split(":");
                        String pluginName = split[0];
                        String version = split[1];
                        File pluginFile = pluginService.getPluginFile(pluginName, version);
                        return ClassLoaderUtils.createClassLoader(pluginFile);
                    }
                });

        //默认1小时更新缓存
        int class_upgrade_interval = PropertiesConfiguration.getInstance().getInt("plugin.class.upgrade.interval", 3600);
        int class_upgrade_max = PropertiesConfiguration.getInstance().getInt("plugin.class.upgrade.max", 200);
        logger.info("cache config class_upgrade_interval={},class_upgrade_max={}", class_upgrade_interval, class_upgrade_max);
        classCache = CacheBuilder.newBuilder().expireAfterWrite(class_upgrade_interval, TimeUnit.SECONDS).maximumSize(class_upgrade_max)
                .removalListener(new RemovalListener<Object, Object>() {
                    @Override
                    public void onRemoval(RemovalNotification<Object, Object> notification) {
                        Object key = notification.getKey();
                        logger.info("cache remove key:{}", key.toString());
                    }
                }).build(new CacheLoader<String, Class>() {
                    @Override
                    public Class load(String key) throws Exception {
                        String[] split = key.split(":");
                        String pluginName = split[0];
                        String version = split[1];
                        String className = split[2];
                        ClassLoader classLoader = getClassLoaderFromCache(pluginName, version);
                        return classLoader.loadClass(className);
                    }
                });
    }
}
