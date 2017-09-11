package com.datatrees.crawler.core.processor.format.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.format.AbstractFormat;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyFormatImpl extends AbstractFormat {

    private static final Logger              log                = LoggerFactory.getLogger(CurrencyFormatImpl.class);
    private static final Map<String, String> defaultCurrencyMap = new LinkedHashMap<String, String>();
    private static       String              defaultCurrency    = "RMB";

    static {
        defaultCurrencyMap.put("美元|美金|\\$|＄|USD|U\\.S\\.", "USD");
        defaultCurrencyMap.put("欧元|€|EUR", "EUR");
        defaultCurrencyMap.put("港币|HKD", "HKD");
        defaultCurrencyMap.put("英镑|£|GBP", "GBP");
        defaultCurrencyMap.put("日元|日圆|日币|¥|JPY", "JPY");
        defaultCurrencyMap.put("法郎|CHF", "CHF");
        defaultCurrencyMap.put("新加坡元|SGD", "SGD");
        defaultCurrencyMap.put("加拿大元|CAD", "CAD");
        defaultCurrencyMap.put("澳大利亚元|AUD", "AUD");
        defaultCurrencyMap.put("新西兰元|NZD", "NZD");
        defaultCurrencyMap.put("泰铢|THB", "THB");
        defaultCurrencyMap.put("丹麦克朗|DKK", "DKK");
        defaultCurrencyMap.put("挪威克朗|NOK", "NOK");
        defaultCurrencyMap.put("瑞典克朗|SEK", "SEK");
        defaultCurrencyMap.put("澳门元|MOP", "MOP");
        defaultCurrencyMap.put("人民币|￥|RMB|CNY|元", "RMB");

        String json = PropertiesConfiguration.getInstance().get("custom.currency.map.json");
        if (StringUtils.isNotBlank(json)) {
            Map<String, String> newUnitMap = (Map<String, String>) GsonUtils.fromJson(json, Map.class);
            if (MapUtils.isNotEmpty(newUnitMap)) {
                defaultCurrencyMap.putAll(newUnitMap);
            }
        }
    }

    private Map<String, String> buildCustomCurrencyMap(String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            try {
                Map<String, String> newUnitMap = (Map<String, String>) GsonUtils.fromJson(pattern, Map.class);
                return newUnitMap;
            } catch (Exception e) {
                log.warn("buildCustomUnitMap error with input " + pattern);
            }
        }
        return null;
    }

    private String currencyMatch(String orginal, Map<String, String> unitMap) {
        if (MapUtils.isNotEmpty(unitMap)) {
            for (Map.Entry<String, String> entry : unitMap.entrySet()) {
                if (PatternUtils.match(entry.getKey(), orginal)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public Object format(Request req, Response response, String orginal, String pattern) {
        if (StringUtils.isEmpty(orginal)) {
            log.warn("orginal empty!");
            return null;
        }
        orginal = orginal.toUpperCase();
        Map<String, String> newUnitMap = this.buildCustomCurrencyMap(pattern);

        String result = this.currencyMatch(orginal, newUnitMap);
        if (result == null) {
            result = this.currencyMatch(orginal, defaultCurrencyMap);
        }
        return result != null ? result : defaultCurrency;
    }

    @Override
    public boolean isResultType(Object result) {
        if (result != null && result instanceof String) {
            return true;
        } else {
            return false;
        }
    }

}
