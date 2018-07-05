package com.treefinance.crawler.framework.format;

import com.datatrees.common.conf.Configurable;
import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.PropertiesConfiguration;

/**
 * @author Jerry
 * @since 16:08 2018/5/14
 */
public abstract class ConfigurableFormatter<R> extends CommonFormatter<R> implements Configurable {

    private Configuration configuration;

    @Override
    public void setConf(Configuration conf) {
        this.configuration = conf;
    }

    @Override
    public Configuration getConf() {
        return configuration == null ? PropertiesConfiguration.getInstance() : configuration;
    }

}
