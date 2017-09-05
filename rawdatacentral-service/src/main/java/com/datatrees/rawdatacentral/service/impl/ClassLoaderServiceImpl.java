package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/7/14.
 */
@Service
public class ClassLoaderServiceImpl implements ClassLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderServiceImpl.class);
    @Value("${env:local}")
    private String                 env;
    @Resource
    private PluginService          pluginService;
    @Resource
    private RedisService           redisService;
    @Value("${operator.plugin.filename}")
    private String                 operatorPluginFilename;
    @Resource
    private WebsiteOperatorService websiteOperatorService;

    @Override
    public Class loadPlugin(String jarName, String className) {
        CheckUtils.checkNotBlank(jarName, "jarName is blank");
        CheckUtils.checkNotBlank(className, "className is blank");
        try {
            ////本地调试不走redis
            //if (StringUtils.equals("local", env)) {
            //    return Thread.currentThread().getContextClassLoader().loadClass(className);
            //}
            String postfix = jarName + "_" + className;
            String cacheKey = RedisKeyPrefixEnum.PLUGIN_CLASS.getRedisKey(postfix);
            Class mainClass = redisService.getCache(cacheKey, new TypeReference<Class>() {});
            PluginUpgradeResult plugin = pluginService.getPluginFromRedis(jarName);
            if (null == mainClass || plugin.getForceReload()) {
                mainClass = ClassLoaderUtils.loadClass(plugin.getFile(), className);
                logger.info("重新加载class,className={},jar={}", className, jarName);
                redisService.cache(RedisKeyPrefixEnum.PLUGIN_CLASS, postfix, mainClass);
            }
            return mainClass;
        } catch (Exception e) {
            logger.error("loadPlugin error jarName={},className={}", jarName, className);
            throw new RuntimeException(TemplateUtils.format("loadPlugin error jarName={},className={}", jarName, className));
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
        } catch (Exception e) {
            logger.error("getOperatorService error websiteName={}", websiteName, e);
            throw new RuntimeException("getOperatorPluginService error websiteName=" + websiteName);
        }
    }

}
