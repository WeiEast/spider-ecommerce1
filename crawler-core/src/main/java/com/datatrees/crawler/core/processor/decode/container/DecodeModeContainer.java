/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.decode.container;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.properties.UnicodeMode;
import com.datatrees.crawler.core.processor.Constants;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 14, 2014 11:09:37 AM
 */
public class DecodeModeContainer {

    private static final Logger                   log           = LoggerFactory.getLogger(DecodeModeContainer.class);
    private static final Cache                    CACHE         = new Cache();
    private              Configuration            conf          = null;
    private              Map<UnicodeMode, String> unicodeMapper = new HashMap<UnicodeMode, String>();

    private DecodeModeContainer(Configuration conf) {
        setConf(conf);
        initMap();
    }

    private DecodeModeContainer() {}

    public static DecodeModeContainer get(Configuration conf) {

        if (conf == null) {
            conf = PropertiesConfiguration.getInstance();
        }
        DecodeModeContainer container = CACHE.get(conf);
        return container;
    }

    /**
     * @param periodMapper2
     */
    private void initMap() {
        String unicodeFormat = getConf().get(Constants.UNICODE_FROMAT_CONFIG);
        if (StringUtils.isNotEmpty(unicodeFormat)) {
            try {
                Map<UnicodeMode, String> decoders = (Map<UnicodeMode, String>) GsonUtils.fromJson(unicodeFormat, new TypeToken<Map<UnicodeMode, String>>() {}.getType());
                unicodeMapper.putAll(decoders);
            } catch (Exception e) {
                log.error("unicode json format error!");
            }
        }
    }

    ;

    public Map<UnicodeMode, String> getModeMapper() {
        return Collections.unmodifiableMap(unicodeMapper);
    }

    public Configuration getConf() {
        return conf;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    static class Cache {

        private static Map<Configuration, DecodeModeContainer> cache = new HashMap<Configuration, DecodeModeContainer>();

        public synchronized DecodeModeContainer get(Configuration conf) {
            DecodeModeContainer container = cache.get(conf);
            if (container == null) {
                container = new DecodeModeContainer(conf);
                cache.put(conf, container);
            }
            return container;
        }
    }

}
