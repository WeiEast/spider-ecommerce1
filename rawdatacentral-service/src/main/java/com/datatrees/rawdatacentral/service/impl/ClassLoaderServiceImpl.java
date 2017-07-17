package com.datatrees.rawdatacentral.service.impl;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ClassLoaderUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.vo.PluginUpgradeResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorLoginPluginService;
import com.datatrees.rawdatacentral.service.PluginService;
import com.datatrees.rawdatacentral.share.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhouxinghai on 2017/7/14.
 */
@Service
public class ClassLoaderServiceImpl implements ClassLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderServiceImpl.class);

    @Resource
    private PluginService       pluginService;

    @Resource
    private RedisService        redisService;

    @Override
    public Class loadPlugin(String jarName, String className) {
        CheckUtils.checkNotBlank(jarName, "jarName is blank");
        CheckUtils.checkNotBlank(className, "className is blank");
        PluginUpgradeResult plugin = pluginService.getPluginFromRedis(jarName);
        String postfix = jarName + "_" + className;
        String cacheKey = RedisKeyPrefixEnum.PLUGIN_CLASS.getRedisKey(postfix);
        Class mainClass = redisService.getCache(cacheKey, Class.class);
        if (null == mainClass || plugin.getForceReload()) {
            mainClass = ClassLoaderUtils.loadClass(plugin.getFile(), className);
            redisService.cache(RedisKeyPrefixEnum.PLUGIN_CLASS, postfix, mainClass);
        }
        return mainClass;
    }

    @Override
    public OperatorLoginPluginService getOperatorLongService(String websiteName) {
        try {
            String propertyName = "login.class." + websiteName;
            String jarName = websiteName + ".jar";
            String mainLoginClass = PropertiesConfiguration.getInstance().get(propertyName);
            CheckUtils.checkNotBlank(mainLoginClass, "get login class error websiteName=" + websiteName);
            Class loginClass = loadPlugin(jarName, mainLoginClass);
            if (!loginClass.isAssignableFrom(OperatorLoginPluginService.class)) {
                throw new RuntimeException(
                    "mainLoginClass not impl com.datatrees.rawdatacentral.service.OperatorLoginPluginService");
            }
            return (OperatorLoginPluginService) loginClass.newInstance();
        } catch (Exception e) {
            logger.error("getOperatorLongService error websiteName={}", websiteName);
            throw new RuntimeException("getOperatorLongService error websiteName=" + websiteName);
        }
    }

}
