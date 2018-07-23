package com.treefinance.crawler.framework.format.money;

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
 * @since 01:39 2018/6/2
 */
public final class PaymentUnitMapping {

    private static final Logger                                                LOGGER = LoggerFactory.getLogger(PaymentUnitMapping.class);

    private static final LoadingCache<Configuration, Map<String, PaymentUnit>> CACHE  = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES).softValues().build(new CacheLoader<Configuration, Map<String, PaymentUnit>>() {
                @Override
                public Map<String, PaymentUnit> load(@Nonnull Configuration conf) throws Exception {
                    Map<String, PaymentUnit> paymentMapper = new HashMap<>();
                    String config = conf.get(Constants.PAYMENT_FROMAT_CONFIG, "{\"OUT\":\"支出|转出|欠款|\\\\+\",\"IN\":\"-|转入|存入|存款\"}");
                    if (StringUtils.isNotEmpty(config)) {
                        Map<String, String> paymentMap = GsonUtils.fromJson(config, new TypeToken<HashMap<String, String>>() {}.getType());

                        paymentMap.forEach((key, value) -> {
                            PaymentUnit pu = null;
                            try {
                                pu = PaymentUnit.valueOf(key);
                            } catch (Exception e) {
                                LOGGER.error("payment value error", e);
                            }

                            if (pu != null && StringUtils.isNotEmpty(value)) {
                                LOGGER.debug("payment: {}, pattern: {}", pu, value);
                                paymentMapper.put(value, pu);
                            }
                        });
                    }

                    return paymentMapper;
                }
            });

    private PaymentUnitMapping() {
    }

    public static Map<String, PaymentUnit> getPaymentUnitMap(Configuration conf) {
        Configuration configuration = conf;

        if (configuration == null) {
            configuration = PropertiesConfiguration.getInstance();
        }

        Map<String, PaymentUnit> map = CACHE.getUnchecked(configuration);

        return Collections.unmodifiableMap(map);
    }
}
