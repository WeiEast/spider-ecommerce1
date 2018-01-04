package com.datatrees.rawdatacentral.service;

import com.datatrees.rawdatacentral.api.service.CommonPluginService;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;

public interface ClassLoaderService {

    Class loadPlugin(String pluginName, String className, Long taskId);

    OperatorPluginService getOperatorPluginService(String websiteName, Long taskId);

    CommonPluginService getCommonPluginService(String pluginName, String className, Long taskId);

    CommonPluginService getCommonPluginService(CommonPluginParam pluginParam);

}
