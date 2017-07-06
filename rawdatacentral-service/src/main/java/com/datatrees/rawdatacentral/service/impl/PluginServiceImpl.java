package com.datatrees.rawdatacentral.service.impl;

import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.service.PluginService;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhouxinghai on 2017/7/6.
 */
@Service
public class PluginServiceImpl implements PluginService, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(PluginServiceImpl.class);

    @Resource
    private RedisService        redisService;

    @Value("${plugin.local.store.path:/dashu/log/plugins}")
    private String              pluginPath;;

    @Override
    public void savePlugin(String fileName, byte[] bytes) {
        CheckUtils.checkNotBlank(fileName, "fileName is blank");
        String md5 = DigestUtils.md5Hex(bytes);
        redisService.saveBytes(RedisKeyPrefixEnum.PLUGIN_FILE.getRedisKey(fileName), bytes);
        redisService.cache(RedisKeyPrefixEnum.PLUGIN_FILE_MD5, fileName, md5);
        logger.info("cache plugin fileName={},md5={}", fileName, md5);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.endsWith(pluginPath, "/")) {
            pluginPath += "/";
        }
    }
}
