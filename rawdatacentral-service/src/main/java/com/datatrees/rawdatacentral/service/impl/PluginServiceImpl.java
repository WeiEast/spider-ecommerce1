package com.datatrees.rawdatacentral.service.impl;

import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.service.PluginService;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhouxinghai on 2017/7/6.
 */
@Service
public class PluginServiceImpl implements PluginService, InitializingBean {

    private static final Logger              logger    = LoggerFactory.getLogger(PluginServiceImpl.class);

    @Resource
    private RedisService                     redisService;

    @Value("${plugin.local.store.path:/dashu/log/plugins}")
    private String                           pluginPath;

    private static final Map<String, String> pluginMd5 = new ConcurrentHashMap<>();

    @Override
    public String savePlugin(String fileName, byte[] bytes) {
        CheckUtils.checkNotBlank(fileName, "fileName is blank");
        String md5 = DigestUtils.md5Hex(bytes);
        redisService.saveBytes(RedisKeyPrefixEnum.PLUGIN_FILE.getRedisKey(fileName), bytes);
        redisService.cache(RedisKeyPrefixEnum.PLUGIN_FILE_MD5, fileName, md5);
        logger.info("cache plugin fileName={},md5={}", fileName, md5);
        return md5;
    }

    @Override
    public boolean needUpgradePlugin(String fileName) {
        String md5 = redisService.getString(RedisKeyPrefixEnum.PLUGIN_FILE_MD5.getRedisKey(fileName));
        boolean needUpgradlePlugin = !pluginMd5.containsKey(md5) || !StringUtils.equals(md5, pluginMd5.get(fileName));
        if (needUpgradlePlugin) {
            byte[] bytes = redisService.getBytes(RedisKeyPrefixEnum.PLUGIN_FILE.getRedisKey(fileName));
            try {
                FileUtils.writeByteArrayToFile(new File(pluginPath + fileName), bytes, false);
                logger.info("update plugin success fileName={},pluginPath={}", fileName, pluginPath);
            } catch (Exception e) {
                logger.error("upgrade plugin error fileName={},pluginPath={}", fileName, pluginPath);
                throw new RuntimeException("get plugin error", e);
            }
        }
        return needUpgradlePlugin;
    }

    @Override
    public File getPlugin(String fileName) {
        return new File(pluginPath + fileName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.endsWith(pluginPath, "/")) {
            pluginPath += "/";
        }
    }
}
