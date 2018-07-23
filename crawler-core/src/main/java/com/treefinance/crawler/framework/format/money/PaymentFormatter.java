package com.treefinance.crawler.framework.format.money;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.treefinance.crawler.framework.format.ConfigurableFormatter;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Jerry
 * @since 00:45 2018/6/2
 */
public class PaymentFormatter extends ConfigurableFormatter<Number> {

    private static final String NEGATE_FLAG       = "~";

    private static final String PAYMENT_NUM_REGEX = "([\\.,\\d]+)";

    @Override
    protected Number toFormat(@Nonnull String value, String pattern, Request request, Response response) throws Exception {
        String numPart = RegExp.group(value, PAYMENT_NUM_REGEX, 1);
        // replace ,to
        boolean needNegate = false;
        String actualPattern = StringUtils.trimToEmpty(pattern);
        if (StringUtils.isNotEmpty(actualPattern) && actualPattern.startsWith(NEGATE_FLAG)) {
            actualPattern = actualPattern.replaceFirst(NEGATE_FLAG, "");
            needNegate = true;
        }

        Map<String, PaymentUnit> paymentMapper = buildPaymentMapping(actualPattern);
        PaymentUnit unit = findPaymentUnit(paymentMapper, value);

        if (unit == null) {
            paymentMapper = PaymentUnitMapping.getPaymentUnitMap(getConf());
            unit = findPaymentUnit(paymentMapper, value);
        }

        if (unit == null) {
            logger.debug("can't find current payment conf! input: {}, numPart: {}, pattern: {}", value, numPart, pattern);
            unit = PaymentUnit.OUT;
        }
        // save to .00
        String temp = unit.getValue() + numPart;

        Number result = DecimalFormat.getInstance().parse(temp);

        if (needNegate) {
            result = 0 - result.doubleValue();
        }

        return result;
    }

    private PaymentUnit findPaymentUnit(Map<String, PaymentUnit> paymentMapper, String value) {
        PaymentUnit result = null;
        if (MapUtils.isNotEmpty(paymentMapper) && StringUtils.isNotEmpty(value)) {
            String val = value.toLowerCase();
            for (Map.Entry<String, PaymentUnit> entry : paymentMapper.entrySet()) {
                String pattern = entry.getKey();
                if (RegExp.find(val, pattern.toLowerCase())) {
                    result = entry.getValue();
                    logger.debug("find payment {} unit: {}", val, result);
                    break;
                }
            }
        }
        return result;
    }

    private Map<String, PaymentUnit> buildPaymentMapping(String pattern) {
        if (StringUtils.isNotEmpty(pattern)) {
            String[] values = pattern.split("###");

            if (values.length == 2) {
                Map<String, PaymentUnit> map = new HashMap<>();
                map.put(values[0], PaymentUnit.IN);
                map.put(values[1], PaymentUnit.OUT);
                return map;
            }
        }
        return Collections.emptyMap();
    }
}
