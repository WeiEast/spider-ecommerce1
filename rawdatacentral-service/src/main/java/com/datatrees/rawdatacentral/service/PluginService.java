package com.datatrees.rawdatacentral.service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.datatrees.rawdatacentral.domain.vo.PluginUpgradeResult;

/**
 * 插件管理
 * Created by zhouxinghai on 2017/7/6.
 */
public interface PluginService {

    /**
     * 保存插件
     * 返回md5
     * @param fileName
     * @param bytes
     */
    @Deprecated
    String savePlugin(String fileName, byte[] bytes);

    /**
     * 环境变量
     * @param sassEnv
     * @param fileName
     * @param bytes
     * @param version
     * @return
     */
    String savePlugin(String sassEnv, String fileName, byte[] bytes, String version);

    /**
     * 获取插件
     * @param pluginName
     * @return
     */
    PluginUpgradeResult getPluginFromRedis(String pluginName) throws IOException;

    /**
     * 新的从redis获取配置
     * @param pluginName
     * @return
     */
    PluginUpgradeResult getPluginFromRedisNew(String pluginName) throws IOException;

    /**
     * 获取插件版本
     * @param pluginName
     * @return
     */
    String getPluginVersionFromCache(String pluginName) throws ExecutionException;

    /**
     * 获取插件文件路径
     * @param pluginName
     * @param version
     * @return
     */
    File getPluginFile(String pluginName, String version);

}
