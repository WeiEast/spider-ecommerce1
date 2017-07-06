package com.datatrees.rawdatacentral.service;

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
    void savePlugin(String fileName, byte[] bytes);

}
