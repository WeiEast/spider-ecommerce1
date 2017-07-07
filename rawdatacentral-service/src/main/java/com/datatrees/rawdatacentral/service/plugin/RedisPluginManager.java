package com.datatrees.rawdatacentral.service.plugin;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.common.exception.PluginExeception;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import com.datatrees.crawler.core.processor.plugin.PluginWrapper;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.vo.PluginUpgradeResult;
import com.datatrees.rawdatacentral.service.PluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhouxinghai on 2017/7/6.
 */
@Service
public class RedisPluginManager extends PluginManager {

    @Resource
    private PluginService       pluginService;

    private static final Logger logger = LoggerFactory.getLogger(RedisPluginManager.class);

    @Override
    public PluginWrapper getPlugin(String websiteName, AbstractPlugin pluginDesc) throws PluginExeception {
        String fileName = pluginDesc.getFileName();
        CheckUtils.checkNotBlank(fileName, "plugin fileName is blank");
        PluginUpgradeResult plugin = pluginService.getPlugin(pluginDesc.getFileName());
        PluginWrapper wrapper = new PluginWrapper(plugin.getFile(), pluginDesc);
        wrapper.setForceReload(plugin.getForceReload());
        return wrapper;
    }
}
