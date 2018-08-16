/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.crawler.core.processor.conf;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.DefaultConfiguration;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 13, 2014 10:10:55 AM
 */
@Deprecated
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
        return val;
    }

    /**
     * set property
     */
    @Override
    public void set(String name, String value) {
        super.set(name, value);
    }

}
