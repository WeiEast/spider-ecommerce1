package com.treefinance.crawler.framework.format.number;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.Constants;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 02:08 2018/6/2
 */
public final class NumberUnitMapping {

    private static final Logger                                               log   = LoggerFactory.getLogger(NumberUnitMapping.class);
    private static final LoadingCache<Configuration, Map<String, NumberUnit>> CACHE = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).softValues().build(new CacheLoader<Configuration, Map<String, NumberUnit>>() {
        @Override
        public Map<String, NumberUnit> load(@Nonnull Configuration conf) throws Exception {
            Map<String, NumberUnit> numberMapper = new HashMap<>();
            String config = conf.get(Constants.NUMBER_FROMAT_CONFIG, "{\"ONE\":\"views|次\",\"TEN_THOUSAND\":\"万\"}");
            if (StringUtils.isNotEmpty(config)) {
                Map<String, String> periods = GsonUtils.fromJson(config, new TypeToken<Map<String, String>>() {}.getType());

                periods.forEach((unit, val) -> {
                    NumberUnit tu = null;
                    try {
                        tu = NumberUnit.valueOf(unit);
                    } catch (Exception e) {
                        // ignore
                    }
                    if (tu != null && StringUtils.isNotEmpty(val)) {
                        log.debug("period: {}, pattern: {}", tu, val);
                        numberMapper.put(val, tu);
                    }
                });
            }

            return numberMapper;
        }
    });

    private NumberUnitMapping() {}

    public static Map<String, NumberUnit> getNumberUnitMap(Configuration conf) {
        Configuration configuration = conf;
        if (configuration == null) {
            configuration = PropertiesConfiguration.getInstance();
        }

        Map<String, NumberUnit> map = CACHE.getUnchecked(configuration);

        return Collections.unmodifiableMap(map);
    }
}
