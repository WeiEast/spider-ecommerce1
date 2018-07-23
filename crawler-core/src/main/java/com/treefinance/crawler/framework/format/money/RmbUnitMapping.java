package com.treefinance.crawler.framework.format.money;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.Constants;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 02:04 2018/6/2
 */
public final class RmbUnitMapping {

    private static final Logger                                            LOGGER = LoggerFactory.getLogger(RmbUnitMapping.class);

    private static final LoadingCache<Configuration, Map<String, RMBUnit>> CACHE  = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES)
            .softValues().build(new CacheLoader<Configuration, Map<String, RMBUnit>>() {
                @Override
                public Map<String, RMBUnit> load(@Nonnull Configuration conf) throws Exception {
                    Map<String, RMBUnit> rmbMapper = new HashMap<>();
                    String config = conf.get(Constants.RMB_FROMAT_CONFIG, "{\"YUAN\":\"yuan|元\",\"JIAO\":\"角|jiao\",\"FEN\":\"分|fen\"}");
                    if (StringUtils.isNotEmpty(config)) {
                        Map<String, String> rmbMap = GsonUtils.fromJson(config, new TypeToken<LinkedHashMap<String, String>>() {}.getType());

                        rmbMap.forEach((unit, val) -> {
                            RMBUnit pu = null;
                            try {
                                pu = RMBUnit.valueOf(unit);
                            } catch (Exception e) {
                                LOGGER.error("rmb value error:" + e);
                            }
                            if (pu != null && StringUtils.isNotEmpty(val)) {
                                LOGGER.debug("rmb: {}, pattern: {}", pu, val);
                                rmbMapper.put(val, pu);
                            }
                        });
                    }

                    return rmbMapper;
                }
            });

    private RmbUnitMapping() {}

    public static Map<String, RMBUnit> getRMBUnitMap(Configuration conf) {
        Configuration configuration = conf;
        if (configuration == null) {
            configuration = PropertiesConfiguration.getInstance();
        }

        Map<String, RMBUnit> map = CACHE.getUnchecked(configuration);

        return Collections.unmodifiableMap(map);
    }
}
