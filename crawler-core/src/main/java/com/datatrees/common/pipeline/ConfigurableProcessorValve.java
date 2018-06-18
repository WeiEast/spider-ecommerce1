package com.datatrees.common.pipeline;

import com.datatrees.common.conf.Configurable;
import com.datatrees.common.conf.Configuration;

/**
 * @author Jerry
 * @since 09:45 2018/5/24
 */
public abstract class ConfigurableProcessorValve extends ProcessorValve implements Configurable {

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
