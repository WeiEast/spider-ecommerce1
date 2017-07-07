package com.datatrees.rawdatacentral.service;

import java.io.File;

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
     * 是否需要升级plugin
     * @param fileName
     * @return
     */
    boolean needUpgradePlugin(String fileName);

    /**
     * 获取插件
     * @param fileName
     * @return
     */
    File getPlugin(String fileName);

}
