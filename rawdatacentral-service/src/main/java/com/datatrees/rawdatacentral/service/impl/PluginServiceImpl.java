package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.io.File;

import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
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

    private static final Logger logger = LoggerFactory.getLogger(PluginServiceImpl.class);
    @Resource
    private RedisService redisService;
    @Value("${plugin.local.store.path:/dashu/log/plugins/}")
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
        PluginUpgradeResult result = new PluginUpgradeResult();
        String md5 = redisService.getString(RedisKeyPrefixEnum.PLUGIN_FILE_MD5.getRedisKey(fileName));
        if (StringUtils.isBlank(md5)) {
            logger.error("没有从redis读取到插件md5,fileName={}", fileName);
            throw new CommonException("没有从redis读取到插件:" + fileName);
        }
        //修改策略,文件保存到本地用${md5}.jar,这样文件变化了,classLoader就变了
        File file = new File(TemplateUtils.format("{}{}-{}.jar", pluginPath, fileName,md5));
        boolean forceReload = !file.exists();
        if (forceReload) {
            byte[] bytes = redisService.getBytes(RedisKeyPrefixEnum.PLUGIN_FILE.getRedisKey(fileName));
            try {
                FileUtils.writeByteArrayToFile(file, bytes, false);
                logger.info("plugin已经更新,重新加载到本地,fileName={},pluginPath={},md5={}", fileName, pluginPath, md5);
            } catch (Throwable e) {
                logger.error("upgrade plugin error fileName={},pluginPath={},md5={}", fileName, pluginPath, md5, e);
                throw new RuntimeException("get plugin error", e);
            }
        }
        result.setForceReload(forceReload);
        result.setFile(file);
        logger.info("getPluginFromRedis success fileName={},localJar={}", fileName, file.getName());
        return result;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.endsWith(pluginPath, "/")) {
            pluginPath += "/";
        }
        File file = new File(pluginPath);
        if (file.exists()) {
            file.deleteOnExit();
            file.mkdirs();
        }
        logger.info("初始化plugin目录,清理所有jar,pluginPath={}", pluginPath);
    }
}
