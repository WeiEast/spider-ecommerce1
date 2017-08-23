/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.conf;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.DefaultConfiguration;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 13, 2014 10:10:55 AM
 */
public class CrawlerConfiguration extends DefaultConfiguration {

    private Configuration defaultConf = null;

    public CrawlerConfiguration(Configuration conf) {
        super();
        setDefaultConf(conf);
    }

    protected Configuration getDefaultConf() {
        return defaultConf;
    }

    public void setDefaultConf(Configuration defaultConf) {
        this.defaultConf = defaultConf;
    }

    /**
     * get property from conf
     * get from default conf if current conf does not exists
     */
    @Override
    public String get(String name) {
        String val = super.get(name);
        if (StringUtils.isEmpty(val) && defaultConf != null) {
            val = defaultConf.get(name);
        }
        return null;
    }

    /**
     * set property
     */
    @Override
    public void set(String name, String value) {
        super.set(name, value);
    }

}
