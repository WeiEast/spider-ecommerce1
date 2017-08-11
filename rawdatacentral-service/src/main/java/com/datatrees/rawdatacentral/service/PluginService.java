package com.datatrees.rawdatacentral.service;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.rawdatacentral.domain.vo.PluginUpgradeResult;

/**
 * 插件管理
 * Created by zhouxinghai on 2017/7/6.
 */
public interface PluginService {

    /**
     * 保存插件
     * @param fileName
     * @param bytes
     */
    String savePlugin(String fileName, byte[] bytes);

    /**
     * 获取插件
     * @param fileName
     * @return
     */
    PluginUpgradeResult getPluginFromRedis(String fileName);

    /**
     * 获取插件
     * @param
     * @return
     */
    PluginUpgradeResult getPluginFromLocal(String websiteName, AbstractPlugin pluginDesc);

}
