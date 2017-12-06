package com.datatrees.rawdatacentral.service;

/**
 * 目前只给运营商用
 * Created by zhouxinghai on 2017/7/14.
 */
public interface ClassLoaderService {

    /**
     * 加载插件
     * @param pluginName
     * @param className
     * @return
     */
    Class loadPlugin(String pluginName, String className);

    /**
     * 重新加载插件
     * @param pluginName
     * @param className
     * @return
     */
    Class reloadClass(String pluginName, String className);

    /**
     * 加载运营商插件
     * @param websiteName
     * @return
     */
    OperatorPluginService getOperatorPluginService(String websiteName);

}
