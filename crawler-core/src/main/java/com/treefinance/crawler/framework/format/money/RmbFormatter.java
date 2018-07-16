package com.treefinance.crawler.framework.format.money;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import com.treefinance.crawler.framework.format.ConfigurableFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Jerry
 * @since 00:45 2018/6/2
 */
public class RmbFormatter extends ConfigurableFormatter<Number> {

    private static final String PAYMENT_NUM_REGEX = "([\\.,\\d]+)";

    @Override
    protected Number toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception {
        String numPart = RegExp.group(value, PAYMENT_NUM_REGEX, 1);

        String actualPattern = StringUtils.trim(config.getPattern());
        Map<String, RMBUnit> rmbMapper = buildRMPUnitMapping(actualPattern);
        RMBUnit unit = findRMBUnit(rmbMapper, value);

        if (unit == null) {
            rmbMapper = RmbUnitMapping.getRMBUnitMap(getConf());
            unit = findRMBUnit(rmbMapper, value);
        }

        if (unit == null) {
            logger.debug("can't find current RMB conf! input: {}, numPart: {}, pattern: {}", value, numPart, actualPattern);
            unit = RMBUnit.YUAN;
        }

        // save to .00
        double temp = Double.valueOf(numPart);

        return temp * unit.getConversion();
    }

    private RMBUnit findRMBUnit(Map<String, RMBUnit> unitMap, String value) {
        RMBUnit result = null;
        if (MapUtils.isNotEmpty(unitMap) && StringUtils.isNotEmpty(value)) {
            String input = value.toLowerCase();
            for (Map.Entry<String, RMBUnit> entry : unitMap.entrySet()) {
                String pattern = entry.getKey().toLowerCase();
                if (RegExp.find(input, pattern)) {
                    result = entry.getValue();
                    logger.debug("find RMB {} unit : {}", value, result);
                    break;
                }
            }
        }
        return result;
    }

    private Map<String, RMBUnit> buildRMPUnitMapping(String pattern) {
        if (StringUtils.isNotEmpty(pattern)) {
            String[] values = pattern.split("###");
            if (values.length == 3) {
                Map<String, RMBUnit> map = new HashMap<>();
                map.put(values[0], RMBUnit.YUAN);
                map.put(values[1], RMBUnit.JIAO);
                map.put(values[2], RMBUnit.FEN);
                return map;
            }
        }
        return null;
    }
}
