package com.treefinance.crawler.framework.format.number;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Map;

import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.treefinance.crawler.framework.format.ConfigurableFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.MapUtils;

/**
 * @author Jerry
 * @since 00:46 2018/6/2
 */
public class NumberFormatter extends ConfigurableFormatter<Number> {

    @Override
    protected Number toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception {
        String val = value.replaceAll("\\s+", "");

        Map<String, NumberUnit> numberMap = RequestUtil.getNumberFormat(config.getRequest(), getConf());
        NumberUnit unit = findTimeUnitForNumber(numberMap, val);
        Number result = null;
        if (unit != null) {
            result = getNumber(val);
            if (result != null) {
                result = calcResult(result, unit);
            }
        }
        return result;
    }

    private Number calcResult(Number result, NumberUnit unit) {
        Double rs = null;
        try {
            rs = unit.getProportion() * result.doubleValue();
        } catch (Exception e) {
            // ignore
        }
        return rs;
    }

    private Number getNumber(String value) {
        Number result = null;
        try {
            result = DecimalFormat.getInstance().parse(value);
        } catch (Exception e) {
            logger.warn("parse Number error! - input: {}", value);
        }
        return result;
    }

    private NumberUnit findTimeUnitForNumber(Map<String, NumberUnit> numberMap, String value) {
        NumberUnit result = null;
        if (MapUtils.isNotEmpty(numberMap)) {
            for (Map.Entry<String, NumberUnit> entry : numberMap.entrySet()) {
                String pattern = entry.getKey();
                if (RegExp.find(value, pattern)) {
                    result = entry.getValue();
                    logger.debug("find period: {} unit : {}", value, result);
                    break;
                }

            }
        }

        if (result == null) {
            logger.debug("can't find correct number format  conf! " + value + "set to default!");
            result = NumberUnit.ONE;
        }
        return result;
    }

}
