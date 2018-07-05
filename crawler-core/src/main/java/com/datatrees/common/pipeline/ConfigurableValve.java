package com.datatrees.common.pipeline;

import com.datatrees.common.conf.Configurable;
import com.datatrees.common.conf.Configuration;

/**
 * @author Jerry
 * @since 20:35 2018/5/14
 */
public abstract class ConfigurableValve extends ValveBase implements Configurable {

    private Configuration configuration;

    @Override
    public void setConf(Configuration conf) {
        this.configuration = conf;
    }

    @Override
    public Configuration getConf() {
        return configuration;
    }
}
