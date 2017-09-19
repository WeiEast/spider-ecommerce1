package com.datatrees.rawdatacentral.service.plugin;

import javax.annotation.Resource;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import com.datatrees.crawler.core.processor.plugin.PluginWrapper;
import com.datatrees.rawdatacentral.domain.vo.PluginUpgradeResult;
import com.datatrees.rawdatacentral.service.PluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/7/6.
 */
@Service
public class RedisPluginManager extends PluginManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisPluginManager.class);
    @Resource
    private PluginService pluginService;

    @Override
    public PluginWrapper getPlugin(String websiteName, AbstractPlugin pluginDesc) {
        PluginUpgradeResult plugin = null;
        try {
            String fileName = pluginDesc.getFileName();
            if (StringUtils.isNoneBlank(fileName)) {
                plugin = pluginService.getPluginFromRedis(pluginDesc.getFileName());
            } else {
                plugin = pluginService.getPluginFromLocal(websiteName, pluginDesc);
            }
            PluginWrapper wrapper = new PluginWrapper(plugin.getFile(), pluginDesc);
            wrapper.setForceReload(plugin.getForceReload());
            return wrapper;
        } catch (Throwable e) {
            logger.error("getPlugin error", e);
            return null;
        }

    }
}
