package com.datatrees.crawler.core.processor.format.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.format.unit.RMBUnit;
import com.google.common.reflect.TypeToken;

public class RMBMapContainer {
    private static final Logger log = LoggerFactory.getLogger(RMBMapContainer.class);

    private static final Cache CACHE = new Cache();

    private Map<String, RMBUnit> rmbMapper = new LinkedHashMap<String, RMBUnit>();

    private RMBMapContainer(Configuration conf) {
        String config = conf.get(Constants.RMB_FROMAT_CONFIG, "{\"YUAN\":\"yuan|元\",\"JIAO\":\"角|jiao\",\"FEN\":\"分|fen\"}");
        initMap(config, rmbMapper);
    }

    private void initMap(String config, Map<String, RMBUnit> rmbMapper) {
        if (StringUtils.isNotEmpty(config)) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> rmbMap =
                        (LinkedHashMap<String, String>) GsonUtils.fromJson(config, new TypeToken<LinkedHashMap<String, String>>() {}.getType());
                ListIterator<Map.Entry<String, String>> i =
                        new ArrayList<Map.Entry<String, String>>(rmbMap.entrySet()).listIterator(rmbMap.size());
                while (i.hasPrevious()) {
                    Map.Entry<String, String> entry = i.previous();
                    String unit = entry.getKey();
                    String val = entry.getValue();
                    RMBUnit pu = null;
                    try {
                        pu = RMBUnit.valueOf(unit);
                    } catch (Exception e) {
                        log.error("rmb value error:" + e);
                    }
                    if (pu != null && StringUtils.isNotEmpty(val)) {
                    	rmbMapper.put(val, pu);
                        if (log.isDebugEnabled()){
                            log.debug(String.format("rmb %s , pattern %s", pu.name(), val));
                        }
                    }
                }
            } catch (Exception e) {
                log.error("rmb json format error!",e);
            }
        }
    }

    public Map<String, RMBUnit> getRMBMapper(String config) {
        if (StringUtils.isBlank(config)) {
            return this.getRMBMapper();
        } else {
            Map<String, RMBUnit> customPaymentMapper = new LinkedHashMap<String, RMBUnit>();
            this.initMap(config, customPaymentMapper);
            return customPaymentMapper;
        }
    }


    public Map<String, RMBUnit> getRMBMapper() {
        return Collections.unmodifiableMap(rmbMapper);
    }

    private RMBMapContainer() {};

    public static RMBMapContainer get(Configuration conf) {

        if (conf == null) {
            conf = PropertiesConfiguration.getInstance();
        }
        RMBMapContainer container = CACHE.get(conf);
        return container;
    }


    static class Cache {

        private static Map<Configuration, RMBMapContainer> cache = new HashMap<Configuration, RMBMapContainer>();

        public synchronized RMBMapContainer get(Configuration conf) {
        	RMBMapContainer container = cache.get(conf);
            if (container == null) {
                container = new RMBMapContainer(conf);
                cache.put(conf, container);
            }
            return container;
        }
    }
}
