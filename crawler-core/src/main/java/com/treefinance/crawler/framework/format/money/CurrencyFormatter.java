package com.treefinance.crawler.framework.format.money;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.google.gson.reflect.TypeToken;
import com.treefinance.crawler.framework.format.CommonFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 00:44 2018/6/2
 */
public class CurrencyFormatter extends CommonFormatter<String> {

    private static final Logger              log                  = LoggerFactory.getLogger(CurrencyFormatter.class);

    private static final Map<String, String> DEFAULT_CURRENCY_MAP = new HashMap<>();

    private static final String              DEFAULT_CURRENCY     = "RMB";

    static {
        DEFAULT_CURRENCY_MAP.put("美元|美金|\\$|＄|USD|U\\.S\\.", "USD");
        DEFAULT_CURRENCY_MAP.put("欧元|€|EUR", "EUR");
        DEFAULT_CURRENCY_MAP.put("港币|HKD", "HKD");
        DEFAULT_CURRENCY_MAP.put("英镑|£|GBP", "GBP");
        DEFAULT_CURRENCY_MAP.put("日元|日圆|日币|¥|JPY", "JPY");
        DEFAULT_CURRENCY_MAP.put("法郎|CHF", "CHF");
        DEFAULT_CURRENCY_MAP.put("新加坡元|SGD", "SGD");
        DEFAULT_CURRENCY_MAP.put("加拿大元|CAD", "CAD");
        DEFAULT_CURRENCY_MAP.put("澳大利亚元|AUD", "AUD");
        DEFAULT_CURRENCY_MAP.put("新西兰元|NZD", "NZD");
        DEFAULT_CURRENCY_MAP.put("泰铢|THB", "THB");
        DEFAULT_CURRENCY_MAP.put("丹麦克朗|DKK", "DKK");
        DEFAULT_CURRENCY_MAP.put("挪威克朗|NOK", "NOK");
        DEFAULT_CURRENCY_MAP.put("瑞典克朗|SEK", "SEK");
        DEFAULT_CURRENCY_MAP.put("澳门元|MOP", "MOP");
        DEFAULT_CURRENCY_MAP.put("人民币|￥|RMB|CNY|元", "RMB");
        DEFAULT_CURRENCY_MAP.put("印尼卢比|印尼盾|IDR", "IDR");

        String json = PropertiesConfiguration.getInstance().get("custom.currency.map.json");
        if (StringUtils.isNotBlank(json)) {
            Map<String, String> newUnitMap = buildFromJson(json);
            if (MapUtils.isNotEmpty(newUnitMap)) {
                merge(DEFAULT_CURRENCY_MAP, newUnitMap);
            }
        }

    }

    private static void merge(Map<String, String> unitMap, Map<String, String> newUnitMap) {
        newUnitMap.forEach((key, value) -> {
            if (StringUtils.isNotEmpty(key) && StringUtils.isNotBlank(value)) {
                unitMap.put(key.trim(), value.trim());
            }
        });
    }

    private static Map<String, String> buildFromJson(String json) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return GsonUtils.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
            } catch (Exception e) {
                log.warn("Error building currency unit mapping. >> {}", json, e);
            }
        }

        return Collections.emptyMap();
    }

    @Override
    protected String toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception {
        String val = value.toUpperCase();
        String actualPattern = config.trimmedPattern();

        Map<String, String> unitMap = ensureCurrencyMap(actualPattern);
        for (Map.Entry<String, String> entry : unitMap.entrySet()) {
            if (RegExp.find(val, entry.getKey())) {
                return entry.getValue();
            }
        }

        return DEFAULT_CURRENCY;
    }

    private Map<String, String> ensureCurrencyMap(String json) {
        Map<String, String> map = buildFromJson(json);

        if (MapUtils.isEmpty(map)) {
            return DEFAULT_CURRENCY_MAP;
        }

        Map<String, String> result = new HashMap<>(DEFAULT_CURRENCY_MAP);

        merge(result, map);

        return result;
    }
}
