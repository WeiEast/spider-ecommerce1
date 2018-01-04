package com.datatrees.rawdatacentral.collector.service;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.api.ConfigServiceApi;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceApiImpl implements ConfigServiceApi {

    @Override
    public String getProperty(String name) {
        return PropertiesConfiguration.getInstance().get(name);
    }

    @Override
    public String getProperty(String prefix, String name) {
        return PropertiesConfiguration.getInstance().get(prefix + "." + name);
    }

    @Override
    public String getPropertyOrDefaultValue(String name, String defaultValue) {
        return PropertiesConfiguration.getInstance().get(name, defaultValue);
    }

    @Override
    public String getPropertyOrDefaultValue(String prefix, String name, String defaultValue) {
        return PropertiesConfiguration.getInstance().get(prefix + "." + name, defaultValue);
    }
}
