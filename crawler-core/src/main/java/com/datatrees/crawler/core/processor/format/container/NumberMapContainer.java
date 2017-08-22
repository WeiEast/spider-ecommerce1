/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.format.container;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.DefaultConfiguration;
import com.datatrees.common.conf.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.format.unit.NumberUnit;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 31, 2014 10:17:53 AM
 */
public class NumberMapContainer {
    private static final Logger log = LoggerFactory.getLogger(NumberMapContainer.class);

    private static final Cache CACHE = new Cache();

    private Map<String, NumberUnit> numberMapper = new HashMap<String, NumberUnit>();

    private NumberMapContainer(Configuration conf) {
        String config =
                conf.get(Constants.NUMBER_FROMAT_CONFIG,
                        "{\"ONE\":\"views|次\",\"TEN_THOUSAND\":\"万\"}");
        initMap(config, numberMapper);
    }

    /**
     * 
     * @param periodMapper2
     */
    private void initMap(String config, Map<String, NumberUnit> customNumberMapper) {
        if (StringUtils.isNotEmpty(config)) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> perids =
                        (Map<String, String>) GsonUtils.fromJson(config,
                                new TypeToken<Map<String, String>>() {}.getType());
                Iterator<String> unitIterator = perids.keySet().iterator();
                while (unitIterator.hasNext()) {
                    String unit = unitIterator.next();
                    String val = perids.get(unit);
                    NumberUnit tu = null;
                    try {
                        tu = NumberUnit.valueOf(unit);
                    } catch (Exception e) {
                        // ignore
                    }
                    if (tu != null && StringUtils.isNotEmpty(val)) {
                        customNumberMapper.put(val, tu);
                        log.debug(String.format("period %s , pattern %s", tu.name(), val));
                    }
                }
            } catch (Exception e) {
                log.error("Period json format error!");
            }
        }
    }



    public Map<String, NumberUnit> getNumberMapper() {
        return Collections.unmodifiableMap(numberMapper);
    }

    public Map<String, NumberUnit> getNumberMapper(String config) {
        if (StringUtils.isBlank(config)) {
            return this.getNumberMapper();
        } else {
            Map<String, NumberUnit> customPeriodMapper = new HashMap<String, NumberUnit>();
            this.initMap(config, customPeriodMapper);
            return customPeriodMapper;
        }
    }


    private NumberMapContainer() {};

    public static NumberMapContainer get(Configuration conf) {

        if (conf == null) {
            conf = PropertiesConfiguration.getInstance();
        }
        NumberMapContainer container = CACHE.get(conf);
        return container;
    }


    static class Cache {

        private static Map<Configuration, NumberMapContainer> cache =
                new HashMap<Configuration, NumberMapContainer>();

        public synchronized NumberMapContainer get(Configuration conf) {
            NumberMapContainer container = cache.get(conf);
            if (container == null) {
                container = new NumberMapContainer(conf);
                cache.put(conf, container);
            }
            return container;
        }
    }

}
