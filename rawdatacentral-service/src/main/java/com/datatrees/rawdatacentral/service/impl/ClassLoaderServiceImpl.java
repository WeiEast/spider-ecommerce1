package com.datatrees.rawdatacentral.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ClassLoaderUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.vo.PluginUpgradeResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.rawdatacentral.service.PluginService;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhouxinghai on 2017/7/14.
 */
@Service
public class ClassLoaderServiceImpl implements ClassLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderServiceImpl.class);

    @Value("${env:local}")
    private String              env;

    @Resource
    private PluginService       pluginService;

    @Resource
    private RedisService        redisService;

    @Value("${operator.plugin.filename}")
    private String              operatorLoginPlugin;

    @Override
    public Class loadPlugin(String jarName, String className) {
        CheckUtils.checkNotBlank(jarName, "jarName is blank");
        CheckUtils.checkNotBlank(className, "className is blank");
        try {
            //            if (StringUtils.equals("local", env)) {
            //                return Thread.currentThread().getContextClassLoader().loadClass(className);
            //            }
            String postfix = jarName + "_" + className;
            String cacheKey = RedisKeyPrefixEnum.PLUGIN_CLASS.getRedisKey(postfix);
            Class mainClass = redisService.getCache(cacheKey, new TypeReference<Class>() {
            });
            PluginUpgradeResult plugin = pluginService.getPluginFromRedis(jarName);
            if (null == mainClass || plugin.getForceReload()) {
                mainClass = ClassLoaderUtils.loadClass(plugin.getFile(), className);
                redisService.cache(RedisKeyPrefixEnum.PLUGIN_CLASS, postfix, mainClass);
            }
            return mainClass;
        } catch (Exception e) {
            logger.error("loadPlugin error jarName={},className={}", jarName, className);
            throw new RuntimeException(
                TemplateUtils.format("loadPlugin error jarName={},className={}", jarName, className));
        }
    }

    @Override
    public OperatorPluginService getOperatorPluginService(String websiteName) {
        try {
            String propertyName = "operator.plugin." + websiteName;
            String mainLoginClass = PropertiesConfiguration.getInstance().get(propertyName);
            CheckUtils.checkNotBlank(mainLoginClass, "get operator plugin class error websiteName=" + websiteName);
            Class loginClass = loadPlugin(operatorLoginPlugin, mainLoginClass);
            if (!OperatorPluginService.class.isAssignableFrom(loginClass)) {
                throw new RuntimeException(
                    "mainLoginClass not impl com.datatrees.rawdatacentral.service.OperatorPluginService");
            }
            return (OperatorPluginService) loginClass.newInstance();
        } catch (Exception e) {

            logger.error("getOperatorService error websiteName={}", websiteName, e);
            throw new RuntimeException("getOperatorPluginService error websiteName=" + websiteName);
        }
    }

}
