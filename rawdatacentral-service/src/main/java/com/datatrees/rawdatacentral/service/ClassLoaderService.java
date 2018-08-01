package com.datatrees.rawdatacentral.service;

import com.datatrees.spider.share.service.plugin.CommonPlugin;
import com.datatrees.spider.share.domain.CommonPluginParam;

public interface ClassLoaderService {

    Class loadPlugin(String pluginName, String className, Long taskId);

    CommonPlugin getCommonPluginService(String pluginName, String className, Long taskId);

    CommonPlugin getCommonPluginService(CommonPluginParam pluginParam);

}
