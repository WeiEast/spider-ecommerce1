package com.datatrees.rawdatacentral.service;

/**
 * 目前只给运营商用
 * Created by zhouxinghai on 2017/7/14.
 */
public interface ClassLoaderService {

    /**
     * 加载插件
     * @param jarName
     * @param className
     * @return
     */
    Class loadPlugin(String jarName, String className);

    /**
     * @param websiteName
     * @return
     */
    OperatorPluginService getOperatorPluginService(String websiteName);

}
