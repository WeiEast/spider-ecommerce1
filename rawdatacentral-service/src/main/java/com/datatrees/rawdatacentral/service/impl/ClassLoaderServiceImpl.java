package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ClassLoaderUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.exception.CommonException;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.vo.PluginUpgradeResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.rawdatacentral.service.PluginService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import com.google.common.cache.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/7/14.
 */
@Service
public class ClassLoaderServiceImpl implements ClassLoaderService, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderServiceImpl.class);
    private static LoadingCache<String, ClassLoader> classLoacerCache1;
    private static LoadingCache<String, Class>       classCache1;
    @Value("${env:local}")
    private        String                            env;
    @Resource
    private        PluginService                     pluginService;
    @Resource
    private        RedisService                      redisService;
    @Value("${operator.plugin.filename}")
    private        String                            operatorPluginFilename;
    @Resource
    private        WebsiteOperatorService            websiteOperatorService;

    @Override
    public Class loadPlugin(String pluginName, String className) {
        CheckUtils.checkNotBlank(pluginName, "pluginName is blank");
        CheckUtils.checkNotBlank(className, "className is blank");
        try {
            String version = pluginService.getPluginVersionFromCache(pluginName);
            Class mainClass = getClassFromCache(pluginName, version, className);
            return mainClass;
        } catch (Throwable e) {
            logger.error("loadPlugin error pluginName={},className={}", pluginName, className, e);
            throw new RuntimeException(TemplateUtils.format("loadPlugin error pluginName={},className={}", pluginName, className));
        }
    }

    @Override
    public Class reloadClass(String pluginName, String className) {
        CheckUtils.checkNotBlank(pluginName, "pluginName is blank");
        CheckUtils.checkNotBlank(className, "className is blank");
        try {
            PluginUpgradeResult plugin = pluginService.getPluginFromRedis(pluginName);
            ClassLoader classLoader = ClassLoaderUtils.createClassLoader(plugin.getFile());
            Class mainClass = classLoader.loadClass(className);
            logger.info("reload class success,className={},pluginFile={}", className, plugin.getFile().getAbsolutePath());
            return mainClass;
        } catch (Throwable e) {
            logger.error("reload class error pluginName={},className={}", pluginName, className, e);
            throw new RuntimeException(TemplateUtils.format("loadPlugin error pluginName={},className={}", pluginName, className));
        }
    }

    @Override
    public OperatorPluginService getOperatorPluginService(String websiteName) {
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
            pluginFileName = operatorPluginFilename;
        }
        try {
            Class loginClass = loadPlugin(pluginFileName, mainLoginClass);
            if (!OperatorPluginService.class.isAssignableFrom(loginClass)) {
                throw new RuntimeException("mainLoginClass not impl com.datatrees.rawdatacentral.service.OperatorPluginService");
            }
            return (OperatorPluginService) loginClass.newInstance();
        } catch (Throwable e) {
            logger.error("getOperatorService error websiteName={}", websiteName, e);
            throw new RuntimeException("getOperatorPluginService error websiteName=" + websiteName, e);
        }
    }

    private Class getClassFromCache(String pluginName, String version, String className) throws ExecutionException {
        String key = buildCacheKeyForClass(pluginName, version, className);
        return classCache1.get(key);
    }

    private ClassLoader getClassLoaderFromCache(String pluginName, String version) throws ExecutionException {
        String key = buildCacheKeyForClassLoader(pluginName, version);
        return classLoacerCache1.get(key);
    }

    private String buildCacheKeyForClassLoader(String pluginName, String version) {
        return new StringBuilder(pluginName).append(":").append(version).toString();
    }

    private String buildCacheKeyForClass(String pluginName, String version, String className) {
        return new StringBuilder(pluginName).append(":").append(version).append(":").append(className).toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //默认1小时更新缓存
        int classloader_upgrade_interval = PropertiesConfiguration.getInstance().getInt("plugin.classloader.upgrade.interval", 3600);
        logger.info("cache config classloader_upgrade_interval={}", classloader_upgrade_interval);
        classLoacerCache1 = CacheBuilder.newBuilder().expireAfterWrite(classloader_upgrade_interval, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<Object, Object>() {
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
        logger.info("cache config class_upgrade_interval={}", class_upgrade_interval);
        classCache1 = CacheBuilder.newBuilder().expireAfterWrite(class_upgrade_interval, TimeUnit.SECONDS)
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
