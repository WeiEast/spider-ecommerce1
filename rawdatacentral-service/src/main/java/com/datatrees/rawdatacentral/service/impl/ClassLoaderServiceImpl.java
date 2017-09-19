package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/7/14.
 */
@Service
public class ClassLoaderServiceImpl implements ClassLoaderService, InitializingBean {

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
    private Map<String, Class> classCache      = new HashMap<>();
    private Map<String, Long>  classUpdateTime = new ConcurrentHashMap<>();

    @Override
    public Class loadPlugin(String jarName, String className) {
        CheckUtils.checkNotBlank(jarName, "jarName is blank");
        CheckUtils.checkNotBlank(className, "className is blank");
        String pluginFile = null;
        try {
            String cacheKey = jarName + "_" + className;
            Class mainClass = classCache.get(cacheKey);
            PluginUpgradeResult plugin = pluginService.getPluginFromRedis(jarName);
            if (null == mainClass || plugin.getForceReload()) {
                mainClass = ClassLoaderUtils.loadClass(plugin.getFile(), className);
                logger.info("重新加载class,className={},jar={},pluginFile={}", className, jarName, plugin.getFile().getAbsolutePath());
                classCache.put(cacheKey, mainClass);
            }
            pluginFile = plugin.getFile().getAbsolutePath();
            //1个不使用就自动标记回收
            classUpdateTime.put(cacheKey, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
            return mainClass;
        } catch (Throwable e) {
            logger.error("loadPlugin error jarName={},className={}", jarName, className, e);
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
        } catch (Throwable e) {
            logger.error("getOperatorService error websiteName={}", websiteName, e);
            throw new RuntimeException("getOperatorPluginService error websiteName=" + websiteName);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Iterator<Map.Entry<String, Long>> iterator = classUpdateTime.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Long> entry = iterator.next();
                        if (System.currentTimeMillis() <= entry.getValue()) {
                            classCache.remove(entry.getKey());
                            logger.info("remove class cache key={}", entry.getKey());
                            iterator.remove();
                        }
                    }
                    try {
                        TimeUnit.MINUTES.sleep(15);
                    } catch (InterruptedException e) {
                        logger.error("clean class cache error", e);
                    }
                }
            }
        }).start();

    }
}
