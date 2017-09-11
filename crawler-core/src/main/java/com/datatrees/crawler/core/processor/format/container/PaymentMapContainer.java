package com.datatrees.crawler.core.processor.format.container;

import java.util.*;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.format.unit.PaymentUnit;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentMapContainer {

    private static final Logger                   log           = LoggerFactory.getLogger(PaymentMapContainer.class);
    private static final Cache                    CACHE         = new Cache();
    private              Map<String, PaymentUnit> paymentMapper = new LinkedHashMap<String, PaymentUnit>();

    private PaymentMapContainer(Configuration conf) {
        String config = conf.get(Constants.PAYMENT_FROMAT_CONFIG, "{\"OUT\":\"支出|转出|欠款|\\\\+\",\"IN\":\"-|转入|存入|存款\"}");
        initMap(config, paymentMapper);
    }

    private PaymentMapContainer() {}

    public static PaymentMapContainer get(Configuration conf) {

        if (conf == null) {
            conf = PropertiesConfiguration.getInstance();
        }
        PaymentMapContainer container = CACHE.get(conf);
        return container;
    }

    private void initMap(String config, Map<String, PaymentUnit> paymentMapper) {
        if (StringUtils.isNotEmpty(config)) {
            try {
                @SuppressWarnings("unchecked") Map<String, String> paymentMap = (LinkedHashMap<String, String>) GsonUtils.fromJson(config, new TypeToken<LinkedHashMap<String, String>>() {}.getType());
                ListIterator<Map.Entry<String, String>> i = new ArrayList<Map.Entry<String, String>>(paymentMap.entrySet()).listIterator(paymentMap.size());
                while (i.hasPrevious()) {
                    Map.Entry<String, String> entry = i.previous();
                    String unit = entry.getKey();
                    String val = entry.getValue();
                    PaymentUnit pu = null;
                    try {
                        pu = PaymentUnit.valueOf(unit);
                    } catch (Exception e) {
                        log.error("payment value error:" + e);
                    }
                    if (pu != null && StringUtils.isNotEmpty(val)) {
                        paymentMapper.put(val, pu);
                        if (log.isDebugEnabled()) {
                            log.debug(String.format("payment %s , pattern %s", pu.name(), val));
                        }
                    }
                }

            } catch (Exception e) {
                log.error("payment json format error!", e);
            }
        }
    }

    public Map<String, PaymentUnit> getPaymentMapper(String config) {
        if (StringUtils.isBlank(config)) {
            return this.getPaymentMapper();
        } else {
            Map<String, PaymentUnit> customPaymentMapper = new LinkedHashMap<String, PaymentUnit>();
            this.initMap(config, customPaymentMapper);
            return customPaymentMapper;
        }
    }

    ;

    public Map<String, PaymentUnit> getPaymentMapper() {
        return Collections.unmodifiableMap(paymentMapper);
    }

    static class Cache {

        private static Map<Configuration, PaymentMapContainer> cache = new HashMap<Configuration, PaymentMapContainer>();

        public synchronized PaymentMapContainer get(Configuration conf) {
            PaymentMapContainer container = cache.get(conf);
            if (container == null) {
                container = new PaymentMapContainer(conf);
                cache.put(conf, container);
            }
            return container;
        }
    }
}
