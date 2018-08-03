package com.datatrees.spider.share.service.impl.dubbo;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.spider.share.api.ConfigApi;
import org.springframework.stereotype.Service;

@Service
public class ConfigApiImpl implements ConfigApi {

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
