package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.exception.CommonException;
import com.datatrees.rawdatacentral.domain.vo.PluginUpgradeResult;
import com.datatrees.rawdatacentral.service.PluginService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/7/6.
 */
@Service
public class PluginServiceImpl implements PluginService, InitializingBean {

    private static final Logger              logger    = LoggerFactory.getLogger(PluginServiceImpl.class);
    private static final Map<String, String> pluginMd5 = new ConcurrentHashMap<>();
    @Resource
    private RedisService redisService;
    @Value("${plugin.local.store.path:/dashu/log/plugins}")
    private String       pluginPath;

    @Override
    public String savePlugin(String fileName, byte[] bytes) {
        CheckUtils.checkNotBlank(fileName, "fileName is blank");
        String md5 = DigestUtils.md5Hex(bytes);
        redisService.saveBytes(RedisKeyPrefixEnum.PLUGIN_FILE.getRedisKey(fileName), bytes);
        redisService.saveString(RedisKeyPrefixEnum.PLUGIN_FILE_MD5, fileName, md5);
        logger.info("cache plugin fileName={},md5={}", fileName, md5);
        return md5;
    }

    @Override
    public PluginUpgradeResult getPluginFromRedis(String fileName) {
        File file = new File(pluginPath + fileName);
        PluginUpgradeResult result = new PluginUpgradeResult();
        String md5 = redisService.getString(RedisKeyPrefixEnum.PLUGIN_FILE_MD5.getRedisKey(fileName));
        if (StringUtils.isBlank(md5)) {
            logger.error("没有从redis读取到插件md5,fileName={}", fileName);
            throw new CommonException("没有从redis读取到插件:" + fileName);
        }
        boolean forceReload = !pluginMd5.containsKey(fileName) || !StringUtils.equals(md5, pluginMd5.get(fileName));
        if (forceReload) {
            byte[] bytes = redisService.getBytes(RedisKeyPrefixEnum.PLUGIN_FILE.getRedisKey(fileName));
            try {
                FileUtils.writeByteArrayToFile(file, bytes, false);
                pluginMd5.put(fileName, md5);
                logger.info("plugin已经更新,重新加载到本地,fileName={},pluginPath={}", fileName, pluginPath);
            } catch (Exception e) {
                logger.error("upgrade plugin error fileName={},pluginPath={}", fileName, pluginPath);
                throw new RuntimeException("get plugin error", e);
            }
        }
        result.setForceReload(forceReload);
        result.setFile(file);
        logger.info("getPluginFromRedis success fileName={},pluginPath={}", fileName, pluginPath);
        return result;
    }

    @Override
    public PluginUpgradeResult getPluginFromLocal(String websiteName, AbstractPlugin pluginDesc) {
        String filePath = pluginPath + websiteName + "/" + pluginDesc.getId() + "." + pluginDesc.getType();
        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("local plugin not found filePath={}", filePath);
            throw new RuntimeException("local plugin not found filePath=" + filePath);
        }
        PluginUpgradeResult result = new PluginUpgradeResult();
        result.setFile(file);
        result.setForceReload(false);
        logger.info("getPluginFromLocal success filePath={}", filePath);
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.endsWith(pluginPath, "/")) {
            pluginPath += "/";
        }
    }
}
